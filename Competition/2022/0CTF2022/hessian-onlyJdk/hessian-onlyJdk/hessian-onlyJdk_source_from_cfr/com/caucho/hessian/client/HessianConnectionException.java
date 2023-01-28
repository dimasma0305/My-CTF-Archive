/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.HessianException;

public class HessianConnectionException
extends HessianException {
    public HessianConnectionException() {
    }

    public HessianConnectionException(String message) {
        super(message);
    }

    public HessianConnectionException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public HessianConnectionException(Throwable rootCause) {
        super(rootCause);
    }
}

