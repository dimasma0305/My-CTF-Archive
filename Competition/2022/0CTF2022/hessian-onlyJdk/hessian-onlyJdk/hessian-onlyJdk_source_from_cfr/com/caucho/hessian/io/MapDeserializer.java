/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractMapDeserializer;
import com.caucho.hessian.io.IOExceptionWrapper;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MapDeserializer
extends AbstractMapDeserializer {
    private Class<?> _type;
    private Constructor<?> _ctor;

    public MapDeserializer(Class<?> type) {
        if (type == null) {
            type = HashMap.class;
        }
        this._type = type;
        Constructor<?>[] ctors = type.getConstructors();
        for (int i = 0; i < ctors.length; ++i) {
            if (ctors[i].getParameterTypes().length != 0) continue;
            this._ctor = ctors[i];
        }
        if (this._ctor == null) {
            try {
                this._ctor = HashMap.class.getConstructor(new Class[0]);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public Class<?> getType() {
        if (this._type != null) {
            return this._type;
        }
        return HashMap.class;
    }

    @Override
    public Object readMap(AbstractHessianInput in) throws IOException {
        Map<Object, Object> map;
        if (this._type == null) {
            map = new HashMap<Object, Object>();
        } else if (this._type.equals(Map.class)) {
            map = new HashMap();
        } else if (this._type.equals(SortedMap.class)) {
            map = new TreeMap();
        } else {
            try {
                map = (Map)this._ctor.newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new IOExceptionWrapper(e);
            }
        }
        in.addRef(map);
        while (!in.isEnd()) {
            map.put(in.readObject(), in.readObject());
        }
        in.readEnd();
        return map;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])fields;
        Map map = this.createMap();
        int ref = in.addRef(map);
        for (int i = 0; i < fieldNames.length; ++i) {
            String name = fieldNames[i];
            map.put(name, in.readObject());
        }
        return map;
    }

    private Map createMap() throws IOException {
        if (this._type == null) {
            return new HashMap();
        }
        if (this._type.equals(Map.class)) {
            return new HashMap();
        }
        if (this._type.equals(SortedMap.class)) {
            return new TreeMap();
        }
        try {
            return (Map)this._ctor.newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }
}

