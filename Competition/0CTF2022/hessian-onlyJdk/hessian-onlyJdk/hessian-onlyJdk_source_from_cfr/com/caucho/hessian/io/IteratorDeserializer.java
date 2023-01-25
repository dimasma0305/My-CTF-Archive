/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractListDeserializer;
import java.io.IOException;
import java.util.ArrayList;

public class IteratorDeserializer
extends AbstractListDeserializer {
    private static IteratorDeserializer _deserializer;

    public static IteratorDeserializer create() {
        if (_deserializer == null) {
            _deserializer = new IteratorDeserializer();
        }
        return _deserializer;
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        ArrayList<Object> list = new ArrayList<Object>();
        in.addRef(list);
        while (!in.isEnd()) {
            list.add(in.readObject());
        }
        in.readEnd();
        return list.iterator();
    }
}

