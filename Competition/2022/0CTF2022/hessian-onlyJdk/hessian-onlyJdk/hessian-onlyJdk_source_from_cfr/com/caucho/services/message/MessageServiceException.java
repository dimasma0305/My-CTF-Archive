/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.services.message;

import java.io.IOException;

public class MessageServiceException
extends IOException {
    private Throwable _rootCause;

    public MessageServiceException() {
    }

    public MessageServiceException(String message) {
        super(message);
    }

    public MessageServiceException(String message, Throwable rootCause) {
        super(message);
        this._rootCause = rootCause;
    }

    public MessageServiceException(Throwable rootCause) {
        super(String.valueOf(rootCause));
        this._rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return this.getCause();
    }

    public Throwable getCause() {
        return this._rootCause;
    }
}

