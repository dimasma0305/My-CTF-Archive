/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.HessianException;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;
import java.io.IOException;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractSerializer
implements Serializer {
    public static final NullSerializer NULL = new NullSerializer();
    protected static final Logger log = Logger.getLogger(AbstractSerializer.class.getName());

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        try {
            Object replace = this.writeReplace(obj);
            if (replace != null) {
                out.writeObject(replace);
                out.replaceRef(replace, obj);
                return;
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HessianException(e);
        }
        Class<?> cl = this.getClass(obj);
        int ref = out.writeObjectBegin(cl.getName());
        if (ref < -1) {
            this.writeObject10(obj, out);
        } else {
            if (ref == -1) {
                this.writeDefinition20(cl, out);
                out.writeObjectBegin(cl.getName());
            }
            this.writeInstance(obj, out);
        }
    }

    protected Object writeReplace(Object obj) {
        return null;
    }

    protected Class<?> getClass(Object obj) {
        return obj.getClass();
    }

    protected void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    protected void writeDefinition20(Class<?> cl, AbstractHessianOutput out) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    protected void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    static final class NullSerializer
    extends AbstractSerializer {
        NullSerializer() {
        }

        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            throw new IllegalStateException(this.getClass().getName());
        }
    }
}

