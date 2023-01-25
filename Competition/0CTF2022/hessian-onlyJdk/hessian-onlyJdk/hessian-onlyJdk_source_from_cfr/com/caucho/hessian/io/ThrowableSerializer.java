/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.JavaSerializer;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ThrowableSerializer
extends JavaSerializer {
    public ThrowableSerializer(Class<?> cl, ClassLoader loader) {
        super(cl);
    }

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        Throwable e = (Throwable)obj;
        e.getStackTrace();
        super.writeObject(obj, out);
    }
}

