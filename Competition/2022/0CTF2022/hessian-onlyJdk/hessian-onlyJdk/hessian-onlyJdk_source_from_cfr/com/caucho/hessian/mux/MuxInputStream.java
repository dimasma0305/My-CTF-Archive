/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.mux;

import com.caucho.hessian.mux.MuxServer;
import java.io.IOException;
import java.io.InputStream;

public class MuxInputStream
extends InputStream {
    private MuxServer server;
    protected InputStream is;
    private int channel;
    private String url;
    private int chunkLength;

    protected void init(MuxServer server, int channel) throws IOException {
        this.server = server;
        this.channel = channel;
        this.url = null;
        this.chunkLength = 0;
    }

    protected InputStream getInputStream() throws IOException {
        if (this.is == null && this.server != null) {
            this.is = this.server.readChannel(this.channel);
        }
        return this.is;
    }

    void setInputStream(InputStream is) {
        this.is = is;
    }

    public int getChannel() {
        return this.channel;
    }

    public String getURL() {
        return this.url;
    }

    public int read() throws IOException {
        if (this.chunkLength <= 0) {
            this.readToData(false);
            if (this.chunkLength <= 0) {
                return -1;
            }
        }
        --this.chunkLength;
        return this.is.read();
    }

    public void close() throws IOException {
        this.skipToEnd();
    }

    private void skipToEnd() throws IOException {
        InputStream is = this.getInputStream();
        if (is == null) {
            return;
        }
        if (this.chunkLength > 0) {
            is.skip(this.chunkLength);
        }
        int tag = is.read();
        while (tag >= 0) {
            switch (tag) {
                case 89: {
                    this.server.freeReadLock();
                    this.is = is = this.server.readChannel(this.channel);
                    if (is != null) break;
                    this.server = null;
                    return;
                }
                case 81: {
                    this.server.freeReadLock();
                    this.is = null;
                    this.server = null;
                    return;
                }
                case -1: {
                    this.server.freeReadLock();
                    this.is = null;
                    this.server = null;
                    return;
                }
                default: {
                    int length = (is.read() << 8) + is.read();
                    is.skip(length);
                }
            }
            tag = is.read();
        }
    }

    void readToData(boolean returnOnYield) throws IOException {
        InputStream is = this.getInputStream();
        if (is == null) {
            return;
        }
        int tag = is.read();
        while (tag >= 0) {
            switch (tag) {
                case 89: {
                    this.server.freeReadLock();
                    if (returnOnYield) {
                        return;
                    }
                    this.server.readChannel(this.channel);
                    break;
                }
                case 81: {
                    this.server.freeReadLock();
                    this.is = null;
                    this.server = null;
                    return;
                }
                case 85: {
                    this.url = this.readUTF();
                    break;
                }
                case 68: {
                    this.chunkLength = (is.read() << 8) + is.read();
                    return;
                }
                default: {
                    this.readTag(tag);
                }
            }
            tag = is.read();
        }
    }

    protected void readTag(int tag) throws IOException {
        int length = (this.is.read() << 8) + this.is.read();
        this.is.skip(length);
    }

    protected String readUTF() throws IOException {
        int len = (this.is.read() << 8) + this.is.read();
        StringBuffer sb = new StringBuffer();
        while (len > 0) {
            int d1 = this.is.read();
            if (d1 < 0) {
                return sb.toString();
            }
            if (d1 < 128) {
                --len;
                sb.append((char)d1);
                continue;
            }
            if ((d1 & 0xE0) == 192) {
                len -= 2;
                sb.append(((d1 & 0x1F) << 6) + (this.is.read() & 0x3F));
                continue;
            }
            if ((d1 & 0xF0) == 224) {
                len -= 3;
                sb.append(((d1 & 0xF) << 12) + ((this.is.read() & 0x3F) << 6) + (this.is.read() & 0x3F));
                continue;
            }
            throw new IOException("utf-8 encoding error");
        }
        return sb.toString();
    }
}

