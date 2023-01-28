/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.io;

import com.caucho.hessian.io.HessianProtocolException;

public class BurlapProtocolException
extends HessianProtocolException {
    public BurlapProtocolException() {
    }

    public BurlapProtocolException(String message) {
        super(message);
    }

    public BurlapProtocolException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public BurlapProtocolException(Throwable rootCause) {
        super(rootCause);
    }
}

