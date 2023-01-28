/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.services.name;

import com.caucho.services.name.NameServiceException;
import java.rmi.RemoteException;

public interface NameServerRemote {
    public Object lookup(String var1) throws NameServiceException, RemoteException;

    public String[] list() throws NameServiceException, RemoteException;
}

