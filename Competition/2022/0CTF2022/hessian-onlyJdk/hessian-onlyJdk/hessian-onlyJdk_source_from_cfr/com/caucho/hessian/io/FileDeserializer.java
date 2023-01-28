/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractStringValueDeserializer;
import java.io.File;

public class FileDeserializer
extends AbstractStringValueDeserializer {
    public Class getType() {
        return File.class;
    }

    protected Object create(String value) {
        return new File(value);
    }
}

