/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.services.name;

import java.io.IOException;

public class NameServiceException
extends IOException {
    private Throwable rootCause;

    public NameServiceException() {
    }

    public NameServiceException(String name) {
        super(name);
    }

    public NameServiceException(String name, Throwable rootCause) {
        super(name);
        this.rootCause = rootCause;
    }

    public NameServiceException(Throwable rootCause) {
        super(String.valueOf(rootCause));
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return this.rootCause;
    }
}

