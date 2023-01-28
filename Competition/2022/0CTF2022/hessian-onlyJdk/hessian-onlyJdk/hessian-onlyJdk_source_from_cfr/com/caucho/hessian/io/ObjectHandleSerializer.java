/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.Serializer;
import java.io.IOException;

public class ObjectHandleSerializer
extends AbstractSerializer {
    public static final Serializer SER = new ObjectHandleSerializer();

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
        } else {
            if (out.addRef(obj)) {
                return;
            }
            int ref = out.writeObjectBegin("object");
            if (ref < -1) {
                out.writeMapEnd();
            } else if (ref == -1) {
                out.writeInt(0);
                out.writeObjectBegin("object");
            }
        }
    }
}

