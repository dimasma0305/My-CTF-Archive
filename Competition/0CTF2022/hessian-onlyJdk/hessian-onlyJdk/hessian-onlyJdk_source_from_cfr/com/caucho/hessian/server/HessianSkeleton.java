/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.server;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.HessianInputFactory;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.services.server.AbstractSkeleton;
import com.caucho.services.server.ServiceContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HessianSkeleton
extends AbstractSkeleton {
    private static final Logger log = Logger.getLogger(HessianSkeleton.class.getName());
    private boolean _isDebug;
    private HessianInputFactory _inputFactory = new HessianInputFactory();
    private HessianFactory _hessianFactory = new HessianFactory();
    private Object _service;

    public HessianSkeleton(Object service, Class<?> apiClass) {
        super(apiClass);
        if (service == null) {
            service = this;
        }
        this._service = service;
        if (!apiClass.isAssignableFrom(service.getClass())) {
            throw new IllegalArgumentException("Service " + service + " must be an instance of " + apiClass.getName());
        }
    }

    public HessianSkeleton(Class<?> apiClass) {
        super(apiClass);
    }

    public void setDebug(boolean isDebug) {
        this._isDebug = isDebug;
    }

    public boolean isDebug() {
        return this._isDebug;
    }

    public void setHessianFactory(HessianFactory factory) {
        this._hessianFactory = factory;
    }

    public void invoke(InputStream is, OutputStream os) throws Exception {
        this.invoke(is, os, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invoke(InputStream is, OutputStream os, SerializerFactory serializerFactory) throws Exception {
        AbstractHessianOutput out;
        AbstractHessianInput in;
        boolean isDebug = false;
        if (this.isDebugInvoke()) {
            isDebug = true;
            PrintWriter dbg = this.createDebugPrintWriter();
            HessianDebugInputStream dIs = new HessianDebugInputStream(is, dbg);
            dIs.startTop2();
            is = dIs;
            HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
            dOs.startTop2();
            os = dOs;
        }
        HessianInputFactory.HeaderType header = this._inputFactory.readHeader(is);
        switch (header) {
            case CALL_1_REPLY_1: {
                in = this._hessianFactory.createHessianInput(is);
                out = this._hessianFactory.createHessianOutput(os);
                break;
            }
            case CALL_1_REPLY_2: {
                in = this._hessianFactory.createHessianInput(is);
                out = this._hessianFactory.createHessian2Output(os);
                break;
            }
            case HESSIAN_2: {
                in = this._hessianFactory.createHessian2Input(is);
                in.readCall();
                out = this._hessianFactory.createHessian2Output(os);
                break;
            }
            default: {
                throw new IllegalStateException((Object)((Object)header) + " is an unknown Hessian call");
            }
        }
        if (serializerFactory != null) {
            in.setSerializerFactory(serializerFactory);
            out.setSerializerFactory(serializerFactory);
        }
        try {
            this.invoke(this._service, in, out);
        }
        finally {
            in.close();
            out.close();
            if (isDebug) {
                os.close();
            }
        }
    }

    public void invoke(AbstractHessianInput in, AbstractHessianOutput out) throws Exception {
        this.invoke(this._service, in, out);
    }

    public void invoke(Object service, AbstractHessianInput in, AbstractHessianOutput out) throws Exception {
        Class<?>[] args;
        String header;
        ServiceContext context = ServiceContext.getContext();
        in.skipOptionalCall();
        while ((header = in.readHeader()) != null) {
            Object value = in.readObject();
            context.addHeader(header, value);
        }
        String methodName = in.readMethod();
        int argLength = in.readMethodArgLength();
        Method method = this.getMethod(methodName + "__" + argLength);
        if (method == null) {
            method = this.getMethod(methodName);
        }
        if (method == null) {
            if ("_hessian_getAttribute".equals(methodName)) {
                String attrName = in.readString();
                in.completeCall();
                String value = null;
                if ("java.api.class".equals(attrName)) {
                    value = this.getAPIClassName();
                } else if ("java.home.class".equals(attrName)) {
                    value = this.getHomeClassName();
                } else if ("java.object.class".equals(attrName)) {
                    value = this.getObjectClassName();
                }
                out.writeReply(value);
                out.close();
                return;
            }
            if (method == null) {
                out.writeFault("NoSuchMethodException", this.escapeMessage("The service has no method named: " + in.getMethod()), null);
                out.close();
                return;
            }
        }
        if (argLength != (args = method.getParameterTypes()).length && argLength >= 0) {
            out.writeFault("NoSuchMethod", this.escapeMessage("method " + method + " argument length mismatch, received length=" + argLength), null);
            out.close();
            return;
        }
        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; ++i) {
            values[i] = in.readObject(args[i]);
        }
        Object result = null;
        try {
            result = method.invoke(service, values);
        }
        catch (Exception e) {
            Throwable e1 = e;
            if (e1 instanceof InvocationTargetException) {
                e1 = ((InvocationTargetException)e).getTargetException();
            }
            log.log(Level.FINE, this + " " + e1.toString(), e1);
            out.writeFault("ServiceException", this.escapeMessage(e1.getMessage()), e1);
            out.close();
            return;
        }
        in.completeCall();
        out.writeReply(result);
        out.close();
    }

    private String escapeMessage(String msg) {
        if (msg == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int length = msg.length();
        block6: for (int i = 0; i < length; ++i) {
            char ch = msg.charAt(i);
            switch (ch) {
                case '<': {
                    sb.append("&lt;");
                    continue block6;
                }
                case '>': {
                    sb.append("&gt;");
                    continue block6;
                }
                case '\u0000': {
                    sb.append("&#00;");
                    continue block6;
                }
                case '&': {
                    sb.append("&amp;");
                    continue block6;
                }
                default: {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    protected boolean isDebugInvoke() {
        return log.isLoggable(Level.FINEST) || this.isDebug() && log.isLoggable(Level.FINE);
    }

    protected PrintWriter createDebugPrintWriter() throws IOException {
        return new PrintWriter(new LogWriter(log));
    }

    static class LogWriter
    extends Writer {
        private Logger _log;
        private StringBuilder _sb = new StringBuilder();

        LogWriter(Logger log) {
            this._log = log;
        }

        public void write(char ch) {
            if (ch == '\n' && this._sb.length() > 0) {
                this._log.fine(this._sb.toString());
                this._sb.setLength(0);
            } else {
                this._sb.append(ch);
            }
        }

        public void write(char[] buffer, int offset, int length) {
            for (int i = 0; i < length; ++i) {
                char ch = buffer[offset + i];
                if (ch == '\n' && this._sb.length() > 0) {
                    this._log.fine(this._sb.toString());
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

