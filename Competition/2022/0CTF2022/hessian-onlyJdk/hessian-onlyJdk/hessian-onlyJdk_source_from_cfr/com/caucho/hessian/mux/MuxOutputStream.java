/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.mux;

import com.caucho.hessian.mux.MuxServer;
import java.io.IOException;
import java.io.OutputStream;

public class MuxOutputStream
extends OutputStream {
    private MuxServer server;
    private int channel;
    private OutputStream os;

    protected void init(MuxServer server, int channel) throws IOException {
        this.server = server;
        this.channel = channel;
        this.os = null;
    }

    protected OutputStream getOutputStream() throws IOException {
        if (this.os == null && this.server != null) {
            this.os = this.server.writeChannel(this.channel);
        }
        return this.os;
    }

    public int getChannel() {
        return this.channel;
    }

    public void writeURL(String url) throws IOException {
        this.writeUTF(85, url);
    }

    public void write(int ch) throws IOException {
        OutputStream os = this.getOutputStream();
        os.write(68);
        os.write(0);
        os.write(1);
        os.write(ch);
    }

    public void write(byte[] buffer, int offset, int length) throws IOException {
        OutputStream os = this.getOutputStream();
        while (length > 32768) {
            os.write(68);
            os.write(128);
            os.write(0);
            os.write(buffer, offset, 32768);
            offset += 32768;
            length -= 32768;
        }
        os.write(68);
        os.write(length >> 8);
        os.write(length);
        os.write(buffer, offset, length);
    }

    public void yield() throws IOException {
        OutputStream os = this.os;
        this.os = null;
        if (os != null) {
            this.server.yield(this.channel);
        }
    }

    public void flush() throws IOException {
        OutputStream os = this.os;
        this.os = null;
        if (os != null) {
            this.server.flush(this.channel);
        }
    }

    public void close() throws IOException {
        if (this.server != null) {
            OutputStream os = this.getOutputStream();
            this.os = null;
            MuxServer server = this.server;
            this.server = null;
            server.close(this.channel);
        }
    }

    protected void writeUTF(int code, String string) throws IOException {
        char ch;
        int i;
        OutputStream os = this.getOutputStream();
        os.write(code);
        int charLength = string.length();
        int length = 0;
        for (i = 0; i < charLength; ++i) {
            ch = string.charAt(i);
            if (ch < '\u0080') {
                ++length;
                continue;
            }
            if (ch < '\u0800') {
                length += 2;
                continue;
            }
            length += 3;
        }
        os.write(length >> 8);
        os.write(length);
        for (i = 0; i < length; ++i) {
            ch = string.charAt(i);
            if (ch < '\u0080') {
                os.write(ch);
                continue;
            }
            if (ch < '\u0800') {
                os.write(192 + (ch >> 6) & 0x1F);
                os.write(128 + (ch & 0x3F));
                continue;
            }
            os.write(224 + (ch >> 12) & 0xF);
            os.write(128 + (ch >> 6 & 0x3F));
            os.write(128 + (ch & 0x3F));
        }
    }
}

