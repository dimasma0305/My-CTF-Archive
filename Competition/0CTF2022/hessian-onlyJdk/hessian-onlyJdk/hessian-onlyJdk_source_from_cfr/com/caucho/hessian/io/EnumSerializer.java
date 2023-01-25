/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import java.io.IOException;
import java.lang.reflect.Method;

public class EnumSerializer
extends AbstractSerializer {
    private Method _name;

    public EnumSerializer(Class cl) {
        if (!cl.isEnum() && cl.getSuperclass().isEnum()) {
            cl = cl.getSuperclass();
        }
        try {
            this._name = cl.getMethod("name", new Class[0]);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Class<?> cl = obj.getClass();
        if (!cl.isEnum() && cl.getSuperclass().isEnum()) {
            cl = cl.getSuperclass();
        }
        String name = null;
        try {
            name = (String)this._name.invoke(obj, null);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        int ref = out.writeObjectBegin(cl.getName());
        if (ref < -1) {
            out.writeString("name");
            out.writeString(name);
            out.writeMapEnd();
        } else {
            if (ref == -1) {
                out.writeClassFieldLength(1);
                out.writeString("name");
                out.writeObjectBegin(cl.getName());
            }
            out.writeString(name);
        }
    }
}

