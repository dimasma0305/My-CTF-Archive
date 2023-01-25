/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.HessianUnshared;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.IOExceptionWrapper;
import com.caucho.hessian.io.UnsafeUnsharedSerializer;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnsafeSerializer
extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(UnsafeSerializer.class.getName());
    private static boolean _isEnabled;
    private static final Unsafe _unsafe;
    private static final WeakHashMap<Class<?>, SoftReference<UnsafeSerializer>> _serializerMap;
    private Field[] _fields;
    private FieldSerializer[] _fieldSerializers;

    public static boolean isEnabled() {
        return _isEnabled;
    }

    public UnsafeSerializer(Class<?> cl) {
        this.introspect(cl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static UnsafeSerializer create(Class<?> cl) {
        WeakHashMap<Class<?>, SoftReference<UnsafeSerializer>> weakHashMap = _serializerMap;
        synchronized (weakHashMap) {
            UnsafeSerializer base;
            SoftReference<UnsafeSerializer> baseRef = _serializerMap.get(cl);
            UnsafeSerializer unsafeSerializer = base = baseRef != null ? baseRef.get() : null;
            if (base == null) {
                base = cl.isAnnotationPresent(HessianUnshared.class) ? new UnsafeUnsharedSerializer(cl) : new UnsafeSerializer(cl);
                baseRef = new SoftReference<UnsafeSerializer>(base);
                _serializerMap.put(cl, baseRef);
            }
            return base;
        }
    }

    protected void introspect(Class<?> cl) {
        int i;
        Object fields;
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
            this._fieldSerializers[i] = UnsafeSerializer.getFieldSerializer(this._fields[i]);
        }
    }

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Class<?> cl = obj.getClass();
        int ref = out.writeObjectBegin(cl.getName());
        if (ref >= 0) {
            this.writeInstance(obj, out);
        } else if (ref == -1) {
            this.writeDefinition20(out);
            out.writeObjectBegin(cl.getName());
            this.writeInstance(obj, out);
        } else {
            this.writeObject10(obj, out);
        }
    }

    @Override
    protected void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        for (int i = 0; i < this._fields.length; ++i) {
            Field field = this._fields[i];
            out.writeString(field.getName());
            this._fieldSerializers[i].serialize(out, obj);
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
    public final void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        try {
            FieldSerializer[] fieldSerializers = this._fieldSerializers;
            int length = fieldSerializers.length;
            for (int i = 0; i < length; ++i) {
                fieldSerializers[i].serialize(out, obj);
            }
        }
        catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage() + "\n class: " + obj.getClass().getName() + " (object=" + obj + ")", e);
        }
        catch (IOException e) {
            throw new IOExceptionWrapper(e.getMessage() + "\n class: " + obj.getClass().getName() + " (object=" + obj + ")", e);
        }
    }

    private static FieldSerializer getFieldSerializer(Field field) {
        Class<?> type = field.getType();
        if (Boolean.TYPE.equals(type)) {
            return new BooleanFieldSerializer(field);
        }
        if (Byte.TYPE.equals(type)) {
            return new ByteFieldSerializer(field);
        }
        if (Character.TYPE.equals(type)) {
            return new CharFieldSerializer(field);
        }
        if (Short.TYPE.equals(type)) {
            return new ShortFieldSerializer(field);
        }
        if (Integer.TYPE.equals(type)) {
            return new IntFieldSerializer(field);
        }
        if (Long.TYPE.equals(type)) {
            return new LongFieldSerializer(field);
        }
        if (Double.TYPE.equals(type)) {
            return new DoubleFieldSerializer(field);
        }
        if (Float.TYPE.equals(type)) {
            return new FloatFieldSerializer(field);
        }
        if (String.class.equals(type)) {
            return new StringFieldSerializer(field);
        }
        if (java.util.Date.class.equals(type) || Date.class.equals(type) || Timestamp.class.equals(type) || Time.class.equals(type)) {
            return new DateFieldSerializer(field);
        }
        return new ObjectFieldSerializer(field);
    }

    static {
        _serializerMap = new WeakHashMap();
        boolean isEnabled = false;
        Unsafe unsafe = null;
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = null;
            for (Field field : unsafeClass.getDeclaredFields()) {
                if (!field.getName().equals("theUnsafe")) continue;
                theUnsafe = field;
            }
            if (theUnsafe != null) {
                theUnsafe.setAccessible(true);
                unsafe = (Unsafe)theUnsafe.get(null);
            }
            isEnabled = unsafe != null;
            String unsafeProp = System.getProperty("com.caucho.hessian.unsafe");
            if ("false".equals(unsafeProp)) {
                isEnabled = false;
            }
        }
        catch (Throwable e) {
            log.log(Level.ALL, e.toString(), e);
        }
        _unsafe = unsafe;
        _isEnabled = isEnabled;
    }

    static final class DateFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        DateFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            java.util.Date value = (java.util.Date)_unsafe.getObject(obj, this._offset);
            if (value == null) {
                out.writeNull();
            } else {
                out.writeUTCDate(value.getTime());
            }
        }
    }

    static final class StringFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        StringFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            String value = (String)_unsafe.getObject(obj, this._offset);
            out.writeString(value);
        }
    }

    static final class DoubleFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        DoubleFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            double value = _unsafe.getDouble(obj, this._offset);
            out.writeDouble(value);
        }
    }

    static final class FloatFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        FloatFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            double value = _unsafe.getFloat(obj, this._offset);
            out.writeDouble(value);
        }
    }

    static final class LongFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        LongFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            long value = _unsafe.getLong(obj, this._offset);
            out.writeLong(value);
        }
    }

    static final class IntFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        IntFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            int value = _unsafe.getInt(obj, this._offset);
            out.writeInt(value);
        }
    }

    static final class ShortFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        ShortFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            short value = _unsafe.getShort(obj, this._offset);
            out.writeInt(value);
        }
    }

    static final class CharFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        CharFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            char value = _unsafe.getChar(obj, this._offset);
            out.writeString(String.valueOf(value));
        }
    }

    static final class ByteFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        ByteFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            byte value = _unsafe.getByte(obj, this._offset);
            out.writeInt(value);
        }
    }

    static final class BooleanFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        BooleanFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            boolean value = _unsafe.getBoolean(obj, this._offset);
            out.writeBoolean(value);
        }
    }

    static final class ObjectFieldSerializer
    extends FieldSerializer {
        private final Field _field;
        private final long _offset;

        ObjectFieldSerializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(field);
            if (this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            try {
                Object value = _unsafe.getObject(obj, this._offset);
                out.writeObject(value);
            }
            catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage() + "\n field: " + this._field.getDeclaringClass().getName() + '.' + this._field.getName(), e);
            }
            catch (IOException e) {
                throw new IOExceptionWrapper(e.getMessage() + "\n field: " + this._field.getDeclaringClass().getName() + '.' + this._field.getName(), e);
            }
        }
    }

    static abstract class FieldSerializer {
        FieldSerializer() {
        }

        abstract void serialize(AbstractHessianOutput var1, Object var2) throws IOException;
    }
}

