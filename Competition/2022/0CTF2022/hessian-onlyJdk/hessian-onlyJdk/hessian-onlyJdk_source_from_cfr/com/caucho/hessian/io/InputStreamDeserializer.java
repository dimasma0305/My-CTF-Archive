/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;

public class InputStreamDeserializer
extends AbstractDeserializer {
    public static final InputStreamDeserializer DESER = new InputStreamDeserializer();

    public Object readObject(AbstractHessianInput in) throws IOException {
        return in.readInputStream();
    }
}

