/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AnnotationInvocationHandler
implements InvocationHandler {
    private Class _annType;
    private HashMap<String, Object> _valueMap;

    public AnnotationInvocationHandler(Class annType, HashMap<String, Object> valueMap) {
        this._annType = annType;
        this._valueMap = valueMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean zeroArgs;
        String name = method.getName();
        boolean bl = zeroArgs = args == null || args.length == 0;
        if (name.equals("annotationType") && zeroArgs) {
            return this._annType;
        }
        if (name.equals("toString") && zeroArgs) {
            return this.toString();
        }
        if (name.equals("hashCode") && zeroArgs) {
            return this.doHashCode();
        }
        if (name.equals("equals") && !zeroArgs && args.length == 1) {
            return this.doEquals(args[0]);
        }
        if (!zeroArgs) {
            return null;
        }
        return this._valueMap.get(method.getName());
    }

    public int doHashCode() {
        return 13;
    }

    public boolean doEquals(Object value) {
        if (!(value instanceof Annotation)) {
            return false;
        }
        Annotation ann = (Annotation)value;
        return this._annType.equals(ann.annotationType());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(this._annType.getName());
        sb.append("[");
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : this._valueMap.entrySet()) {
            if (!isFirst) {
                sb.append(", ");
            }
            isFirst = false;
            sb.append((Object)entry.getKey());
            sb.append("=");
            if (entry.getValue() instanceof String) {
                sb.append('\"').append(entry.getValue()).append('\"');
                continue;
            }
            sb.append(entry.getValue());
        }
        sb.append("]");
        return sb.toString();
    }
}

