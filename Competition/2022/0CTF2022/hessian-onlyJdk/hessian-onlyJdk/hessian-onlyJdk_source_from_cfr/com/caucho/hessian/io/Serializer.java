/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import java.io.IOException;

public interface Serializer {
    public void writeObject(Object var1, AbstractHessianOutput var2) throws IOException;
}

