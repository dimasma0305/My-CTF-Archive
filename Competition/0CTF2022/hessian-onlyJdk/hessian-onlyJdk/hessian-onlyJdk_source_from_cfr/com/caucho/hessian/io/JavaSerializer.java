/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.HessianUnshared;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.IOExceptionWrapper;
import com.caucho.hessian.io.JavaUnsharedSerializer;
import com.caucho.hessian.io.Serializer;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JavaSerializer
extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(JavaSerializer.class.getName());
    private static final WeakHashMap<Class<?>, SoftReference<JavaSerializer>> _serializerMap = new WeakHashMap();
    private Field[] _fields;
    private FieldSerializer[] _fieldSerializers;
    private Object _writeReplaceFactory;
    private Method _writeReplace;

    public JavaSerializer(Class<?> cl) {
        this.introspect(cl);
        this._writeReplace = JavaSerializer.getWriteReplace(cl);
        if (this._writeReplace != null) {
            this._writeReplace.setAccessible(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Serializer create(Class<?> cl) {
        WeakHashMap<Class<?>, SoftReference<JavaSerializer>> weakHashMap = _serializerMap;
        synchronized (weakHashMap) {
            JavaSerializer base;
            SoftReference<JavaSerializer> baseRef = _serializerMap.get(cl);
            JavaSerializer javaSerializer = base = baseRef != null ? baseRef.get() : null;
            if (base == null) {
                base = cl.isAnnotationPresent(HessianUnshared.class) ? new JavaUnsharedSerializer(cl) : new JavaSerializer(cl);
                baseRef = new SoftReference<JavaSerializer>(base);
                _serializerMap.put(cl, baseRef);
            }
            return base;
        }
    }

    protected void introspect(Class<?> cl) {
        int i;
        Object fields;
        if (this._writeReplace != null) {
            this._writeReplace.setAccessible(true);
        }
        ArrayList<Object> primitiveFields = new ArrayList<Object>();
        ArrayList<Object> compoundFields = new ArrayList<Object>();
        while (cl != null) {
            fields = cl.getDeclaredFields();
            for (i = 0; i < ((Object)fields).length; ++i) {
                Object field = fields[i];
                if (Modifier.isTransient(((Field)field).getModifiers()) || Modifier.isStatic(((Field)field).getModifiers())) continue;
                ((Field)field).setAccessible(true);
                if (((Field)field).getType().isPrimitive() || ((Field)field).getType().getName().startsWith("java.lang.") && !((Field)field).getType().equals(Object.class)) {
                    primitiveFields.add(field);
                    continue;
                }
                compoundFields.add(field);
            }
            cl = cl.getSuperclass();
        }
        fields = new ArrayList();
        ((ArrayList)fields).addAll(primitiveFields);
        ((ArrayList)fields).addAll(compoundFields);
        this._fields = new Field[((ArrayList)fields).size()];
        ((ArrayList)fields).toArray(this._fields);
        this._fieldSerializers = new FieldSerializer[this._fields.length];
        for (i = 0; i < this._fields.length; ++i) {
            this._fieldSerializers[i] = JavaSerializer.getFieldSerializer(this._fields[i].getType());
        }
    }

    protected static Method getWriteReplace(Class<?> cl) {
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (int i = 0; i < methods.length; ++i) {
                Method method = methods[i];
                if (!method.getName().equals("writeReplace") || method.getParameterTypes().length != 0) continue;
                return method;
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    protected Method getWriteReplace(Class<?> cl, Class<?> param) {
        while (cl != null) {
            for (Method method : cl.getDeclaredMethods()) {
                if (!method.getName().equals("writeReplace") || method.getParameterTypes().length != 1 || !param.equals(method.getParameterTypes()[0])) continue;
                return method;
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Class<?> cl = obj.getClass();
        try {
            if (this._writeReplace != null) {
                Object repl = this._writeReplaceFactory != null ? this._writeReplace.invoke(this._writeReplaceFactory, obj) : this._writeReplace.invoke(obj, new Object[0]);
                int ref = out.writeObjectBegin(cl.getName());
                if (ref < -1) {
                    this.writeObject10(repl, out);
                } else {
                    if (ref == -1) {
                        this.writeDefinition20(out);
                        out.writeObjectBegin(cl.getName());
                    }
                    this.writeInstance(repl, out);
                }
                return;
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        int ref = out.writeObjectBegin(cl.getName());
        if (ref < -1) {
            this.writeObject10(obj, out);
        } else {
            if (ref == -1) {
                this.writeDefinition20(out);
                out.writeObjectBegin(cl.getName());
            }
            this.writeInstance(obj, out);
        }
    }

    @Override
    protected void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        for (int i = 0; i < this._fields.length; ++i) {
            Field field = this._fields[i];
            out.writeString(field.getName());
            this._fieldSerializers[i].serialize(out, obj, field);
        }
        out.writeMapEnd();
    }

    private void writeDefinition20(AbstractHessianOutput out) throws IOException {
        out.writeClassFieldLength(this._fields.length);
        for (int i = 0; i < this._fields.length; ++i) {
            Field field = this._fields[i];
            out.writeString(field.getName());
        }
    }

    @Override
    public void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        try {
            for (int i = 0; i < this._fields.length; ++i) {
                Field field = this._fields[i];
                this._fieldSerializers[i].serialize(out, obj, field);
            }
        }
        catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage() + "\n class: " + obj.getClass().getName() + " (object=" + obj + ")", e);
        }
        catch (IOException e) {
            throw new IOExceptionWrapper(e.getMessage() + "\n class: " + obj.getClass().getName() + " (object=" + obj + ")", e);
        }
    }

    private static FieldSerializer getFieldSerializer(Class<?> type) {
        if (Integer.TYPE.equals(type) || Byte.TYPE.equals(type) || Short.TYPE.equals(type) || Integer.TYPE.equals(type)) {
            return IntFieldSerializer.SER;
        }
        if (Long.TYPE.equals(type)) {
            return LongFieldSerializer.SER;
        }
        if (Double.TYPE.equals(type) || Float.TYPE.equals(type)) {
            return DoubleFieldSerializer.SER;
        }
        if (Boolean.TYPE.equals(type)) {
            return BooleanFieldSerializer.SER;
        }
        if (String.class.equals(type)) {
            return StringFieldSerializer.SER;
        }
        if (java.util.Date.class.equals(type) || Date.class.equals(type) || Timestamp.class.equals(type) || Time.class.equals(type)) {
            return DateFieldSerializer.SER;
        }
        return FieldSerializer.SER;
    }

    static class DateFieldSerializer
    extends FieldSerializer {
        static final FieldSerializer SER = new DateFieldSerializer();

        DateFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            java.util.Date value = null;
            try {
                value = (java.util.Date)field.get(obj);
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

    static class StringFieldSerializer
    extends FieldSerializer {
        static final FieldSerializer SER = new StringFieldSerializer();

        StringFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            String value = null;
            try {
                value = (String)field.get(obj);
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeString(value);
        }
    }

    static class DoubleFieldSerializer
    extends FieldSerializer {
        static final FieldSerializer SER = new DoubleFieldSerializer();

        DoubleFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            double value = 0.0;
            try {
                value = field.getDouble(obj);
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeDouble(value);
        }
    }

    static class LongFieldSerializer
    extends FieldSerializer {
        static final FieldSerializer SER = new LongFieldSerializer();

        LongFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            long value = 0L;
            try {
                value = field.getLong(obj);
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeLong(value);
        }
    }

    static class IntFieldSerializer
    extends FieldSerializer {
        static final FieldSerializer SER = new IntFieldSerializer();

        IntFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            int value = 0;
            try {
                value = field.getInt(obj);
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeInt(value);
        }
    }

    static class BooleanFieldSerializer
    extends FieldSerializer {
        static final FieldSerializer SER = new BooleanFieldSerializer();

        BooleanFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            boolean value = false;
            try {
                value = field.getBoolean(obj);
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            out.writeBoolean(value);
        }
    }

    static class FieldSerializer {
        static final FieldSerializer SER = new FieldSerializer();

        FieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            Object value = null;
            try {
                value = field.get(obj);
            }
            catch (IllegalAccessException e) {
                log.log(Level.FINE, e.toString(), e);
            }
            try {
                out.writeObject(value);
            }
            catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage() + "\n field: " + field.getDeclaringClass().getName() + '.' + field.getName(), e);
            }
            catch (IOException e) {
                throw new IOExceptionWrapper(e.getMessage() + "\n field: " + field.getDeclaringClass().getName() + '.' + field.getName(), e);
            }
        }
    }
}

