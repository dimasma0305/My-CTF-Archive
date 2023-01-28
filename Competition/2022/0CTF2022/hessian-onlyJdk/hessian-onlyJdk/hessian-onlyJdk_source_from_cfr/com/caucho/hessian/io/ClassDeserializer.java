/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractMapDeserializer;
import com.caucho.hessian.io.IOExceptionWrapper;
import java.io.IOException;
import java.util.HashMap;

public class ClassDeserializer
extends AbstractMapDeserializer {
    private static final HashMap<String, Class> _primClasses = new HashMap();
    private ClassLoader _loader;

    public ClassDeserializer(ClassLoader loader) {
        this._loader = loader;
    }

    public Class getType() {
        return Class.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        int ref = in.addRef(null);
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
        Object value = this.create(name);
        in.setRef(ref, value);
        return value;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])fields;
        int ref = in.addRef(null);
        String name = null;
        for (int i = 0; i < fieldNames.length; ++i) {
            if ("name".equals(fieldNames[i])) {
                name = in.readString();
                continue;
            }
            in.readObject();
        }
        Object value = this.create(name);
        in.setRef(ref, value);
        return value;
    }

    Object create(String name) throws IOException {
        if (name == null) {
            throw new IOException("Serialized Class expects name.");
        }
        Class cl = _primClasses.get(name);
        if (cl != null) {
            return cl;
        }
        try {
            if (this._loader != null) {
                return Class.forName(name, false, this._loader);
            }
            return Class.forName(name);
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }

    static {
        _primClasses.put("void", Void.TYPE);
        _primClasses.put("boolean", Boolean.TYPE);
        _primClasses.put("java.lang.Boolean", Boolean.class);
        _primClasses.put("byte", Byte.TYPE);
        _primClasses.put("java.lang.Byte", Byte.class);
        _primClasses.put("char", Character.TYPE);
        _primClasses.put("java.lang.Character", Character.class);
        _primClasses.put("short", Short.TYPE);
        _primClasses.put("java.lang.Short", Short.class);
        _primClasses.put("int", Integer.TYPE);
        _primClasses.put("java.lang.Integer", Integer.class);
        _primClasses.put("long", Long.TYPE);
        _primClasses.put("java.lang.Long", Long.class);
        _primClasses.put("float", Float.TYPE);
        _primClasses.put("java.lang.Float", Float.class);
        _primClasses.put("double", Double.TYPE);
        _primClasses.put("java.lang.Double", Double.class);
        _primClasses.put("java.lang.String", String.class);
    }
}

