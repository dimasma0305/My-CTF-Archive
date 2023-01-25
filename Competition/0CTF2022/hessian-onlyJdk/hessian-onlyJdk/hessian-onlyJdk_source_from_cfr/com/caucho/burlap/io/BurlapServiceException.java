/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.io;

import com.caucho.hessian.io.HessianServiceException;

public class BurlapServiceException
extends HessianServiceException {
    public BurlapServiceException() {
    }

    public BurlapServiceException(String message, String code, Object detail) {
        super(message, code, detail);
    }
}

