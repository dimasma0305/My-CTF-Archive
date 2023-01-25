/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianEnvelope;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Deflation
extends HessianEnvelope {
    public Hessian2Output wrap(Hessian2Output out) throws IOException {
        DeflateOutputStream os = new DeflateOutputStream(out);
        Hessian2Output filterOut = new Hessian2Output(os);
        filterOut.setCloseStreamOnClose(true);
        return filterOut;
    }

    public Hessian2Input unwrap(Hessian2Input in) throws IOException {
        int version = in.readEnvelope();
        String method = in.readMethod();
        if (!method.equals(this.getClass().getName())) {
            throw new IOException("expected hessian Envelope method '" + this.getClass().getName() + "' at '" + method + "'");
        }
        return this.unwrapHeaders(in);
    }

    public Hessian2Input unwrapHeaders(Hessian2Input in) throws IOException {
        DeflateInputStream is = new DeflateInputStream(in);
        Hessian2Input filter = new Hessian2Input(is);
        filter.setCloseStreamOnClose(true);
        return filter;
    }

    static class DeflateInputStream
    extends InputStream {
        private Hessian2Input _in;
        private InputStream _bodyIn;
        private InflaterInputStream _inflateIn;

        DeflateInputStream(Hessian2Input in) throws IOException {
            this._in = in;
            int len = in.readInt();
            if (len != 0) {
                throw new IOException("expected no headers");
            }
            this._bodyIn = this._in.readInputStream();
            this._inflateIn = new InflaterInputStream(this._bodyIn);
        }

        public int read() throws IOException {
            return this._inflateIn.read();
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            return this._inflateIn.read(buffer, offset, length);
        }

        public void close() throws IOException {
            Hessian2Input in = this._in;
            this._in = null;
            if (in != null) {
                this._inflateIn.close();
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

    static class DeflateOutputStream
    extends OutputStream {
        private Hessian2Output _out;
        private OutputStream _bodyOut;
        private DeflaterOutputStream _deflateOut;

        DeflateOutputStream(Hessian2Output out) throws IOException {
            this._out = out;
            this._out.startEnvelope(Deflation.class.getName());
            this._out.writeInt(0);
            this._bodyOut = this._out.getBytesOutputStream();
            this._deflateOut = new DeflaterOutputStream(this._bodyOut);
        }

        public void write(int ch) throws IOException {
            this._deflateOut.write(ch);
        }

        public void write(byte[] buffer, int offset, int length) throws IOException {
            this._deflateOut.write(buffer, offset, length);
        }

        public void close() throws IOException {
            Hessian2Output out = this._out;
            this._out = null;
            if (out != null) {
                this._deflateOut.close();
                this._bodyOut.close();
                out.writeInt(0);
                out.completeEnvelope();
                out.close();
            }
        }
    }
}

