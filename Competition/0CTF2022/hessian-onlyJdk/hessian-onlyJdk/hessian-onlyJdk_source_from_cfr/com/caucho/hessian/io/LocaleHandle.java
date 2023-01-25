/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.util.Locale;

public class LocaleHandle
implements Serializable,
HessianHandle {
    private String value;

    public LocaleHandle(String locale) {
        this.value = locale;
    }

    private Object readResolve() {
        int head;
        int i;
        String s = this.value;
        if (s == null) {
            return null;
        }
        int len = s.length();
        int ch = 32;
        for (i = 0; i < len; ++i) {
            char c = s.charAt(i);
            ch = c;
            if (!('a' <= c && ch <= 122 || 65 <= ch && ch <= 90) && (48 > ch || ch > 57)) break;
        }
        String language = s.substring(0, i);
        String country = null;
        String var = null;
        if (ch == 45 || ch == 95) {
            head = ++i;
            while (i < len) {
                char c = s.charAt(i);
                ch = c;
                if (!('a' <= c && ch <= 122 || 65 <= ch && ch <= 90) && (48 > ch || ch > 57)) break;
                ++i;
            }
            country = s.substring(head, i);
        }
        if (ch == 45 || ch == 95) {
            head = ++i;
            while (i < len) {
                char c = s.charAt(i);
                ch = c;
                if (!('a' <= c && ch <= 122 || 65 <= ch && ch <= 90) && (48 > ch || ch > 57)) break;
                ++i;
            }
            var = s.substring(head, i);
        }
        if (var != null) {
            return new Locale(language, country, var);
        }
        if (country != null) {
            return new Locale(language, country);
        }
        return new Locale(language);
    }
}

