/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AbstractDeserializer
implements Deserializer {
    public static final NullDeserializer NULL = new NullDeserializer();

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public boolean isReadResolve() {
        return false;
    }

    @Override
    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        String className = this.getClass().getName();
        if (obj != null) {
            throw this.error(className + ": unexpected object " + obj.getClass().getName() + " (" + obj + ")");
        }
        throw this.error(className + ": unexpected null value");
    }

    @Override
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override
    public Object readMap(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        String className = this.getClass().getName();
        if (obj != null) {
            throw this.error(className + ": unexpected object " + obj.getClass().getName() + " (" + obj + ")");
        }
        throw this.error(className + ": unexpected null value");
    }

    @Override
    public Object[] createFields(int len) {
        return new String[len];
    }

    @Override
    public Object createField(String name) {
        return name;
    }

    @Override
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        return this.readObject(in, (Object[])fieldNames);
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        throw new UnsupportedOperationException(this.toString());
    }

    protected HessianProtocolException error(String msg) {
        return new HessianProtocolException(msg);
    }

    protected String codeName(int ch) {
        if (ch < 0) {
            return "end of file";
        }
        return "0x" + Integer.toHexString(ch & 0xFF);
    }

    static final class NullDeserializer
    extends AbstractDeserializer {
        NullDeserializer() {
        }
    }
}

