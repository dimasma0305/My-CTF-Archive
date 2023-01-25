/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.jmx;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanAttributeInfo;

public class MBeanAttributeInfoDeserializer
extends AbstractDeserializer {
    public Class getType() {
        return MBeanAttributeInfo.class;
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
            if ("attributeType".equals(key)) {
                type = in.readString();
                continue;
            }
            if ("description".equals(key)) {
                description = in.readString();
                continue;
            }
            if ("isRead".equals(key)) {
                isRead = in.readBoolean();
                continue;
            }
            if ("isWrite".equals(key)) {
                isWrite = in.readBoolean();
                continue;
            }
            if ("is".equals(key)) {
                isIs = in.readBoolean();
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        try {
            MBeanAttributeInfo info = new MBeanAttributeInfo(name, type, description, isRead, isWrite, isIs);
            return info;
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

