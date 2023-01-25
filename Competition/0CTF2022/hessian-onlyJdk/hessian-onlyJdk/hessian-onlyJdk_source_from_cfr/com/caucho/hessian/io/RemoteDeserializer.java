/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianRemote;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.JavaDeserializer;
import java.util.logging.Logger;

public class RemoteDeserializer
extends JavaDeserializer {
    private static final Logger log = Logger.getLogger(RemoteDeserializer.class.getName());
    public static final Deserializer DESER = new RemoteDeserializer();

    public RemoteDeserializer() {
        super(HessianRemote.class);
    }

    public boolean isReadResolve() {
        return true;
    }

    protected Object resolve(AbstractHessianInput in, Object obj) throws Exception {
        HessianRemote remote = (HessianRemote)obj;
        HessianRemoteResolver resolver = in.getRemoteResolver();
        if (resolver != null) {
            Object proxy = resolver.lookup(remote.getType(), remote.getURL());
            return proxy;
        }
        return remote;
    }
}

