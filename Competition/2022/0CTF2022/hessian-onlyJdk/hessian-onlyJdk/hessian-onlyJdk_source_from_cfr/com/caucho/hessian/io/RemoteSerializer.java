/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.HessianRemote;
import com.caucho.hessian.io.HessianRemoteObject;
import java.io.IOException;

public class RemoteSerializer
extends AbstractSerializer {
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        HessianRemoteObject remoteObject = (HessianRemoteObject)obj;
        out.writeObject(new HessianRemote(remoteObject.getHessianType(), remoteObject.getHessianURL()));
    }
}

