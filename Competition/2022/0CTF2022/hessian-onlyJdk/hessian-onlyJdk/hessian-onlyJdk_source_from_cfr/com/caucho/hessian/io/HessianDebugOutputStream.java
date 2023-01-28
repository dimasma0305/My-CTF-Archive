/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.HessianDebugState;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HessianDebugOutputStream
extends OutputStream {
    private static final Logger log = Logger.getLogger(HessianDebugOutputStream.class.getName());
    private OutputStream _os;
    private HessianDebugState _state;

    public HessianDebugOutputStream(OutputStream os, PrintWriter dbg) {
        this._os = os;
        this._state = new HessianDebugState(dbg);
    }

    public HessianDebugOutputStream(OutputStream os, Logger log, Level level) {
        this(os, new PrintWriter(new LogWriter(log, level)));
    }

    public HessianDebugOutputStream(Logger log, Level level) {
        this(null, new PrintWriter(new LogWriter(log, level)));
    }

    public void initPacket(OutputStream os) {
        this._os = os;
    }

    public void startTop2() {
        this._state.startTop2();
    }

    public void startStreaming() {
        this._state.startStreaming();
    }

    public void write(int ch) throws IOException {
        this._os.write(ch &= 0xFF);
        try {
            this._state.next(ch);
        }
        catch (Exception e) {
            log.log(Level.WARNING, e.toString(), e);
        }
    }

    public void flush() throws IOException {
        this._os.flush();
    }

    public void close() throws IOException {
        OutputStream os = this._os;
        this._os = null;
        if (os != null) {
            this._state.next(-1);
            os.close();
        }
        this._state.println();
    }

    static class LogWriter
    extends Writer {
        private Logger _log;
        private Level _level;
        private StringBuilder _sb = new StringBuilder();

        LogWriter(Logger log, Level level) {
            this._log = log;
            this._level = level;
        }

        public void write(char ch) {
            if (ch == '\n' && this._sb.length() > 0) {
                this._log.log(this._level, this._sb.toString());
                this._sb.setLength(0);
            } else {
                this._sb.append(ch);
            }
        }

        public void write(char[] buffer, int offset, int length) {
            for (int i = 0; i < length; ++i) {
                char ch = buffer[offset + i];
                if (ch == '\n' && this._sb.length() > 0) {
                    this._log.log(this._level, this._sb.toString());
                    this._sb.setLength(0);
                    continue;
                }
                this._sb.append(ch);
            }
        }

        public void flush() {
        }

        public void close() {
        }
    }
}

