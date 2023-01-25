/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Deserializer {
    public Class<?> getType();

    public boolean isReadResolve();

    public Object readObject(AbstractHessianInput var1) throws IOException;

    public Object readList(AbstractHessianInput var1, int var2) throws IOException;

    public Object readLengthList(AbstractHessianInput var1, int var2) throws IOException;

    public Object readMap(AbstractHessianInput var1) throws IOException;

    public Object[] createFields(int var1);

    public Object createField(String var1);

    public Object readObject(AbstractHessianInput var1, Object[] var2) throws IOException;

    public Object readObject(AbstractHessianInput var1, String[] var2) throws IOException;
}

