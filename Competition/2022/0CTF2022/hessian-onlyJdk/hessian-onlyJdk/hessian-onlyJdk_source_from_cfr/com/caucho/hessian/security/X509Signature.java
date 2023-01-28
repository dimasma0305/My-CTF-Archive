/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.security;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianEnvelope;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class X509Signature
extends HessianEnvelope {
    private String _algorithm = "HmacSHA256";
    private X509Certificate _cert;
    private PrivateKey _privateKey;
    private SecureRandom _secureRandom;

    public void setAlgorithm(String algorithm) {
        if (algorithm == null) {
            throw new NullPointerException();
        }
        this._algorithm = algorithm;
    }

    public String getAlgorithm() {
        return this._algorithm;
    }

    public X509Certificate getCertificate() {
        return this._cert;
    }

    public void setCertificate(X509Certificate cert) {
        this._cert = cert;
    }

    public PrivateKey getPrivateKey() {
        return this._privateKey;
    }

    public void setPrivateKey(PrivateKey key) {
        this._privateKey = key;
    }

    public SecureRandom getSecureRandom() {
        return this._secureRandom;
    }

    public void setSecureRandom(SecureRandom random) {
        this._secureRandom = random;
    }

    public Hessian2Output wrap(Hessian2Output out) throws IOException {
        if (this._privateKey == null) {
            throw new IOException("X509Signature.wrap requires a private key");
        }
        if (this._cert == null) {
            throw new IOException("X509Signature.wrap requires a certificate");
        }
        SignatureOutputStream os = new SignatureOutputStream(out);
        Hessian2Output filterOut = new Hessian2Output(os);
        filterOut.setCloseStreamOnClose(true);
        return filterOut;
    }

    public Hessian2Input unwrap(Hessian2Input in) throws IOException {
        if (this._cert == null) {
            throw new IOException("X509Signature.unwrap requires a certificate");
        }
        int version = in.readEnvelope();
        String method = in.readMethod();
        if (!method.equals(this.getClass().getName())) {
            throw new IOException("expected hessian Envelope method '" + this.getClass().getName() + "' at '" + method + "'");
        }
        return this.unwrapHeaders(in);
    }

    public Hessian2Input unwrapHeaders(Hessian2Input in) throws IOException {
        if (this._cert == null) {
            throw new IOException("X509Signature.unwrap requires a certificate");
        }
        SignatureInputStream is = new SignatureInputStream(in);
        Hessian2Input filter = new Hessian2Input(is);
        filter.setCloseStreamOnClose(true);
        return filter;
    }

    class SignatureInputStream
    extends InputStream {
        private Hessian2Input _in;
        private Mac _mac;
        private InputStream _bodyIn;
        private CipherInputStream _cipherIn;

        SignatureInputStream(Hessian2Input in) throws IOException {
            try {
                this._in = in;
                byte[] fingerprint = null;
                String keyAlgorithm = null;
                String algorithm = null;
                byte[] encKey = null;
                int len = in.readInt();
                for (int i = 0; i < len; ++i) {
                    String header = in.readString();
                    if ("fingerprint".equals(header)) {
                        fingerprint = in.readBytes();
                        continue;
                    }
                    if ("key-algorithm".equals(header)) {
                        keyAlgorithm = in.readString();
                        continue;
                    }
                    if ("algorithm".equals(header)) {
                        algorithm = in.readString();
                        continue;
                    }
                    if ("key".equals(header)) {
                        encKey = in.readBytes();
                        continue;
                    }
                    throw new IOException("'" + header + "' is an unexpected header");
                }
                Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
                keyCipher.init(4, X509Signature.this._cert);
                Key key = keyCipher.unwrap(encKey, algorithm, 3);
                this._bodyIn = this._in.readInputStream();
                this._mac = Mac.getInstance(algorithm);
                this._mac.init(key);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public int read() throws IOException {
            int ch = this._bodyIn.read();
            if (ch < 0) {
                return ch;
            }
            this._mac.update((byte)ch);
            return ch;
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            int len = this._bodyIn.read(buffer, offset, length);
            if (len < 0) {
                return len;
            }
            this._mac.update(buffer, offset, len);
            return len;
        }

        public void close() throws IOException {
            Hessian2Input in = this._in;
            this._in = null;
            if (in != null) {
                this._bodyIn.close();
                int len = in.readInt();
                byte[] signature = null;
                for (int i = 0; i < len; ++i) {
                    String header = in.readString();
                    if (!"signature".equals(header)) continue;
                    signature = in.readBytes();
                }
                in.completeEnvelope();
                in.close();
                if (signature == null) {
                    throw new IOException("Expected signature");
                }
                byte[] sig = this._mac.doFinal();
                if (sig.length != signature.length) {
                    throw new IOException("mismatched signature");
                }
                for (int i = 0; i < sig.length; ++i) {
                    if (signature[i] == sig[i]) continue;
                    throw new IOException("mismatched signature");
                }
            }
        }
    }

    class SignatureOutputStream
    extends OutputStream {
        private Hessian2Output _out;
        private OutputStream _bodyOut;
        private Mac _mac;

        SignatureOutputStream(Hessian2Output out) throws IOException {
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance(X509Signature.this._algorithm);
                if (X509Signature.this._secureRandom != null) {
                    keyGen.init(X509Signature.this._secureRandom);
                }
                SecretKey sharedKey = keyGen.generateKey();
                this._out = out;
                this._out.startEnvelope(X509Signature.class.getName());
                PublicKey publicKey = X509Signature.this._cert.getPublicKey();
                byte[] encoded = publicKey.getEncoded();
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(encoded);
                byte[] fingerprint = md.digest();
                String keyAlgorithm = X509Signature.this._privateKey.getAlgorithm();
                Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
                keyCipher.init(3, X509Signature.this._privateKey);
                byte[] encKey = keyCipher.wrap(sharedKey);
                this._out.writeInt(4);
                this._out.writeString("algorithm");
                this._out.writeString(X509Signature.this._algorithm);
                this._out.writeString("fingerprint");
                this._out.writeBytes(fingerprint);
                this._out.writeString("key-algorithm");
                this._out.writeString(keyAlgorithm);
                this._out.writeString("key");
                this._out.writeBytes(encKey);
                this._mac = Mac.getInstance(X509Signature.this._algorithm);
                this._mac.init(sharedKey);
                this._bodyOut = this._out.getBytesOutputStream();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (IOException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void write(int ch) throws IOException {
            this._bodyOut.write(ch);
            this._mac.update((byte)ch);
        }

        public void write(byte[] buffer, int offset, int length) throws IOException {
            this._bodyOut.write(buffer, offset, length);
            this._mac.update(buffer, offset, length);
        }

        public void close() throws IOException {
            Hessian2Output out = this._out;
            this._out = null;
            if (out == null) {
                return;
            }
            this._bodyOut.close();
            byte[] sig = this._mac.doFinal();
            out.writeInt(1);
            out.writeString("signature");
            out.writeBytes(sig);
            out.completeEnvelope();
            out.close();
        }
    }
}

