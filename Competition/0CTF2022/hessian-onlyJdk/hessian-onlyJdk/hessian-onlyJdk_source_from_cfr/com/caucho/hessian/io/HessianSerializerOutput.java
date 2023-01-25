/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.IOExceptionWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HessianSerializerOutput
extends Hessian2Output {
    public HessianSerializerOutput(OutputStream os) {
        super(os);
    }

    public HessianSerializerOutput() {
        super(null);
    }

    public void writeObjectImpl(Object obj) throws IOException {
        Class<?> cl;
        try {
            Method method = cl.getMethod("writeReplace", new Class[0]);
            Object repl = method.invoke(obj, new Object[0]);
            this.writeObject(repl);
            return;
        }
        catch (Exception e) {
            try {
                this.writeMapBegin(cl.getName());
                for (cl = obj.getClass(); cl != null; cl = cl.getSuperclass()) {
                    Field[] fields = cl.getDeclaredFields();
                    for (int i = 0; i < fields.length; ++i) {
                        Field field = fields[i];
                        if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) continue;
                        field.setAccessible(true);
                        this.writeString(field.getName());
                        this.writeObject(field.get(obj));
                    }
                }
                this.writeMapEnd();
            }
            catch (IllegalAccessException e2) {
                throw new IOExceptionWrapper(e2);
            }
            return;
        }
    }
}

