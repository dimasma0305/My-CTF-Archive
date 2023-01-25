/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.client;

public class BurlapServiceException
extends Exception {
    private String code;
    private Object detail;

    public BurlapServiceException() {
    }

    public BurlapServiceException(String message, String code, Object detail) {
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

