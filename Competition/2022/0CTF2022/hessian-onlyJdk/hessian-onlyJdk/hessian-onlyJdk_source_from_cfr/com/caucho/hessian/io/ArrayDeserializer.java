/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractListDeserializer;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ArrayDeserializer
extends AbstractListDeserializer {
    private Class _componentType;
    private Class _type;

    public ArrayDeserializer(Class componentType) {
        this._componentType = componentType;
        if (this._componentType != null) {
            try {
                this._type = Array.newInstance(this._componentType, 0).getClass();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this._type == null) {
            this._type = Object[].class;
        }
    }

    public Class getType() {
        return this._type;
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        if (length >= 0) {
            Object[] data = this.createArray(length);
            in.addRef(data);
            if (this._componentType != null) {
                for (int i = 0; i < data.length; ++i) {
                    data[i] = in.readObject(this._componentType);
                }
            } else {
                for (int i = 0; i < data.length; ++i) {
                    data[i] = in.readObject();
                }
            }
            in.readListEnd();
            return data;
        }
        ArrayList<Object> list = new ArrayList<Object>();
        in.addRef(list);
        if (this._componentType != null) {
            while (!in.isEnd()) {
                list.add(in.readObject(this._componentType));
            }
        } else {
            while (!in.isEnd()) {
                list.add(in.readObject());
            }
        }
        in.readListEnd();
        Object[] data = this.createArray(list.size());
        for (int i = 0; i < data.length; ++i) {
            data[i] = list.get(i);
        }
        return data;
    }

    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        Object[] data = this.createArray(length);
        in.addRef(data);
        if (this._componentType != null) {
            for (int i = 0; i < data.length; ++i) {
                data[i] = in.readObject(this._componentType);
            }
        } else {
            for (int i = 0; i < data.length; ++i) {
                data[i] = in.readObject();
            }
        }
        return data;
    }

    protected Object[] createArray(int length) {
        if (this._componentType != null) {
            return (Object[])Array.newInstance(this._componentType, length);
        }
        return new Object[length];
    }

    public String toString() {
        return "ArrayDeserializer[" + this._componentType + "]";
    }
}

