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
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class X509Encryption
extends HessianEnvelope {
    private String _algorithm = "AES";
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

    public void setPrivateKey(PrivateKey privateKey) {
        this._privateKey = privateKey;
    }

    public SecureRandom getSecureRandom() {
        return this._secureRandom;
    }

    public void setSecureRandom(SecureRandom random) {
        this._secureRandom = random;
    }

    public Hessian2Output wrap(Hessian2Output out) throws IOException {
        if (this._cert == null) {
            throw new IOException("X509Encryption.wrap requires a certificate");
        }
        EncryptOutputStream os = new EncryptOutputStream(out);
        Hessian2Output filterOut = new Hessian2Output(os);
        filterOut.setCloseStreamOnClose(true);
        return filterOut;
    }

    public Hessian2Input unwrap(Hessian2Input in) throws IOException {
        if (this._privateKey == null) {
            throw new IOException("X509Encryption.unwrap requires a private key");
        }
        if (this._cert == null) {
            throw new IOException("X509Encryption.unwrap requires a certificate");
        }
        int version = in.readEnvelope();
        String method = in.readMethod();
        if (!method.equals(this.getClass().getName())) {
            throw new IOException("expected hessian Envelope method '" + this.getClass().getName() + "' at '" + method + "'");
        }
        return this.unwrapHeaders(in);
    }

    public Hessian2Input unwrapHeaders(Hessian2Input in) throws IOException {
        if (this._privateKey == null) {
            throw new IOException("X509Encryption.unwrap requires a private key");
        }
        if (this._cert == null) {
            throw new IOException("X509Encryption.unwrap requires a certificate");
        }
        EncryptInputStream is = new EncryptInputStream(in);
        Hessian2Input filter = new Hessian2Input(is);
        filter.setCloseStreamOnClose(true);
        return filter;
    }

    class EncryptInputStream
    extends InputStream {
        private Hessian2Input _in;
        private Cipher _cipher;
        private InputStream _bodyIn;
        private CipherInputStream _cipherIn;

        EncryptInputStream(Hessian2Input in) throws IOException {
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
                keyCipher.init(4, X509Encryption.this._privateKey);
                Key key = keyCipher.unwrap(encKey, algorithm, 3);
                this._bodyIn = this._in.readInputStream();
                this._cipher = Cipher.getInstance(algorithm);
                this._cipher.init(2, key);
                this._cipherIn = new CipherInputStream(this._bodyIn, this._cipher);
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
            return this._cipherIn.read();
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            return this._cipherIn.read(buffer, offset, length);
        }

        public void close() throws IOException {
            Hessian2Input in = this._in;
            this._in = null;
            if (in != null) {
                this._cipherIn.close();
                this._bodyIn.close();
                int len = in.readInt();
                if (len != 0) {
                    throw new IOException("Unexpected footer");
                }
                in.completeEnvelope();
                in.close();
            }
        }
    }

    class EncryptOutputStream
    extends OutputStream {
        private Hessian2Output _out;
        private Cipher _cipher;
        private OutputStream _bodyOut;
        private CipherOutputStream _cipherOut;

        EncryptOutputStream(Hessian2Output out) throws IOException {
            try {
                this._out = out;
                KeyGenerator keyGen = KeyGenerator.getInstance(X509Encryption.this._algorithm);
                if (X509Encryption.this._secureRandom != null) {
                    keyGen.init(X509Encryption.this._secureRandom);
                }
                SecretKey sharedKey = keyGen.generateKey();
                this._out = out;
                this._out.startEnvelope(X509Encryption.class.getName());
                PublicKey publicKey = X509Encryption.this._cert.getPublicKey();
                byte[] encoded = publicKey.getEncoded();
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(encoded);
                byte[] fingerprint = md.digest();
                String keyAlgorithm = publicKey.getAlgorithm();
                Cipher keyCipher = Cipher.getInstance(keyAlgorithm);
                if (X509Encryption.this._secureRandom != null) {
                    keyCipher.init(3, X509Encryption.this._cert, X509Encryption.this._secureRandom);
                } else {
                    keyCipher.init(3, X509Encryption.this._cert);
                }
                byte[] encKey = keyCipher.wrap(sharedKey);
                this._out.writeInt(4);
                this._out.writeString("algorithm");
                this._out.writeString(X509Encryption.this._algorithm);
                this._out.writeString("fingerprint");
                this._out.writeBytes(fingerprint);
                this._out.writeString("key-algorithm");
                this._out.writeString(keyAlgorithm);
                this._out.writeString("key");
                this._out.writeBytes(encKey);
                this._bodyOut = this._out.getBytesOutputStream();
                this._cipher = Cipher.getInstance(X509Encryption.this._algorithm);
                if (X509Encryption.this._secureRandom != null) {
                    this._cipher.init(1, (Key)sharedKey, X509Encryption.this._secureRandom);
                } else {
                    this._cipher.init(1, sharedKey);
                }
                this._cipherOut = new CipherOutputStream(this._bodyOut, this._cipher);
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
            this._cipherOut.write(ch);
        }

        public void write(byte[] buffer, int offset, int length) throws IOException {
            this._cipherOut.write(buffer, offset, length);
        }

        public void close() throws IOException {
            Hessian2Output out = this._out;
            this._out = null;
            if (out != null) {
                this._cipherOut.close();
                this._bodyOut.close();
                out.writeInt(0);
                out.completeEnvelope();
                out.close();
            }
        }
    }
}

