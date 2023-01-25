/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.services.client;

import java.net.MalformedURLException;

public interface ServiceProxyFactory {
    public Object create(Class var1, String var2) throws MalformedURLException;
}

