/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.JavaSerializer;
import java.io.IOException;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JavaUnsharedSerializer
extends JavaSerializer {
    private static final Logger log = Logger.getLogger(JavaUnsharedSerializer.class.getName());

    public JavaUnsharedSerializer(Class<?> cl) {
        super(cl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        boolean oldUnshared = out.setUnshared(true);
        try {
            super.writeObject(obj, out);
        }
        finally {
            out.setUnshared(oldUnshared);
        }
    }
}

