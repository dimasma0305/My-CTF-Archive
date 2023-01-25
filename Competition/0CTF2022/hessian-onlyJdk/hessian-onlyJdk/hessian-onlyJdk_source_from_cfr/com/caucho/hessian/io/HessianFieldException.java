/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.HessianProtocolException;

public class HessianFieldException
extends HessianProtocolException {
    public HessianFieldException() {
    }

    public HessianFieldException(String message) {
        super(message);
    }

    public HessianFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public HessianFieldException(Throwable cause) {
        super(cause);
    }
}

