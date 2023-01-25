/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamSerializer
extends AbstractSerializer {
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        InputStream is = (InputStream)obj;
        if (is == null) {
            out.writeNull();
        } else {
            out.writeByteStream(is);
        }
    }
}

