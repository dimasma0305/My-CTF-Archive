/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.Serializer;
import java.io.IOException;

public class StringValueSerializer
extends AbstractSerializer {
    public static final Serializer SER = new StringValueSerializer();

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
        } else {
            if (out.addRef(obj)) {
                return;
            }
            Class<?> cl = obj.getClass();
            int ref = out.writeObjectBegin(cl.getName());
            if (ref < -1) {
                out.writeString("value");
                out.writeString(obj.toString());
                out.writeMapEnd();
            } else {
                if (ref == -1) {
                    out.writeInt(1);
                    out.writeString("value");
                    out.writeObjectBegin(cl.getName());
                }
                out.writeString(obj.toString());
            }
        }
    }
}

