/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.client;

import java.io.IOException;

public class BurlapProtocolException
extends IOException {
    private Throwable rootCause;

    public BurlapProtocolException() {
    }

    public BurlapProtocolException(String message) {
        super(message);
    }

    public BurlapProtocolException(String message, Throwable rootCause) {
        super(message);
        this.rootCause = rootCause;
    }

    public BurlapProtocolException(Throwable rootCause) {
        super(String.valueOf(rootCause));
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return this.rootCause;
    }
}

