/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.client;

import com.caucho.burlap.client.BurlapProxyFactory;
import com.caucho.burlap.io.BurlapRemoteResolver;
import java.io.IOException;

public class BurlapProxyResolver
implements BurlapRemoteResolver {
    private BurlapProxyFactory factory;

    public BurlapProxyResolver(BurlapProxyFactory factory) {
        this.factory = factory;
    }

    public Object lookup(String type, String url) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> api = Class.forName(type, false, loader);
            return this.factory.create(api, url);
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

