/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianMetaInfoAPI;
import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyResolver;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.client.HessianURLConnectionFactory;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.HessianRemoteObject;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.services.client.ServiceProxyFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HessianProxyFactory
implements ServiceProxyFactory,
ObjectFactory {
    protected static Logger log = Logger.getLogger(HessianProxyFactory.class.getName());
    private final ClassLoader _loader;
    private SerializerFactory _serializerFactory;
    private HessianConnectionFactory _connFactory;
    private HessianRemoteResolver _resolver;
    private String _user;
    private String _password;
    private String _basicAuth;
    private boolean _isOverloadEnabled = false;
    private boolean _isHessian2Reply = true;
    private boolean _isHessian2Request = false;
    private boolean _isChunkedPost = true;
    private boolean _isDebug = false;
    private long _readTimeout = -1L;
    private long _connectTimeout = -1L;

    public HessianProxyFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public HessianProxyFactory(ClassLoader loader) {
        this._loader = loader;
        this._resolver = new HessianProxyResolver(this);
    }

    public void setUser(String user) {
        this._user = user;
        this._basicAuth = null;
    }

    public void setPassword(String password) {
        this._password = password;
        this._basicAuth = null;
    }

    public String getBasicAuth() {
        if (this._basicAuth != null) {
            return this._basicAuth;
        }
        if (this._user != null && this._password != null) {
            return "Basic " + this.base64(this._user + ":" + this._password);
        }
        return null;
    }

    public void setConnectionFactory(HessianConnectionFactory factory) {
        this._connFactory = factory;
    }

    public HessianConnectionFactory getConnectionFactory() {
        if (this._connFactory == null) {
            this._connFactory = this.createHessianConnectionFactory();
            this._connFactory.setHessianProxyFactory(this);
        }
        return this._connFactory;
    }

    public void setDebug(boolean isDebug) {
        this._isDebug = isDebug;
    }

    public boolean isDebug() {
        return this._isDebug;
    }

    public boolean isOverloadEnabled() {
        return this._isOverloadEnabled;
    }

    public void setOverloadEnabled(boolean isOverloadEnabled) {
        this._isOverloadEnabled = isOverloadEnabled;
    }

    public void setChunkedPost(boolean isChunked) {
        this._isChunkedPost = isChunked;
    }

    public boolean isChunkedPost() {
        return this._isChunkedPost;
    }

    public long getReadTimeout() {
        return this._readTimeout;
    }

    public void setReadTimeout(long timeout) {
        this._readTimeout = timeout;
    }

    public long getConnectTimeout() {
        return this._connectTimeout;
    }

    public void setConnectTimeout(long timeout) {
        this._connectTimeout = timeout;
    }

    public void setHessian2Reply(boolean isHessian2) {
        this._isHessian2Reply = isHessian2;
    }

    public void setHessian2Request(boolean isHessian2) {
        this._isHessian2Request = isHessian2;
        if (isHessian2) {
            this._isHessian2Reply = true;
        }
    }

    public HessianRemoteResolver getRemoteResolver() {
        return this._resolver;
    }

    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    public SerializerFactory getSerializerFactory() {
        if (this._serializerFactory == null) {
            this._serializerFactory = new SerializerFactory(this._loader);
        }
        return this._serializerFactory;
    }

    protected HessianConnectionFactory createHessianConnectionFactory() {
        String className = System.getProperty(HessianConnectionFactory.class.getName());
        HessianConnectionFactory factory = null;
        try {
            if (className != null) {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                Class<?> cl = Class.forName(className, false, loader);
                factory = (HessianConnectionFactory)cl.newInstance();
                return factory;
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new HessianURLConnectionFactory();
    }

    public Object create(String url) throws MalformedURLException, ClassNotFoundException {
        HessianMetaInfoAPI metaInfo = (HessianMetaInfoAPI)this.create(HessianMetaInfoAPI.class, url);
        String apiClassName = (String)metaInfo._hessian_getAttribute("java.api.class");
        if (apiClassName == null) {
            throw new HessianRuntimeException(url + " has an unknown api.");
        }
        Class<?> apiClass = Class.forName(apiClassName, false, this._loader);
        return this.create(apiClass, url);
    }

    @Override
    public Object create(Class api, String urlName) throws MalformedURLException {
        return this.create(api, urlName, this._loader);
    }

    public Object create(Class<?> api, String urlName, ClassLoader loader) throws MalformedURLException {
        URL url = new URL(urlName);
        return this.create(api, url, loader);
    }

    public Object create(Class<?> api, URL url, ClassLoader loader) {
        if (api == null) {
            throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
        }
        HessianProxy handler = null;
        handler = new HessianProxy(url, this, api);
        return Proxy.newProxyInstance(loader, new Class[]{api, HessianRemoteObject.class}, (InvocationHandler)handler);
    }

    public AbstractHessianInput getHessianInput(InputStream is) {
        return this.getHessian2Input(is);
    }

    public AbstractHessianInput getHessian1Input(InputStream is) {
        if (this._isDebug) {
            is = new HessianDebugInputStream(is, new PrintWriter(System.out));
        }
        HessianInput in = new HessianInput(is);
        in.setRemoteResolver(this.getRemoteResolver());
        ((AbstractHessianInput)in).setSerializerFactory(this.getSerializerFactory());
        return in;
    }

    public AbstractHessianInput getHessian2Input(InputStream is) {
        if (this._isDebug) {
            is = new HessianDebugInputStream(is, new PrintWriter(System.out));
        }
        Hessian2Input in = new Hessian2Input(is);
        in.setRemoteResolver(this.getRemoteResolver());
        ((AbstractHessianInput)in).setSerializerFactory(this.getSerializerFactory());
        return in;
    }

    public AbstractHessianOutput getHessianOutput(OutputStream os) {
        AbstractHessianOutput out;
        if (this._isHessian2Request) {
            out = new Hessian2Output(os);
        } else {
            HessianOutput out1 = new HessianOutput(os);
            out = out1;
            if (this._isHessian2Reply) {
                out1.setVersion(2);
            }
        }
        out.setSerializerFactory(this.getSerializerFactory());
        return out;
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Reference ref = (Reference)obj;
        String api = null;
        String url = null;
        for (int i = 0; i < ref.size(); ++i) {
            RefAddr addr = ref.get(i);
            String type = addr.getType();
            String value = (String)addr.getContent();
            if (type.equals("type")) {
                api = value;
                continue;
            }
            if (type.equals("url")) {
                url = value;
                continue;
            }
            if (type.equals("user")) {
                this.setUser(value);
                continue;
            }
            if (!type.equals("password")) continue;
            this.setPassword(value);
        }
        if (url == null) {
            throw new NamingException("`url' must be configured for HessianProxyFactory.");
        }
        if (api == null) {
            throw new NamingException("`type' must be configured for HessianProxyFactory.");
        }
        Class<?> apiClass = Class.forName(api, false, this._loader);
        return this.create(apiClass, url);
    }

    private String base64(String value) {
        long chunk;
        StringBuffer cb = new StringBuffer();
        int i = 0;
        i = 0;
        while (i + 2 < value.length()) {
            chunk = value.charAt(i);
            chunk = (chunk << 8) + (long)value.charAt(i + 1);
            chunk = (chunk << 8) + (long)value.charAt(i + 2);
            cb.append(HessianProxyFactory.encode(chunk >> 18));
            cb.append(HessianProxyFactory.encode(chunk >> 12));
            cb.append(HessianProxyFactory.encode(chunk >> 6));
            cb.append(HessianProxyFactory.encode(chunk));
            i += 3;
        }
        if (i + 1 < value.length()) {
            chunk = value.charAt(i);
            chunk = (chunk << 8) + (long)value.charAt(i + 1);
            cb.append(HessianProxyFactory.encode((chunk <<= 8) >> 18));
            cb.append(HessianProxyFactory.encode(chunk >> 12));
            cb.append(HessianProxyFactory.encode(chunk >> 6));
            cb.append('=');
        } else if (i < value.length()) {
            chunk = value.charAt(i);
            cb.append(HessianProxyFactory.encode((chunk <<= 16) >> 18));
            cb.append(HessianProxyFactory.encode(chunk >> 12));
            cb.append('=');
            cb.append('=');
        }
        return cb.toString();
    }

    public static char encode(long d) {
        if ((d &= 0x3FL) < 26L) {
            return (char)(d + 65L);
        }
        if (d < 52L) {
            return (char)(d + 97L - 26L);
        }
        if (d < 62L) {
            return (char)(d + 48L - 52L);
        }
        if (d == 62L) {
            return '+';
        }
        return '/';
    }
}

