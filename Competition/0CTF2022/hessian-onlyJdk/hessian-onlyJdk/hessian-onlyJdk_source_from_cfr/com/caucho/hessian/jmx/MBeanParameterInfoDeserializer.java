/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.jmx;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanParameterInfo;

public class MBeanParameterInfoDeserializer
extends AbstractDeserializer {
    public Class getType() {
        return MBeanParameterInfo.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        String name = null;
        String type = null;
        String description = null;
        boolean isRead = false;
        boolean isWrite = false;
        boolean isIs = false;
        while (!in.isEnd()) {
            String key = in.readString();
            if ("name".equals(key)) {
                name = in.readString();
                continue;
            }
            if ("type".equals(key)) {
                type = in.readString();
                continue;
            }
            if ("description".equals(key)) {
                description = in.readString();
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        try {
            MBeanParameterInfo info = new MBeanParameterInfo(name, type, description);
            return info;
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

