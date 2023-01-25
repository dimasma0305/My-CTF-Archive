/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarHandle
implements Serializable,
HessianHandle {
    private Class type;
    private Date date;

    public CalendarHandle() {
    }

    public CalendarHandle(Class type, long time) {
        if (!GregorianCalendar.class.equals((Object)type)) {
            this.type = type;
        }
        this.date = new Date(time);
    }

    private Object readResolve() {
        try {
            Calendar cal = this.type != null ? (Calendar)this.type.newInstance() : new GregorianCalendar();
            cal.setTimeInMillis(this.date.getTime());
            return cal;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

