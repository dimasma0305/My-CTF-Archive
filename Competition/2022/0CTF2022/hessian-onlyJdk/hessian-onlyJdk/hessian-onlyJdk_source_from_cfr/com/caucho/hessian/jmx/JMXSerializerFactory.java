/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.jmx;

import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.StringValueDeserializer;
import com.caucho.hessian.io.StringValueSerializer;
import com.caucho.hessian.jmx.MBeanAttributeInfoDeserializer;
import com.caucho.hessian.jmx.MBeanConstructorInfoDeserializer;
import com.caucho.hessian.jmx.MBeanNotificationInfoDeserializer;
import com.caucho.hessian.jmx.MBeanOperationInfoDeserializer;
import com.caucho.hessian.jmx.MBeanParameterInfoDeserializer;
import com.caucho.hessian.jmx.ObjectInstanceDeserializer;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class JMXSerializerFactory
extends AbstractSerializerFactory {
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (ObjectName.class.equals((Object)cl)) {
            return new StringValueSerializer();
        }
        return null;
    }

    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (ObjectName.class.equals((Object)cl)) {
            return new StringValueDeserializer(cl);
        }
        if (ObjectInstance.class.equals((Object)cl)) {
            return new ObjectInstanceDeserializer();
        }
        if (MBeanAttributeInfo.class.isAssignableFrom(cl)) {
            return new MBeanAttributeInfoDeserializer();
        }
        if (MBeanConstructorInfo.class.isAssignableFrom(cl)) {
            return new MBeanConstructorInfoDeserializer();
        }
        if (MBeanOperationInfo.class.isAssignableFrom(cl)) {
            return new MBeanOperationInfoDeserializer();
        }
        if (MBeanParameterInfo.class.isAssignableFrom(cl)) {
            return new MBeanParameterInfoDeserializer();
        }
        if (MBeanNotificationInfo.class.isAssignableFrom(cl)) {
            return new MBeanNotificationInfoDeserializer();
        }
        return null;
    }
}

