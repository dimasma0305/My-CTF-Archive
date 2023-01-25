/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;

public abstract class ValueDeserializer
extends AbstractDeserializer {
    public Object readMap(AbstractHessianInput in) throws IOException {
        String initValue = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if (key.equals("value")) {
                initValue = in.readString();
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        return this.create(initValue);
    }

    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        String initValue = null;
        for (int i = 0; i < fieldNames.length; ++i) {
            if ("value".equals(fieldNames[i])) {
                initValue = in.readString();
                continue;
            }
            in.readObject();
        }
        return this.create(initValue);
    }

    abstract Object create(String var1) throws IOException;
}

