/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianProxyFactory;
import java.io.IOException;
import java.net.URL;

public interface HessianConnectionFactory {
    public void setHessianProxyFactory(HessianProxyFactory var1);

    public HessianConnection open(URL var1) throws IOException;
}

