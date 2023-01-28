/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionSerializer
extends AbstractSerializer {
    private boolean _sendJavaType = true;

    public void setSendJavaType(boolean sendJavaType) {
        this._sendJavaType = sendJavaType;
    }

    public boolean getSendJavaType() {
        return this._sendJavaType;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        boolean hasEnd;
        if (out.addRef(obj)) {
            return;
        }
        Collection list = (Collection)obj;
        Class<?> cl = obj.getClass();
        if (cl.equals(ArrayList.class) || !Serializable.class.isAssignableFrom(cl)) {
            hasEnd = out.writeListBegin(list.size(), null);
        } else if (!this._sendJavaType) {
            hasEnd = false;
            while (cl != null) {
                if (cl.getName().startsWith("java.")) {
                    hasEnd = out.writeListBegin(list.size(), cl.getName());
                    break;
                }
                cl = cl.getSuperclass();
            }
            if (cl == null) {
                hasEnd = out.writeListBegin(list.size(), null);
            }
        } else {
            hasEnd = out.writeListBegin(list.size(), obj.getClass().getName());
        }
        for (Object value : list) {
            out.writeObject(value);
        }
        if (hasEnd) {
            out.writeListEnd();
        }
    }
}

