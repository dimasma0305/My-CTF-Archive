/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.jmx;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class ObjectInstanceDeserializer
extends AbstractDeserializer {
    public Class getType() {
        return ObjectInstance.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        String className = null;
        ObjectName objectName = null;
        Object initValue = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if ("className".equals(key)) {
                className = in.readString();
                continue;
            }
            if ("name".equals(key)) {
                objectName = (ObjectName)in.readObject(ObjectName.class);
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        try {
            return new ObjectInstance(objectName, className);
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

