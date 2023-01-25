/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.micro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class MicroHessianInput {
    protected InputStream is;

    public MicroHessianInput(InputStream is) {
        this.init(is);
    }

    public MicroHessianInput() {
    }

    public void init(InputStream is) {
        this.is = is;
    }

    public void startReply() throws IOException {
        int tag = this.is.read();
        if (tag != 114) {
            this.protocolException("expected hessian reply");
        }
        int major = this.is.read();
        int minor = this.is.read();
    }

    public void completeReply() throws IOException {
        int tag = this.is.read();
        if (tag != 122) {
            this.protocolException("expected end of reply");
        }
    }

    public boolean readBoolean() throws IOException {
        int tag = this.is.read();
        switch (tag) {
            case 84: {
                return true;
            }
            case 70: {
                return false;
            }
        }
        throw this.expect("boolean", tag);
    }

    public int readInt() throws IOException {
        int tag = this.is.read();
        if (tag != 73) {
            throw this.expect("integer", tag);
        }
        int b32 = this.is.read();
        int b24 = this.is.read();
        int b16 = this.is.read();
        int b8 = this.is.read();
        return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    public long readLong() throws IOException {
        int tag = this.is.read();
        if (tag != 76) {
            throw this.protocolException("expected long");
        }
        long b64 = this.is.read();
        long b56 = this.is.read();
        long b48 = this.is.read();
        long b40 = this.is.read();
        long b32 = this.is.read();
        long b24 = this.is.read();
        long b16 = this.is.read();
        long b8 = this.is.read();
        return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    public long readUTCDate() throws IOException {
        int tag = this.is.read();
        if (tag != 100) {
            throw this.protocolException("expected date");
        }
        long b64 = this.is.read();
        long b56 = this.is.read();
        long b48 = this.is.read();
        long b40 = this.is.read();
        long b32 = this.is.read();
        long b24 = this.is.read();
        long b16 = this.is.read();
        long b8 = this.is.read();
        return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    public String readString() throws IOException {
        int tag = this.is.read();
        if (tag == 78) {
            return null;
        }
        if (tag != 83) {
            throw this.expect("string", tag);
        }
        int b16 = this.is.read();
        int b8 = this.is.read();
        int len = (b16 << 8) + b8;
        return this.readStringImpl(len);
    }

    public byte[] readBytes() throws IOException {
        int tag = this.is.read();
        if (tag == 78) {
            return null;
        }
        if (tag != 66) {
            throw this.expect("bytes", tag);
        }
        int b16 = this.is.read();
        int b8 = this.is.read();
        int len = (b16 << 8) + b8;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (int i = 0; i < len; ++i) {
            bos.write(this.is.read());
        }
        return bos.toByteArray();
    }

    public Object readObject(Class expectedClass) throws IOException {
        int tag = this.is.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 84: {
                return new Boolean(true);
            }
            case 70: {
                return new Boolean(false);
            }
            case 73: {
                int b32 = this.is.read();
                int b24 = this.is.read();
                int b16 = this.is.read();
                int b8 = this.is.read();
                return new Integer((b32 << 24) + (b24 << 16) + (b16 << 8) + b8);
            }
            case 76: {
                long b64 = this.is.read();
                long b56 = this.is.read();
                long b48 = this.is.read();
                long b40 = this.is.read();
                long b32 = this.is.read();
                long b24 = this.is.read();
                long b16 = this.is.read();
                long b8 = this.is.read();
                return new Long((b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8);
            }
            case 100: {
                long b64 = this.is.read();
                long b56 = this.is.read();
                long b48 = this.is.read();
                long b40 = this.is.read();
                long b32 = this.is.read();
                long b24 = this.is.read();
                long b16 = this.is.read();
                long b8 = this.is.read();
                return new Date((b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8);
            }
            case 83: 
            case 88: {
                int b16 = this.is.read();
                int b8 = this.is.read();
                int len = (b16 << 8) + b8;
                return this.readStringImpl(len);
            }
            case 66: {
                if (tag != 66) {
                    throw this.expect("bytes", tag);
                }
                int b16 = this.is.read();
                int b8 = this.is.read();
                int len = (b16 << 8) + b8;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                for (int i = 0; i < len; ++i) {
                    bos.write(this.is.read());
                }
                return bos.toByteArray();
            }
        }
        throw new IOException("unknown code:" + (char)tag);
    }

    protected String readStringImpl(int length) throws IOException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int ch1;
            int ch = this.is.read();
            if (ch < 128) {
                sb.append((char)ch);
                continue;
            }
            if ((ch & 0xE0) == 192) {
                ch1 = this.is.read();
                int v = ((ch & 0x1F) << 6) + (ch1 & 0x3F);
                sb.append((char)v);
                continue;
            }
            if ((ch & 0xF0) == 224) {
                ch1 = this.is.read();
                int ch2 = this.is.read();
                int v = ((ch & 0xF) << 12) + ((ch1 & 0x3F) << 6) + (ch2 & 0x3F);
                sb.append((char)v);
                continue;
            }
            throw new IOException("bad utf-8 encoding");
        }
        return sb.toString();
    }

    protected IOException expect(String expect, int ch) {
        if (ch < 0) {
            return this.protocolException("expected " + expect + " at end of file");
        }
        return this.protocolException("expected " + expect + " at " + (char)ch);
    }

    protected IOException protocolException(String message) {
        return new IOException(message);
    }
}

