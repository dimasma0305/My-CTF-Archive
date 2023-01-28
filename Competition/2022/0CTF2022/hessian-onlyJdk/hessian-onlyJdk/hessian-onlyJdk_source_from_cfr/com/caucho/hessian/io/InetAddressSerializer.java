/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.InetAddressHandle;
import java.io.IOException;
import java.net.InetAddress;

public class InetAddressSerializer
extends AbstractSerializer {
    private static InetAddressSerializer SERIALIZER = new InetAddressSerializer();

    public static InetAddressSerializer create() {
        return SERIALIZER;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
        } else {
            InetAddress addr = (InetAddress)obj;
            out.writeObject(new InetAddressHandle(addr.getHostName(), addr.getAddress()));
        }
    }
}

