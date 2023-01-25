/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import java.io.IOException;

public class HessianProtocolException
extends IOException {
    private Throwable rootCause;

    public HessianProtocolException() {
    }

    public HessianProtocolException(String message) {
        super(message);
    }

    public HessianProtocolException(String message, Throwable rootCause) {
        super(message);
        this.rootCause = rootCause;
    }

    public HessianProtocolException(Throwable rootCause) {
        super(String.valueOf(rootCause));
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return this.rootCause;
    }

    public Throwable getCause() {
        return this.getRootCause();
    }
}

