/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.jmx;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;

public class MBeanConstructorInfoDeserializer
extends AbstractDeserializer {
    public Class getType() {
        return MBeanConstructorInfo.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        String name = null;
        String description = null;
        MBeanParameterInfo[] sig = null;
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
            if ("signature".equals(key)) {
                sig = (MBeanParameterInfo[])in.readObject(MBeanParameterInfo[].class);
                continue;
            }
            in.readObject();
        }
        in.readMapEnd();
        try {
            MBeanConstructorInfo info = new MBeanConstructorInfo(name, description, sig);
            return info;
        }
        catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}

