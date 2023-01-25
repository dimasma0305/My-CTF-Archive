/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.client;

public class BurlapRuntimeException
extends RuntimeException {
    private Throwable rootCause;

    public BurlapRuntimeException() {
    }

    public BurlapRuntimeException(String message) {
        super(message);
    }

    public BurlapRuntimeException(String message, Throwable rootCause) {
        super(message);
        this.rootCause = rootCause;
    }

    public BurlapRuntimeException(Throwable rootCause) {
        super(String.valueOf(rootCause));
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return this.rootCause;
    }

    public Throwable getCause() {
        return this.rootCause;
    }
}

