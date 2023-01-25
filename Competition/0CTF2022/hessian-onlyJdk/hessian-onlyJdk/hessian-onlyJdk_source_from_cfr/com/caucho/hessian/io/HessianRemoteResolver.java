/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import java.io.IOException;

public interface HessianRemoteResolver {
    public Object lookup(String var1, String var2) throws IOException;
}

