/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;

public abstract class AbstractSerializerFactory {
    public abstract Serializer getSerializer(Class var1) throws HessianProtocolException;

    public abstract Deserializer getDeserializer(Class var1) throws HessianProtocolException;
}

