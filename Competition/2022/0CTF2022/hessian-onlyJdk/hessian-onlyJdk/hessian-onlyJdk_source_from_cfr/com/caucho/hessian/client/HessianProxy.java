/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.HessianRemote;
import com.caucho.services.server.AbstractSkeleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HessianProxy
implements InvocationHandler,
Serializable {
    private static final Logger log = Logger.getLogger(HessianProxy.class.getName());
    protected HessianProxyFactory _factory;
    private WeakHashMap<Method, String> _mangleMap = new WeakHashMap();
    private Class<?> _type;
    private URL _url;

    protected HessianProxy(URL url, HessianProxyFactory factory) {
        this(url, factory, null);
    }

    protected HessianProxy(URL url, HessianProxyFactory factory, Class<?> type) {
        this._factory = factory;
        this._url = url;
        this._type = type;
    }

    public URL getURL() {
        return this._url;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String mangleName;
        WeakHashMap<Method, String> weakHashMap = this._mangleMap;
        synchronized (weakHashMap) {
            mangleName = this._mangleMap.get(method);
        }
        if (mangleName == null) {
            String methodName = method.getName();
            Class<?>[] params = method.getParameterTypes();
            if (methodName.equals("equals") && params.length == 1 && params[0].equals(Object.class)) {
                Object value = args[0];
                if (value == null || !Proxy.isProxyClass(value.getClass())) {
                    return Boolean.FALSE;
                }
                InvocationHandler proxyHandler = Proxy.getInvocationHandler(value);
                if (!(proxyHandler instanceof HessianProxy)) {
                    return Boolean.FALSE;
                }
                HessianProxy handler = (HessianProxy)proxyHandler;
                return new Boolean(this._url.equals(handler.getURL()));
            }
            if (methodName.equals("hashCode") && params.length == 0) {
                return new Integer(this._url.hashCode());
            }
            if (methodName.equals("getHessianType")) {
                return proxy.getClass().getInterfaces()[0].getName();
            }
            if (methodName.equals("getHessianURL")) {
                return this._url.toString();
            }
            if (methodName.equals("toString") && params.length == 0) {
                return "HessianProxy[" + this._url + "]";
            }
            mangleName = !this._factory.isOverloadEnabled() ? method.getName() : this.mangleName(method);
            WeakHashMap<Method, String> value = this._mangleMap;
            synchronized (value) {
                this._mangleMap.put(method, mangleName);
            }
        }
        InputStream is = null;
        HessianConnection conn = null;
        try {
            AbstractHessianInput in;
            int code;
            if (log.isLoggable(Level.FINER)) {
                log.finer("Hessian[" + this._url + "] calling " + mangleName);
            }
            conn = this.sendRequest(mangleName, args);
            is = this.getInputStream(conn);
            if (log.isLoggable(Level.FINEST)) {
                PrintWriter dbg = new PrintWriter(new LogWriter(log));
                HessianDebugInputStream dIs = new HessianDebugInputStream(is, dbg);
                dIs.startTop2();
                is = dIs;
            }
            if ((code = is.read()) == 72) {
                Object value;
                int major = is.read();
                int minor = is.read();
                in = this._factory.getHessian2Input(is);
                Object object = value = in.readReply(method.getReturnType());
                return object;
            }
            if (code == 114) {
                int major = is.read();
                int minor = is.read();
                in = this._factory.getHessianInput(is);
                in.startReplyBody();
                Object value = in.readObject(method.getReturnType());
                if (value instanceof InputStream) {
                    value = new ResultInputStream(conn, is, in, (InputStream)value);
                    is = null;
                    conn = null;
                } else {
                    in.completeReply();
                }
                Object object = value;
                return object;
            }
            try {
                throw new HessianProtocolException("'" + (char)code + "' is an unknown code");
            }
            catch (HessianProtocolException e) {
                throw new HessianRuntimeException(e);
            }
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception e) {
                log.log(Level.FINE, e.toString(), e);
            }
            try {
                if (conn != null) {
                    conn.destroy();
                }
            }
            catch (Exception e) {
                log.log(Level.FINE, e.toString(), e);
            }
        }
    }

    protected InputStream getInputStream(HessianConnection conn) throws IOException {
        InputStream is = conn.getInputStream();
        if ("deflate".equals(conn.getContentEncoding())) {
            is = new InflaterInputStream(is, new Inflater(true));
        }
        return is;
    }

    protected String mangleName(Method method) {
        Class<?>[] param = method.getParameterTypes();
        if (param == null || param.length == 0) {
            return method.getName();
        }
        return AbstractSkeleton.mangleName(method, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected HessianConnection sendRequest(String methodName, Object[] args) throws IOException {
        HessianConnection conn = null;
        conn = this._factory.getConnectionFactory().open(this._url);
        boolean isValid = false;
        try {
            this.addRequestHeaders(conn);
            OutputStream os = null;
            try {
                os = conn.getOutputStream();
            }
            catch (Exception e) {
                throw new HessianRuntimeException(e);
            }
            if (log.isLoggable(Level.FINEST)) {
                PrintWriter dbg = new PrintWriter(new LogWriter(log));
                HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
                dOs.startTop2();
                os = dOs;
            }
            AbstractHessianOutput out = this._factory.getHessianOutput(os);
            out.call(methodName, args);
            out.flush();
            conn.sendRequest();
            isValid = true;
            HessianConnection hessianConnection = conn;
            return hessianConnection;
        }
        finally {
            if (!isValid && conn != null) {
                conn.destroy();
            }
        }
    }

    protected void addRequestHeaders(HessianConnection conn) {
        conn.addHeader("Content-Type", "x-application/hessian");
        conn.addHeader("Accept-Encoding", "deflate");
        String basicAuth = this._factory.getBasicAuth();
        if (basicAuth != null) {
            conn.addHeader("Authorization", basicAuth);
        }
    }

    protected void parseResponseHeaders(URLConnection conn) {
    }

    public Object writeReplace() {
        return new HessianRemote(this._type.getName(), this._url.toString());
    }

    static class LogWriter
    extends Writer {
        private Logger _log;
        private Level _level = Level.FINEST;
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
            if (this._sb.length() > 0) {
                this._log.log(this._level, this._sb.toString());
            }
        }
    }

    static class ResultInputStream
    extends InputStream {
        private HessianConnection _conn;
        private InputStream _connIs;
        private AbstractHessianInput _in;
        private InputStream _hessianIs;

        ResultInputStream(HessianConnection conn, InputStream is, AbstractHessianInput in, InputStream hessianIs) {
            this._conn = conn;
            this._connIs = is;
            this._in = in;
            this._hessianIs = hessianIs;
        }

        public int read() throws IOException {
            if (this._hessianIs != null) {
                int value = this._hessianIs.read();
                if (value < 0) {
                    this.close();
                }
                return value;
            }
            return -1;
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            if (this._hessianIs != null) {
                int value = this._hessianIs.read(buffer, offset, length);
                if (value < 0) {
                    this.close();
                }
                return value;
            }
            return -1;
        }

        public void close() throws IOException {
            HessianConnection conn = this._conn;
            this._conn = null;
            InputStream connIs = this._connIs;
            this._connIs = null;
            AbstractHessianInput in = this._in;
            this._in = null;
            InputStream hessianIs = this._hessianIs;
            this._hessianIs = null;
            try {
                if (hessianIs != null) {
                    hessianIs.close();
                }
            }
            catch (Exception e) {
                log.log(Level.FINE, e.toString(), e);
            }
            try {
                if (in != null) {
                    in.completeReply();
                    in.close();
                }
            }
            catch (Exception e) {
                log.log(Level.FINE, e.toString(), e);
            }
            try {
                if (connIs != null) {
                    connIs.close();
                }
            }
            catch (Exception e) {
                log.log(Level.FINE, e.toString(), e);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception e) {
                log.log(Level.FINE, e.toString(), e);
            }
        }
    }
}

