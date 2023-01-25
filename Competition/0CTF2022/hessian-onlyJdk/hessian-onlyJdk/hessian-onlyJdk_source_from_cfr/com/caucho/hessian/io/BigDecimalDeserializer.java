/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractStringValueDeserializer;
import java.math.BigDecimal;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BigDecimalDeserializer
extends AbstractStringValueDeserializer {
    @Override
    public Class<?> getType() {
        return BigDecimal.class;
    }

    @Override
    protected Object create(String value) {
        return new BigDecimal(value);
    }
}

