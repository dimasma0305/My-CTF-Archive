/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.io;

import com.caucho.hessian.io.HessianRemoteResolver;
import java.io.IOException;

public interface BurlapRemoteResolver
extends HessianRemoteResolver {
    public Object lookup(String var1, String var2) throws IOException;
}

