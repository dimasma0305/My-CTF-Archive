/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractListDeserializer;
import java.io.IOException;
import java.util.Vector;

public class EnumerationDeserializer
extends AbstractListDeserializer {
    private static EnumerationDeserializer _deserializer;

    public static EnumerationDeserializer create() {
        if (_deserializer == null) {
            _deserializer = new EnumerationDeserializer();
        }
        return _deserializer;
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        Vector<Object> list = new Vector<Object>();
        in.addRef(list);
        while (!in.isEnd()) {
            list.add(in.readObject());
        }
        in.readEnd();
        return list.elements();
    }
}

