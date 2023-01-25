/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.mux;

import com.caucho.hessian.mux.MuxInputStream;
import com.caucho.hessian.mux.MuxOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MuxServer {
    private Object READ_LOCK = new Object();
    private Object WRITE_LOCK = new Object();
    private InputStream is;
    private OutputStream os;
    private boolean isClient;
    private transient boolean isClosed;
    private boolean[] inputReady = new boolean[4];
    private boolean isReadLocked;
    private boolean isWriteLocked;

    public MuxServer() {
    }

    public MuxServer(InputStream is, OutputStream os, boolean isClient) {
        this.init(is, os, isClient);
    }

    public void init(InputStream is, OutputStream os, boolean isClient) {
        this.is = is;
        this.os = os;
        this.isClient = isClient;
    }

    public InputStream getInputStream() {
        return this.is;
    }

    public OutputStream getOutputStream() {
        return this.os;
    }

    public boolean startCall(MuxInputStream in, MuxOutputStream out) throws IOException {
        int channel = this.isClient ? 2 : 3;
        return this.startCall(channel, in, out);
    }

    public boolean startCall(int channel, MuxInputStream in, MuxOutputStream out) throws IOException {
        in.init(this, channel);
        out.init(this, channel);
        return true;
    }

    public boolean readRequest(MuxInputStream in, MuxOutputStream out) throws IOException {
        int channel = this.isClient ? 3 : 2;
        in.init(this, channel);
        out.init(this, channel);
        if (this.readChannel(channel) != null) {
            in.setInputStream(this.is);
            in.readToData(false);
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    OutputStream writeChannel(int channel) throws IOException {
        while (this.os != null) {
            boolean canWrite = false;
            Object object = this.WRITE_LOCK;
            synchronized (object) {
                if (!this.isWriteLocked) {
                    this.isWriteLocked = true;
                    canWrite = true;
                } else {
                    try {
                        this.WRITE_LOCK.wait(5000L);
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                }
            }
            if (!canWrite) continue;
            this.os.write(67);
            this.os.write(channel >> 8);
            this.os.write(channel);
            return this.os;
        }
        return null;
    }

    void yield(int channel) throws IOException {
        this.os.write(89);
        this.freeWriteLock();
    }

    void flush(int channel) throws IOException {
        this.os.write(89);
        this.os.flush();
        this.freeWriteLock();
    }

    void close(int channel) throws IOException {
        if (this.os != null) {
            this.os.write(81);
            this.os.flush();
            this.freeWriteLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void freeWriteLock() {
        Object object = this.WRITE_LOCK;
        synchronized (object) {
            this.isWriteLocked = false;
            this.WRITE_LOCK.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    InputStream readChannel(int channel) throws IOException {
        while (!this.isClosed) {
            if (this.inputReady[channel]) {
                this.inputReady[channel] = false;
                return this.is;
            }
            boolean canRead = false;
            Object object = this.READ_LOCK;
            synchronized (object) {
                if (!this.isReadLocked) {
                    this.isReadLocked = true;
                    canRead = true;
                } else {
                    try {
                        this.READ_LOCK.wait(5000L);
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                }
            }
            if (!canRead) continue;
            try {
                this.readData();
            }
            catch (IOException e) {
                this.close();
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean getReadLock() {
        Object object = this.READ_LOCK;
        synchronized (object) {
            if (!this.isReadLocked) {
                this.isReadLocked = true;
                return true;
            }
            try {
                this.READ_LOCK.wait(5000L);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void freeReadLock() {
        Object object = this.READ_LOCK;
        synchronized (object) {
            this.isReadLocked = false;
            this.READ_LOCK.notifyAll();
        }
    }

    private void readData() throws IOException {
        block6: while (!this.isClosed) {
            int code = this.is.read();
            switch (code) {
                case 9: 
                case 10: 
                case 13: 
                case 32: {
                    continue block6;
                }
                case 67: {
                    int channel = (this.is.read() << 8) + this.is.read();
                    this.inputReady[channel] = true;
                    return;
                }
                case 69: {
                    int channel = (this.is.read() << 8) + this.is.read();
                    int status = (this.is.read() << 8) + this.is.read();
                    this.inputReady[channel] = true;
                    return;
                }
                case -1: {
                    this.close();
                    return;
                }
            }
            this.close();
            return;
        }
    }

    public void close() throws IOException {
        this.isClosed = true;
        OutputStream os = this.os;
        this.os = null;
        InputStream is = this.is;
        this.is = null;
        if (os != null) {
            os.close();
        }
        if (is != null) {
            is.close();
        }
    }
}

