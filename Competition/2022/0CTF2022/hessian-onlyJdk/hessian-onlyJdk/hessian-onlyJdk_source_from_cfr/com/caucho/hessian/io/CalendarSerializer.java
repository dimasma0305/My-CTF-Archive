/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.CalendarHandle;
import com.caucho.hessian.io.Serializer;
import java.util.Calendar;

public class CalendarSerializer
extends AbstractSerializer {
    public static final Serializer SER = new CalendarSerializer();

    public Object writeReplace(Object obj) {
        Calendar cal = (Calendar)obj;
        return new CalendarHandle(cal.getClass(), cal.getTimeInMillis());
    }
}

