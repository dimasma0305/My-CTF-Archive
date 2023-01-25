/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.BeanDeserializer;
import com.caucho.hessian.io.BeanSerializer;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SerializerFactory;

public class BeanSerializerFactory
extends SerializerFactory {
    protected Serializer getDefaultSerializer(Class cl) {
        return new BeanSerializer(cl, this.getClassLoader());
    }

    protected Deserializer getDefaultDeserializer(Class cl) {
        return new BeanDeserializer(cl);
    }
}

