/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.client;

import com.caucho.burlap.client.BurlapMetaInfoAPI;
import com.caucho.burlap.client.BurlapProxy;
import com.caucho.burlap.client.BurlapProxyResolver;
import com.caucho.burlap.client.BurlapRuntimeException;
import com.caucho.burlap.io.AbstractBurlapInput;
import com.caucho.burlap.io.BurlapInput;
import com.caucho.burlap.io.BurlapOutput;
import com.caucho.burlap.io.BurlapRemoteObject;
import com.caucho.burlap.io.BurlapRemoteResolver;
import com.caucho.services.client.ServiceProxyFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BurlapProxyFactory
implements ServiceProxyFactory,
ObjectFactory {
    private BurlapRemoteResolver _resolver = new BurlapProxyResolver(this);
    private String _user;
    private String _password;
    private String _basicAuth;
    private long _readTimeout;
    private boolean _isOverloadEnabled = false;

    public void setUser(String user) {
        this._user = user;
        this._basicAuth = null;
    }

    public void setPassword(String password) {
        this._password = password;
        this._basicAuth = null;
    }

    public boolean isOverloadEnabled() {
        return this._isOverloadEnabled;
    }

    public void setOverloadEnabled(boolean isOverloadEnabled) {
        this._isOverloadEnabled = isOverloadEnabled;
    }

    public BurlapRemoteResolver getRemoteResolver() {
        return this._resolver;
    }

    protected URLConnection openConnection(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        if (this._basicAuth != null) {
            conn.setRequestProperty("Authorization", this._basicAuth);
        } else if (this._user != null && this._password != null) {
            this._basicAuth = "Basic " + this.base64(this._user + ":" + this._password);
            conn.setRequestProperty("Authorization", this._basicAuth);
        }
        return conn;
    }

    public Object create(String url) throws MalformedURLException, ClassNotFoundException {
        BurlapMetaInfoAPI metaInfo = (BurlapMetaInfoAPI)this.create(BurlapMetaInfoAPI.class, url);
        String apiClassName = (String)metaInfo._burlap_getAttribute("java.api.class");
        if (apiClassName == null) {
            throw new BurlapRuntimeException(url + " has an unknown api.");
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<?> apiClass = Class.forName(apiClassName, false, loader);
        return this.create(apiClass, url);
    }

    @Override
    public Object create(Class api, String urlName) throws MalformedURLException {
        if (api == null) {
            throw new NullPointerException();
        }
        URL url = new URL(urlName);
        try {
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(10);
            conn.setReadTimeout(10);
            conn.setRequestProperty("Connection", "close");
            InputStream is = conn.getInputStream();
            is.close();
            conn.disconnect();
        }
        catch (IOException e) {
            // empty catch block
        }
        BurlapProxy handler = new BurlapProxy(this, url);
        return Proxy.newProxyInstance(api.getClassLoader(), new Class[]{api, BurlapRemoteObject.class}, (InvocationHandler)handler);
    }

    public AbstractBurlapInput getBurlapInput(InputStream is) {
        BurlapInput in = new BurlapInput(is);
        in.setRemoteResolver(this.getRemoteResolver());
        return in;
    }

    public BurlapOutput getBurlapOutput(OutputStream os) {
        BurlapOutput out = new BurlapOutput(os);
        return out;
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        Reference ref = (Reference)obj;
        String api = null;
        String url = null;
        Object user = null;
        Object password = null;
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
            throw new NamingException("`url' must be configured for BurlapProxyFactory.");
        }
        if (api == null) {
            throw new NamingException("`type' must be configured for BurlapProxyFactory.");
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<?> apiClass = Class.forName(api, false, loader);
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
            cb.append(BurlapProxyFactory.encode(chunk >> 18));
            cb.append(BurlapProxyFactory.encode(chunk >> 12));
            cb.append(BurlapProxyFactory.encode(chunk >> 6));
            cb.append(BurlapProxyFactory.encode(chunk));
            i += 3;
        }
        if (i + 1 < value.length()) {
            chunk = value.charAt(i);
            chunk = (chunk << 8) + (long)value.charAt(i + 1);
            cb.append(BurlapProxyFactory.encode((chunk <<= 8) >> 18));
            cb.append(BurlapProxyFactory.encode(chunk >> 12));
            cb.append(BurlapProxyFactory.encode(chunk >> 6));
            cb.append('=');
        } else if (i < value.length()) {
            chunk = value.charAt(i);
            cb.append(BurlapProxyFactory.encode((chunk <<= 16) >> 18));
            cb.append(BurlapProxyFactory.encode(chunk >> 12));
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

