/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.IOExceptionWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class HessianSerializerInput
extends Hessian2Input {
    public HessianSerializerInput(InputStream is) {
        super(is);
    }

    public HessianSerializerInput() {
        super(null);
    }

    protected Object readObjectImpl(Class cl) throws IOException {
        try {
            Object obj = cl.newInstance();
            if (this._refs == null) {
                this._refs = new ArrayList();
            }
            this._refs.add(obj);
            HashMap fieldMap = this.getFieldMap(cl);
            int code = this.read();
            while (code >= 0 && code != 122) {
                Object value;
                this.unread();
                Object key = this.readObject();
                Field field = (Field)fieldMap.get(key);
                if (field != null) {
                    value = this.readObject(field.getType());
                    field.set(obj, value);
                } else {
                    value = this.readObject();
                }
                code = this.read();
            }
            if (code != 122) {
                throw this.expect("map", code);
            }
            try {
                Method method = cl.getMethod("readResolve", new Class[0]);
                return method.invoke(obj, new Object[0]);
            }
            catch (Exception e) {
                return obj;
            }
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }

    protected HashMap getFieldMap(Class cl) {
        HashMap<String, Field> fieldMap = new HashMap<String, Field>();
        while (cl != null) {
            Field[] fields = cl.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                Field field = fields[i];
                if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                fieldMap.put(field.getName(), field);
            }
            cl = cl.getSuperclass();
        }
        return fieldMap;
    }
}

