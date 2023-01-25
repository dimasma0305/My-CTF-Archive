/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.ObjectSerializer;
import com.caucho.hessian.io.Serializer;
import java.io.IOException;

public class ByteArraySerializer
extends AbstractSerializer
implements ObjectSerializer {
    public static final ByteArraySerializer SER = new ByteArraySerializer();

    private ByteArraySerializer() {
    }

    public Serializer getObjectSerializer() {
        return this;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        byte[] data = (byte[])obj;
        if (data != null) {
            out.writeBytes(data, 0, data.length);
        } else {
            out.writeNull();
        }
    }
}

