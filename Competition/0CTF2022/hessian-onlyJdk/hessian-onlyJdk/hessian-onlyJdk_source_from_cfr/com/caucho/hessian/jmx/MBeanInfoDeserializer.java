/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.jmx;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;

public class MBeanInfoDeserializer
extends AbstractDeserializer {
    public Class getType() {
        return MBeanInfo.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        String className = null;
        String description = null;
        MBeanAttributeInfo[] attributes = null;
        MBeanConstructorInfo[] constructors = null;
        MBeanOperationInfo[] operations = null;
        MBeanNotificationInfo[] notifications = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if ("className".equals(key)) {
                className = in.readString();
                continue;
            }
            if ("description".equals(key)) {
                description = in.readString();
                continue;
            }
            if ("attributes".equals(key)) {
                attributes = (MBeanAttributeInfo[])in.readObject(MBeanAttributeInfo[].class);
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        try {
            MBeanInfo info = new MBeanInfo(className, description, attributes, constructors, operations, notifications);
            return info;
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

