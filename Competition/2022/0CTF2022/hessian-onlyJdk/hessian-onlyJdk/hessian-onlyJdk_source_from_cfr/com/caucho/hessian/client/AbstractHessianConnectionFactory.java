/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianProxyFactory;
import java.io.IOException;
import java.net.URL;

public abstract class AbstractHessianConnectionFactory
implements HessianConnectionFactory {
    private HessianProxyFactory _factory;

    public void setHessianProxyFactory(HessianProxyFactory factory) {
        this._factory = factory;
    }

    public HessianProxyFactory getHessianProxyFactory() {
        return this._factory;
    }

    public abstract HessianConnection open(URL var1) throws IOException;
}

