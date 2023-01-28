/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.HessianException;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.HessianMethodSerializationException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationSerializer
extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(AnnotationSerializer.class.getName());
    private static Object[] NULL_ARGS = new Object[0];
    private Class _annType;
    private Method[] _methods;
    private MethodSerializer[] _methodSerializers;

    public AnnotationSerializer(Class annType) {
        if (!Annotation.class.isAssignableFrom(annType)) {
            throw new IllegalStateException(annType.getName() + " is invalid because it is not a java.lang.annotation.Annotation");
        }
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        this.init(((Annotation)obj).annotationType());
        int ref = out.writeObjectBegin(this._annType.getName());
        if (ref < -1) {
            this.writeObject10(obj, out);
        } else {
            if (ref == -1) {
                this.writeDefinition20(out);
                out.writeObjectBegin(this._annType.getName());
            }
            this.writeInstance(obj, out);
        }
    }

    protected void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        for (int i = 0; i < this._methods.length; ++i) {
            Method method = this._methods[i];
            out.writeString(method.getName());
            this._methodSerializers[i].serialize(out, obj, method);
        }
        out.writeMapEnd();
    }

    private void writeDefinition20(AbstractHessianOutput out) throws IOException {
        out.writeClassFieldLength(this._methods.length);
        for (int i = 0; i < this._methods.length; ++i) {
            Method method = this._methods[i];
            out.writeString(method.getName());
        }
    }

    public void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        for (int i = 0; i < this._methods.length; ++i) {
            Method method = this._methods[i];
            this._methodSerializers[i].serialize(out, obj, method);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void init(Class cl) {
        AnnotationSerializer annotationSerializer = this;
        synchronized (annotationSerializer) {
            if (this._annType != null) {
                return;
            }
            this._annType = cl;
            ArrayList<Method> methods = new ArrayList<Method>();
            for (Method method : this._annType.getDeclaredMethods()) {
                if (method.getName().equals("hashCode") || method.getName().equals("toString") || method.getName().equals("annotationType") || method.getParameterTypes().length != 0) continue;
                methods.add(method);
                method.setAccessible(true);
            }
            if (this._annType == null) {
                throw new IllegalStateException(cl.getName() + " is invalid because it does not have a valid annotationType()");
            }
            this._methods = new Method[methods.size()];
            methods.toArray(this._methods);
            this._methodSerializers = new MethodSerializer[this._methods.length];
            for (int i = 0; i < this._methods.length; ++i) {
                this._methodSerializers[i] = AnnotationSerializer.getMethodSerializer(this._methods[i].getReturnType());
            }
        }
    }

    private Class getAnnotationType(Class cl) {
        if (cl == null) {
            return null;
        }
        if (Annotation.class.equals(cl.getSuperclass())) {
            return cl;
        }
        Class<?>[] ifaces = cl.getInterfaces();
        if (ifaces != null) {
            for (Class<?> iface : ifaces) {
                if (iface.equals(Annotation.class)) {
                    return cl;
                }
                Class annType = this.getAnnotationType(iface);
                if (annType == null) continue;
                return annType;
            }
        }
        return this.getAnnotationType(cl.getSuperclass());
    }

    private static MethodSerializer getMethodSerializer(Class type) {
        if (Integer.TYPE.equals(type) || Byte.TYPE.equals(type) || Short.TYPE.equals(type) || Integer.TYPE.equals(type)) {
            return IntMethodSerializer.SER;
        }
        if (Long.TYPE.equals(type)) {
            return LongMethodSerializer.SER;
        }
        if (Double.TYPE.equals(type) || Float.TYPE.equals(type)) {
            return DoubleMethodSerializer.SER;
        }
        if (Boolean.TYPE.equals(type)) {
            return BooleanMethodSerializer.SER;
        }
        if (String.class.equals((Object)type)) {
            return StringMethodSerializer.SER;
        }
        if (java.util.Date.class.equals((Object)type) || Date.class.equals((Object)type) || Timestamp.class.equals((Object)type) || Time.class.equals((Object)type)) {
            return DateMethodSerializer.SER;
        }
        return MethodSerializer.SER;
    }

    static HessianException error(Method method, Throwable cause) {
        String msg = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(): " + cause;
        throw new HessianMethodSerializationException(msg, cause);
    }

    static class DateMethodSerializer
    extends MethodSerializer {
        static final MethodSerializer SER = new DateMethodSerializer();

        DateMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            java.util.Date value = null;
            try {
                value = (java.util.Date)method.invoke(obj, new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw AnnotationSerializer.error(method, e.getCause());
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            if (value == null) {
                out.writeNull();
            } else {
                out.writeUTCDate(value.getTime());
            }
        }
    }

    static class StringMethodSerializer
    extends MethodSerializer {
        static final MethodSerializer SER = new StringMethodSerializer();

        StringMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            String value = null;
            try {
                value = (String)method.invoke(obj, new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw AnnotationSerializer.error(method, e.getCause());
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeString(value);
        }
    }

    static class DoubleMethodSerializer
    extends MethodSerializer {
        static final MethodSerializer SER = new DoubleMethodSerializer();

        DoubleMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            double value = 0.0;
            try {
                value = (Double)method.invoke(obj, new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw AnnotationSerializer.error(method, e.getCause());
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeDouble(value);
        }
    }

    static class LongMethodSerializer
    extends MethodSerializer {
        static final MethodSerializer SER = new LongMethodSerializer();

        LongMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            long value = 0L;
            try {
                value = (Long)method.invoke(obj, new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw AnnotationSerializer.error(method, e.getCause());
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeLong(value);
        }
    }

    static class IntMethodSerializer
    extends MethodSerializer {
        static final MethodSerializer SER = new IntMethodSerializer();

        IntMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            int value = 0;
            try {
                value = (Integer)method.invoke(obj, new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw AnnotationSerializer.error(method, e.getCause());
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeInt(value);
        }
    }

    static class BooleanMethodSerializer
    extends MethodSerializer {
        static final MethodSerializer SER = new BooleanMethodSerializer();

        BooleanMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            boolean value = false;
            try {
                value = (Boolean)method.invoke(obj, new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw AnnotationSerializer.error(method, e.getCause());
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeBoolean(value);
        }
    }

    static class MethodSerializer {
        static final MethodSerializer SER = new MethodSerializer();

        MethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            Object value = null;
            try {
                value = method.invoke(obj, new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw AnnotationSerializer.error(method, e.getCause());
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            try {
                out.writeObject(value);
            }
            catch (Exception e) {
                throw AnnotationSerializer.error(method, e);
            }
        }
    }
}

