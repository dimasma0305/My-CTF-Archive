/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.HessianException;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractMapDeserializer;
import com.caucho.hessian.io.AnnotationInvocationHandler;
import com.caucho.hessian.io.IOExceptionWrapper;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.logging.Logger;

public class AnnotationDeserializer
extends AbstractMapDeserializer {
    private static final Logger log = Logger.getLogger(AnnotationDeserializer.class.getName());
    private Class _annType;

    public AnnotationDeserializer(Class annType) {
        this._annType = annType;
    }

    public Class getType() {
        return this._annType;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        try {
            int ref = in.addRef(null);
            HashMap<String, Object> valueMap = new HashMap<String, Object>(8);
            while (!in.isEnd()) {
                String key = in.readString();
                Object value = in.readObject();
                valueMap.put(key, value);
            }
            in.readMapEnd();
            return Proxy.newProxyInstance(this._annType.getClassLoader(), new Class[]{this._annType}, (InvocationHandler)new AnnotationInvocationHandler(this._annType, valueMap));
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])fields;
        try {
            in.addRef(null);
            HashMap<String, Object> valueMap = new HashMap<String, Object>(8);
            for (int i = 0; i < fieldNames.length; ++i) {
                String name = fieldNames[i];
                valueMap.put(name, in.readObject());
            }
            return Proxy.newProxyInstance(this._annType.getClassLoader(), new Class[]{this._annType}, (InvocationHandler)new AnnotationInvocationHandler(this._annType, valueMap));
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HessianException(this._annType.getName() + ":" + e, e);
        }
    }
}

