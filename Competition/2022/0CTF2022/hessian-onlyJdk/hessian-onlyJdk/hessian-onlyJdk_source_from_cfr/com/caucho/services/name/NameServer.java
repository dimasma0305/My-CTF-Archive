/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.services.name;

import com.caucho.services.name.NameServiceException;

public interface NameServer {
    public Object lookup(String var1) throws NameServiceException;

    public String[] list() throws NameServiceException;
}

