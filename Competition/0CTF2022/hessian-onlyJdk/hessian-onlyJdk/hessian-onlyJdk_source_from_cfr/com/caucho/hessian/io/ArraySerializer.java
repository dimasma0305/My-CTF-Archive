/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import java.io.IOException;

public class ArraySerializer
extends AbstractSerializer {
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Object[] array = (Object[])obj;
        boolean hasEnd = out.writeListBegin(array.length, this.getArrayType(obj.getClass()));
        for (int i = 0; i < array.length; ++i) {
            out.writeObject(array[i]);
        }
        if (hasEnd) {
            out.writeListEnd();
        }
    }

    private String getArrayType(Class cl) {
        if (cl.isArray()) {
            return '[' + this.getArrayType(cl.getComponentType());
        }
        String name = cl.getName();
        if (name.equals("java.lang.String")) {
            return "string";
        }
        if (name.equals("java.lang.Object")) {
            return "object";
        }
        if (name.equals("java.util.Date")) {
            return "date";
        }
        return name;
    }
}

