/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

public class HessianServiceException
extends Exception {
    private String code;
    private Object detail;

    public HessianServiceException() {
    }

    public HessianServiceException(String message, String code, Object detail) {
        super(message);
        this.code = code;
        this.detail = detail;
    }

    public String getCode() {
        return this.code;
    }

    public Object getDetail() {
        return this.detail;
    }
}

