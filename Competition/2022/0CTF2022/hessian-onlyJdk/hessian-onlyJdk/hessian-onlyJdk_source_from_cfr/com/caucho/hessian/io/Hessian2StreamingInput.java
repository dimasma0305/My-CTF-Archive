/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.SerializerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hessian2StreamingInput {
    private static final Logger log = Logger.getLogger(Hessian2StreamingInput.class.getName());
    private StreamingInputStream _is;
    private Hessian2Input _in;

    public Hessian2StreamingInput(InputStream is) {
        this._is = new StreamingInputStream(is);
        this._in = new Hessian2Input(this._is);
    }

    public void setSerializerFactory(SerializerFactory factory) {
        this._in.setSerializerFactory(factory);
    }

    public boolean isDataAvailable() {
        StreamingInputStream is = this._is;
        return is != null && is.isDataAvailable();
    }

    public Hessian2Input startPacket() throws IOException {
        if (this._is.startPacket()) {
            this._in.resetReferences();
            this._in.resetBuffer();
            return this._in;
        }
        return null;
    }

    public void endPacket() throws IOException {
        this._is.endPacket();
        this._in.resetBuffer();
    }

    public Hessian2Input getHessianInput() {
        return this._in;
    }

    public Object readObject() throws IOException {
        this._is.startPacket();
        Object obj = this._in.readStreamingObject();
        this._is.endPacket();
        return obj;
    }

    public void close() throws IOException {
        this._in.close();
    }

    static class StreamingInputStream
    extends InputStream {
        private InputStream _is;
        private int _length;
        private boolean _isPacketEnd;

        StreamingInputStream(InputStream is) {
            this._is = is;
        }

        public boolean isDataAvailable() {
            try {
                return this._is != null && this._is.available() > 0;
            }
            catch (IOException e) {
                log.log(Level.FINER, e.toString(), e);
                return true;
            }
        }

        public boolean startPacket() throws IOException {
            do {
                this._isPacketEnd = false;
            } while ((this._length = this.readChunkLength(this._is)) == 0);
            return this._length > 0;
        }

        public void endPacket() throws IOException {
            while (!this._isPacketEnd) {
                if (this._length <= 0) {
                    this._length = this.readChunkLength(this._is);
                }
                if (this._length <= 0) continue;
                this._is.skip(this._length);
                this._length = 0;
            }
            if (this._length > 0) {
                this._is.skip(this._length);
                this._length = 0;
            }
        }

        public int read() throws IOException {
            InputStream is = this._is;
            if (this._length == 0) {
                if (this._isPacketEnd) {
                    return -1;
                }
                this._length = this.readChunkLength(is);
                if (this._length <= 0) {
                    return -1;
                }
            }
            --this._length;
            return is.read();
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            int sublen;
            InputStream is = this._is;
            if (this._length <= 0) {
                if (this._isPacketEnd) {
                    return -1;
                }
                this._length = this.readChunkLength(is);
                if (this._length <= 0) {
                    return -1;
                }
            }
            if (length < (sublen = this._length)) {
                sublen = length;
            }
            if ((sublen = is.read(buffer, offset, sublen)) < 0) {
                return -1;
            }
            this._length -= sublen;
            return sublen;
        }

        private int readChunkLength(InputStream is) throws IOException {
            if (this._isPacketEnd) {
                return -1;
            }
            int length = 0;
            int code = is.read();
            if (code < 0) {
                this._isPacketEnd = true;
                return -1;
            }
            this._isPacketEnd = (code & 0x80) == 0;
            int len = is.read() & 0x7F;
            length = len < 126 ? len : (len == 126 ? ((is.read() & 0xFF) << 8) + (is.read() & 0xFF) : ((is.read() & 0xFF) << 56) + ((is.read() & 0xFF) << 48) + ((is.read() & 0xFF) << 40) + ((is.read() & 0xFF) << 32) + ((is.read() & 0xFF) << 24) + ((is.read() & 0xFF) << 16) + ((is.read() & 0xFF) << 8) + (is.read() & 0xFF));
            return length;
        }
    }
}

