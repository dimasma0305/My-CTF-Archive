/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.client;

import com.caucho.burlap.client.BurlapProtocolException;
import com.caucho.burlap.client.BurlapProxyFactory;
import com.caucho.burlap.client.BurlapRuntimeException;
import com.caucho.burlap.io.AbstractBurlapInput;
import com.caucho.burlap.io.BurlapOutput;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BurlapProxy
implements InvocationHandler {
    private static final Logger log = Logger.getLogger(BurlapProxy.class.getName());
    private BurlapProxyFactory _factory;
    private URL _url;

    BurlapProxy(BurlapProxyFactory factory, URL url) {
        this._factory = factory;
        this._url = url;
    }

    public URL getURL() {
        return this._url;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] params = method.getParameterTypes();
        if (methodName.equals("equals") && params.length == 1 && params[0].equals(Object.class)) {
            Object value = args[0];
            if (value == null || !Proxy.isProxyClass(value.getClass())) {
                return new Boolean(false);
            }
            BurlapProxy handler = (BurlapProxy)Proxy.getInvocationHandler(value);
            return new Boolean(this._url.equals(handler.getURL()));
        }
        if (methodName.equals("hashCode") && params.length == 0) {
            return new Integer(this._url.hashCode());
        }
        if (methodName.equals("getBurlapType")) {
            return proxy.getClass().getInterfaces()[0].getName();
        }
        if (methodName.equals("getBurlapURL")) {
            return this._url.toString();
        }
        if (methodName.equals("toString") && params.length == 0) {
            return this.getClass().getSimpleName() + "[" + this._url + "]";
        }
        InputStream is = null;
        URLConnection conn = null;
        HttpURLConnection httpConn = null;
        try {
            OutputStream os;
            conn = this._factory.openConnection(this._url);
            httpConn = (HttpURLConnection)conn;
            httpConn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml");
            try {
                os = conn.getOutputStream();
            }
            catch (Exception e) {
                throw new BurlapRuntimeException(e);
            }
            BurlapOutput out = this._factory.getBurlapOutput(os);
            if (this._factory.isOverloadEnabled()) {
                methodName = args != null ? methodName + "__" + args.length : methodName + "__0";
            }
            if (log.isLoggable(Level.FINE)) {
                log.fine(this + " calling " + methodName + " (" + method + ")");
            }
            out.call(methodName, args);
            try {
                os.flush();
            }
            catch (Exception e) {
                throw new BurlapRuntimeException(e);
            }
            if (conn instanceof HttpURLConnection) {
                httpConn = (HttpURLConnection)conn;
                int code = 500;
                try {
                    code = httpConn.getResponseCode();
                }
                catch (Exception e) {
                    // empty catch block
                }
                if (code != 200) {
                    StringBuffer sb = new StringBuffer();
                    try {
                        int ch;
                        is = httpConn.getInputStream();
                        if (is != null) {
                            while ((ch = is.read()) >= 0) {
                                sb.append((char)ch);
                            }
                            is.close();
                        }
                        if ((is = httpConn.getErrorStream()) != null) {
                            while ((ch = is.read()) >= 0) {
                                sb.append((char)ch);
                            }
                        }
                    }
                    catch (FileNotFoundException e) {
                        throw new BurlapRuntimeException(code + ": " + String.valueOf(e));
                    }
                    catch (IOException e) {
                        // empty catch block
                    }
                    if (is != null) {
                        is.close();
                    }
                    throw new BurlapProtocolException(code + ": " + sb.toString());
                }
            }
            is = conn.getInputStream();
            AbstractBurlapInput in = this._factory.getBurlapInput(is);
            Object object = in.readReply(method.getReturnType());
            return object;
        }
        catch (BurlapProtocolException e) {
            throw new BurlapRuntimeException(e);
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException e) {}
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this._url + "]";
    }
}

