/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.JavaDeserializer;

public class StackTraceElementDeserializer
extends JavaDeserializer {
    public StackTraceElementDeserializer() {
        super(StackTraceElement.class);
    }

    protected Object instantiate() throws Exception {
        return new StackTraceElement("", "", "", 0);
    }
}

