/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import java.util.HashMap;

public class AbstractMapDeserializer
extends AbstractDeserializer {
    public Class getType() {
        return HashMap.class;
    }

    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        if (obj != null) {
            throw this.error("expected map/object at " + obj.getClass().getName() + " (" + obj + ")");
        }
        throw this.error("expected map/object at null");
    }
}

