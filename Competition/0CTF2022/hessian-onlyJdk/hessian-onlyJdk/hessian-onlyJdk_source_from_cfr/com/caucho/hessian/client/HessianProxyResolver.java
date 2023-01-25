/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianRemoteResolver;
import java.io.IOException;

public class HessianProxyResolver
implements HessianRemoteResolver {
    private HessianProxyFactory _factory;

    public HessianProxyResolver(HessianProxyFactory factory) {
        this._factory = factory;
    }

    public Object lookup(String type, String url) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> api = Class.forName(type, false, loader);
            return this._factory.create(api, url);
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

