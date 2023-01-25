/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.IOExceptionWrapper;
import java.io.IOException;
import java.lang.reflect.Method;

public class EnumDeserializer
extends AbstractDeserializer {
    private Class _enumType;
    private Method _valueOf;

    public EnumDeserializer(Class cl) {
        if (cl.isEnum()) {
            this._enumType = cl;
        } else if (cl.getSuperclass().isEnum()) {
            this._enumType = cl.getSuperclass();
        } else {
            throw new RuntimeException("Class " + cl.getName() + " is not an enum");
        }
        try {
            this._valueOf = this._enumType.getMethod("valueOf", Class.class, String.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class getType() {
        return this._enumType;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        String name = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if (key.equals("name")) {
                name = in.readString();
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        Object obj = this.create(name);
        in.addRef(obj);
        return obj;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])fields;
        String name = null;
        for (int i = 0; i < fieldNames.length; ++i) {
            if ("name".equals(fieldNames[i])) {
                name = in.readString();
                continue;
            }
            in.readObject();
        }
        Object obj = this.create(name);
        in.addRef(obj);
        return obj;
    }

    private Object create(String name) throws IOException {
        if (name == null) {
            throw new IOException(this._enumType.getName() + " expects name.");
        }
        try {
            return this._valueOf.invoke(null, this._enumType, name);
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }
}

