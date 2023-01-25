/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

public class HessianRuntimeException
extends RuntimeException {
    private Throwable rootCause;

    public HessianRuntimeException() {
    }

    public HessianRuntimeException(String message) {
        super(message);
    }

    public HessianRuntimeException(String message, Throwable rootCause) {
        super(message);
        this.rootCause = rootCause;
    }

    public HessianRuntimeException(Throwable rootCause) {
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

