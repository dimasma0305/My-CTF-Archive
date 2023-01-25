/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.SerializerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class HessianInputFactory {
    public static final Logger log = Logger.getLogger(HessianInputFactory.class.getName());
    private HessianFactory _factory = new HessianFactory();

    public void setSerializerFactory(SerializerFactory factory) {
        this._factory.setSerializerFactory(factory);
    }

    public SerializerFactory getSerializerFactory() {
        return this._factory.getSerializerFactory();
    }

    public HeaderType readHeader(InputStream is) throws IOException {
        int code = is.read();
        int major = is.read();
        int minor = is.read();
        switch (code) {
            case -1: {
                throw new IOException("Unexpected end of file for Hessian message");
            }
            case 99: {
                if (major >= 2) {
                    return HeaderType.CALL_1_REPLY_2;
                }
                return HeaderType.CALL_1_REPLY_1;
            }
            case 114: {
                return HeaderType.REPLY_1;
            }
            case 72: {
                return HeaderType.HESSIAN_2;
            }
        }
        throw new IOException((char)code + " 0x" + Integer.toHexString(code) + " is an unknown Hessian message code.");
    }

    public AbstractHessianInput open(InputStream is) throws IOException {
        int code = is.read();
        int major = is.read();
        int minor = is.read();
        switch (code) {
            case 67: 
            case 82: 
            case 99: 
            case 114: {
                if (major >= 2) {
                    return this._factory.createHessian2Input(is);
                }
                return this._factory.createHessianInput(is);
            }
        }
        throw new IOException((char)code + " is an unknown Hessian message code.");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum HeaderType {
        CALL_1_REPLY_1,
        CALL_1_REPLY_2,
        HESSIAN_2,
        REPLY_1,
        REPLY_2;


        public boolean isCall1() {
            switch (this) {
                case CALL_1_REPLY_1: 
                case CALL_1_REPLY_2: {
                    return true;
                }
            }
            return false;
        }

        public boolean isCall2() {
            switch (this) {
                case HESSIAN_2: {
                    return true;
                }
            }
            return false;
        }

        public boolean isReply1() {
            switch (this) {
                case CALL_1_REPLY_1: {
                    return true;
                }
            }
            return false;
        }

        public boolean isReply2() {
            switch (this) {
                case CALL_1_REPLY_2: 
                case HESSIAN_2: {
                    return true;
                }
            }
            return false;
        }
    }
}

