/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.HessianException;
import com.caucho.hessian.io.AbstractStringValueDeserializer;
import javax.management.ObjectName;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ObjectNameDeserializer
extends AbstractStringValueDeserializer {
    @Override
    public Class<?> getType() {
        return ObjectName.class;
    }

    @Override
    protected Object create(String value) {
        try {
            return new ObjectName(value);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HessianException(e);
        }
    }
}

