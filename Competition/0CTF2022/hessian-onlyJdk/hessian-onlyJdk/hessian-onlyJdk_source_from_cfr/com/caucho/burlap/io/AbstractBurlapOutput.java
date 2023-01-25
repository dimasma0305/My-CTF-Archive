/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import java.io.IOException;

public abstract class AbstractBurlapOutput
extends AbstractHessianOutput {
    public void startCall(String method, int length) throws IOException {
        this.startCall(method);
    }

    abstract void startCall(String var1) throws IOException;
}

