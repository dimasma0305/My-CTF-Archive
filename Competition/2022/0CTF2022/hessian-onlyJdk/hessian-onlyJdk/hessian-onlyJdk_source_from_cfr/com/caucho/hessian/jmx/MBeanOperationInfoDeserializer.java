/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.jmx;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

public class MBeanOperationInfoDeserializer
extends AbstractDeserializer {
    public Class getType() {
        return MBeanOperationInfo.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        String name = null;
        String type = null;
        String description = null;
        MBeanParameterInfo[] sig = null;
        int impact = 0;
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
            if ("type".equals(key)) {
                type = in.readString();
                continue;
            }
            if ("impact".equals(key)) {
                impact = in.readInt();
                continue;
            }
            if ("signature".equals(key)) {
                sig = (MBeanParameterInfo[])in.readObject(MBeanParameterInfo[].class);
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        try {
            MBeanOperationInfo info = new MBeanOperationInfo(name, description, sig, type, impact);
            return info;
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

