/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.HessianRemote;
import com.caucho.hessian.io.HessianRemoteResolver;
import java.io.IOException;

public class AbstractHessianResolver
implements HessianRemoteResolver {
    public Object lookup(String type, String url) throws IOException {
        return new HessianRemote(type, url);
    }
}

