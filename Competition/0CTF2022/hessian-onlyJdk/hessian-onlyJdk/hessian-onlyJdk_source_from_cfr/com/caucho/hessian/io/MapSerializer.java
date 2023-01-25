/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MapSerializer
extends AbstractSerializer {
    private boolean _isSendJavaType = true;

    public void setSendJavaType(boolean sendJavaType) {
        this._isSendJavaType = sendJavaType;
    }

    public boolean getSendJavaType() {
        return this._isSendJavaType;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Map map = (Map)obj;
        Class<?> cl = obj.getClass();
        if (cl.equals(HashMap.class) || !(obj instanceof Serializable)) {
            out.writeMapBegin(null);
        } else if (!this._isSendJavaType) {
            while (cl != null) {
                if (cl.equals(HashMap.class)) {
                    out.writeMapBegin(null);
                    break;
                }
                if (cl.getName().startsWith("java.")) {
                    out.writeMapBegin(cl.getName());
                    break;
                }
                cl = cl.getSuperclass();
            }
            if (cl == null) {
                out.writeMapBegin(null);
            }
        } else {
            out.writeMapBegin(cl.getName());
        }
        for (Map.Entry entry : map.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeMapEnd();
    }
}

