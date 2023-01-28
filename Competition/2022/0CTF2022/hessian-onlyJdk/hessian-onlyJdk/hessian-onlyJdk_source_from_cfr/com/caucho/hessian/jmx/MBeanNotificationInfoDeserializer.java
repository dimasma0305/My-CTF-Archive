/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.jmx;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanNotificationInfo;

public class MBeanNotificationInfoDeserializer
extends AbstractDeserializer {
    public Class getType() {
        return MBeanNotificationInfo.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        String name = null;
        String description = null;
        String[] types = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if ("name".equals(key)) {
                name = in.readString();
                continue;
            }
            if ("description".equals(key)) {
                description = in.readString();
                continue;
            }
            if ("types".equals(key)) {
                types = (String[])in.readObject(String[].class);
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        try {
            MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
            return info;
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

