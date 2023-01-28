/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.LocaleHandle;
import java.io.IOException;
import java.util.Locale;

public class LocaleSerializer
extends AbstractSerializer {
    private static LocaleSerializer SERIALIZER = new LocaleSerializer();

    public static LocaleSerializer create() {
        return SERIALIZER;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
        } else {
            Locale locale = (Locale)obj;
            out.writeObject(new LocaleHandle(locale.toString()));
        }
    }
}

