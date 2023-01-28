/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public abstract class AbstractStreamSerializer
extends AbstractSerializer {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        int ref = out.writeObjectBegin(this.getClassName(obj));
        if (ref < -1) {
            out.writeString("value");
            InputStream is = null;
            try {
                is = this.getInputStream(obj);
            }
            catch (Exception e) {
                log.log(Level.WARNING, e.toString(), e);
            }
            if (is != null) {
                try {
                    out.writeByteStream(is);
                }
                finally {
                    is.close();
                }
            } else {
                out.writeNull();
            }
            out.writeMapEnd();
        } else {
            if (ref == -1) {
                out.writeClassFieldLength(1);
                out.writeString("value");
                out.writeObjectBegin(this.getClassName(obj));
            }
            InputStream is = null;
            try {
                is = this.getInputStream(obj);
            }
            catch (Exception e) {
                log.log(Level.WARNING, e.toString(), e);
            }
            try {
                if (is != null) {
                    out.writeByteStream(is);
                } else {
                    out.writeNull();
                }
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }

    protected String getClassName(Object obj) {
        return obj.getClass().getName();
    }

    protected abstract InputStream getInputStream(Object var1) throws IOException;
}

