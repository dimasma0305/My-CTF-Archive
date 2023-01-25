/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.SerializerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractHessianOutput {
    private SerializerFactory _defaultSerializerFactory;
    protected SerializerFactory _serializerFactory;
    private byte[] _byteBuffer;

    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    public SerializerFactory getSerializerFactory() {
        if (this._serializerFactory == this._defaultSerializerFactory) {
            this._serializerFactory = new SerializerFactory();
        }
        return this._serializerFactory;
    }

    protected final SerializerFactory findSerializerFactory() {
        SerializerFactory factory = this._serializerFactory;
        if (factory == null) {
            this._defaultSerializerFactory = factory = SerializerFactory.createDefault();
            this._serializerFactory = factory;
        }
        return factory;
    }

    public void init(OutputStream os) {
    }

    public boolean setUnshared(boolean isUnshared) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public void call(String method, Object[] args) throws IOException {
        int length = args != null ? args.length : 0;
        this.startCall(method, length);
        for (int i = 0; i < length; ++i) {
            this.writeObject(args[i]);
        }
        this.completeCall();
    }

    public abstract void startCall() throws IOException;

    public abstract void startCall(String var1, int var2) throws IOException;

    public void writeHeader(String name) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName());
    }

    public abstract void writeMethod(String var1) throws IOException;

    public abstract void completeCall() throws IOException;

    public abstract void writeBoolean(boolean var1) throws IOException;

    public abstract void writeInt(int var1) throws IOException;

    public abstract void writeLong(long var1) throws IOException;

    public abstract void writeDouble(double var1) throws IOException;

    public abstract void writeUTCDate(long var1) throws IOException;

    public abstract void writeNull() throws IOException;

    public abstract void writeString(String var1) throws IOException;

    public abstract void writeString(char[] var1, int var2, int var3) throws IOException;

    public abstract void writeBytes(byte[] var1) throws IOException;

    public abstract void writeBytes(byte[] var1, int var2, int var3) throws IOException;

    public abstract void writeByteBufferStart() throws IOException;

    public abstract void writeByteBufferPart(byte[] var1, int var2, int var3) throws IOException;

    public abstract void writeByteBufferEnd(byte[] var1, int var2, int var3) throws IOException;

    public void writeByteStream(InputStream is) throws IOException {
        int len;
        this.writeByteBufferStart();
        if (this._byteBuffer == null) {
            this._byteBuffer = new byte[1024];
        }
        byte[] buffer = this._byteBuffer;
        while ((len = is.read(buffer, 0, buffer.length)) > 0) {
            if (len < buffer.length) {
                int len2 = is.read(buffer, len, buffer.length - len);
                if (len2 < 0) {
                    this.writeByteBufferEnd(buffer, 0, len);
                    return;
                }
                len += len2;
            }
            this.writeByteBufferPart(buffer, 0, len);
        }
        this.writeByteBufferEnd(buffer, 0, 0);
    }

    protected abstract void writeRef(int var1) throws IOException;

    public boolean removeRef(Object obj) throws IOException {
        return false;
    }

    public abstract boolean replaceRef(Object var1, Object var2) throws IOException;

    public abstract boolean addRef(Object var1) throws IOException;

    public abstract int getRef(Object var1);

    public void resetReferences() {
    }

    public abstract void writeObject(Object var1) throws IOException;

    public abstract boolean writeListBegin(int var1, String var2) throws IOException;

    public abstract void writeListEnd() throws IOException;

    public abstract void writeMapBegin(String var1) throws IOException;

    public abstract void writeMapEnd() throws IOException;

    public int writeObjectBegin(String type) throws IOException {
        this.writeMapBegin(type);
        return -2;
    }

    public void writeClassFieldLength(int len) throws IOException {
    }

    public void writeObjectEnd() throws IOException {
    }

    public void writeReply(Object o) throws IOException {
        this.startReply();
        this.writeObject(o);
        this.completeReply();
    }

    public void startReply() throws IOException {
    }

    public void completeReply() throws IOException {
    }

    public void writeFault(String code, String message, Object detail) throws IOException {
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }
}

