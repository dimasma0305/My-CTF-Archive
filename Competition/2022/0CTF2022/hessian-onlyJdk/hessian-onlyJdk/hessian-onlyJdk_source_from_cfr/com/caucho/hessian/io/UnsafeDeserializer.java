/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractMapDeserializer;
import com.caucho.hessian.io.HessianFieldException;
import com.caucho.hessian.io.IOExceptionWrapper;
import com.caucho.hessian.io.JavaDeserializer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnsafeDeserializer
extends AbstractMapDeserializer {
    private static final Logger log = Logger.getLogger(JavaDeserializer.class.getName());
    private static boolean _isEnabled;
    private static Unsafe _unsafe;
    private Class<?> _type;
    private HashMap<String, FieldDeserializer> _fieldMap;
    private Method _readResolve;

    public UnsafeDeserializer(Class<?> cl) {
        this._type = cl;
        this._fieldMap = this.getFieldMap(cl);
        this._readResolve = this.getReadResolve(cl);
        if (this._readResolve != null) {
            this._readResolve.setAccessible(true);
        }
    }

    public static boolean isEnabled() {
        return _isEnabled;
    }

    @Override
    public Class<?> getType() {
        return this._type;
    }

    @Override
    public boolean isReadResolve() {
        return this._readResolve != null;
    }

    @Override
    public Object readMap(AbstractHessianInput in) throws IOException {
        try {
            Object obj = this.instantiate();
            return this.readMap(in, obj);
        }
        catch (IOException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(this._type.getName() + ":" + e.getMessage(), e);
        }
    }

    @Override
    public Object[] createFields(int len) {
        return new FieldDeserializer[len];
    }

    @Override
    public Object createField(String name) {
        FieldDeserializer reader = this._fieldMap.get(name);
        if (reader == null) {
            reader = NullFieldDeserializer.DESER;
        }
        return reader;
    }

    @Override
    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        try {
            Object obj = this.instantiate();
            return this.readObject(in, obj, (FieldDeserializer[])fields);
        }
        catch (IOException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(this._type.getName() + ":" + e.getMessage(), e);
        }
    }

    @Override
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        try {
            Object obj = this.instantiate();
            return this.readObject(in, obj, fieldNames);
        }
        catch (IOException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(this._type.getName() + ":" + e.getMessage(), e);
        }
    }

    protected Method getReadResolve(Class<?> cl) {
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (int i = 0; i < methods.length; ++i) {
                Method method = methods[i];
                if (!method.getName().equals("readResolve") || method.getParameterTypes().length != 0) continue;
                return method;
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    public Object readMap(AbstractHessianInput in, Object obj) throws IOException {
        try {
            int ref = in.addRef(obj);
            while (!in.isEnd()) {
                Object key = in.readObject();
                FieldDeserializer deser = this._fieldMap.get(key);
                if (deser != null) {
                    deser.deserialize(in, obj);
                    continue;
                }
                in.readObject();
            }
            in.readMapEnd();
            Object resolve = this.resolve(in, obj);
            if (obj != resolve) {
                in.setRef(ref, resolve);
            }
            return resolve;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }

    public Object readObject(AbstractHessianInput in, Object obj, FieldDeserializer[] fields) throws IOException {
        try {
            int ref = in.addRef(obj);
            for (FieldDeserializer reader : fields) {
                reader.deserialize(in, obj);
            }
            Object resolve = this.resolve(in, obj);
            if (obj != resolve) {
                in.setRef(ref, resolve);
            }
            return resolve;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(obj.getClass().getName() + ":" + e, e);
        }
    }

    public Object readObject(AbstractHessianInput in, Object obj, String[] fieldNames) throws IOException {
        try {
            int ref = in.addRef(obj);
            for (String fieldName : fieldNames) {
                FieldDeserializer reader = this._fieldMap.get(fieldName);
                if (reader != null) {
                    reader.deserialize(in, obj);
                    continue;
                }
                in.readObject();
            }
            Object resolve = this.resolve(in, obj);
            if (obj != resolve) {
                in.setRef(ref, resolve);
            }
            return resolve;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOExceptionWrapper(obj.getClass().getName() + ":" + e, e);
        }
    }

    protected Object resolve(AbstractHessianInput in, Object obj) throws Exception {
        try {
            if (this._readResolve != null) {
                return this._readResolve.invoke(obj, new Object[0]);
            }
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception)e.getCause();
            }
            throw e;
        }
        return obj;
    }

    protected Object instantiate() throws Exception {
        return _unsafe.allocateInstance(this._type);
    }

    protected HashMap<String, FieldDeserializer> getFieldMap(Class<?> cl) {
        HashMap<String, FieldDeserializer> fieldMap = new HashMap<String, FieldDeserializer>();
        while (cl != null) {
            Field[] fields = cl.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                Field field = fields[i];
                if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || fieldMap.get(field.getName()) != null) continue;
                try {
                    field.setAccessible(true);
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
                Class<?> type = field.getType();
                FieldDeserializer deser = String.class.equals(type) ? new StringFieldDeserializer(field) : (Byte.TYPE.equals(type) ? new ByteFieldDeserializer(field) : (Character.TYPE.equals(type) ? new CharFieldDeserializer(field) : (Short.TYPE.equals(type) ? new ShortFieldDeserializer(field) : (Integer.TYPE.equals(type) ? new IntFieldDeserializer(field) : (Long.TYPE.equals(type) ? new LongFieldDeserializer(field) : (Float.TYPE.equals(type) ? new FloatFieldDeserializer(field) : (Double.TYPE.equals(type) ? new DoubleFieldDeserializer(field) : (Boolean.TYPE.equals(type) ? new BooleanFieldDeserializer(field) : (Date.class.equals(type) ? new SqlDateFieldDeserializer(field) : (Timestamp.class.equals(type) ? new SqlTimestampFieldDeserializer(field) : (Time.class.equals(type) ? new SqlTimeFieldDeserializer(field) : new ObjectFieldDeserializer(field))))))))))));
                fieldMap.put(field.getName(), deser);
            }
            cl = cl.getSuperclass();
        }
        return fieldMap;
    }

    static void logDeserializeError(Field field, Object obj, Object value, Throwable e) throws IOException {
        String fieldName = field.getDeclaringClass().getName() + "." + field.getName();
        if (e instanceof HessianFieldException) {
            throw (HessianFieldException)e;
        }
        if (e instanceof IOException) {
            throw new HessianFieldException(fieldName + ": " + e.getMessage(), e);
        }
        if (value != null) {
            throw new HessianFieldException(fieldName + ": " + value.getClass().getName() + " (" + value + ")" + " cannot be assigned to '" + field.getType().getName() + "'", e);
        }
        throw new HessianFieldException(fieldName + ": " + field.getType().getName() + " cannot be assigned from null", e);
    }

    static {
        boolean isEnabled = false;
        try {
            Class<?> unsafe = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = null;
            for (Field field : unsafe.getDeclaredFields()) {
                if (!field.getName().equals("theUnsafe")) continue;
                theUnsafe = field;
            }
            if (theUnsafe != null) {
                theUnsafe.setAccessible(true);
                _unsafe = (Unsafe)theUnsafe.get(null);
            }
            isEnabled = _unsafe != null;
            String unsafeProp = System.getProperty("com.caucho.hessian.unsafe");
            if ("false".equals(unsafeProp)) {
                isEnabled = false;
            }
        }
        catch (Throwable e) {
            log.log(Level.FINER, e.toString(), e);
        }
        _isEnabled = isEnabled;
    }

    static class SqlTimeFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        SqlTimeFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Time value = null;
            try {
                java.util.Date date = (java.util.Date)in.readObject();
                if (date != null) {
                    value = new Time(date.getTime());
                    _unsafe.putObject(obj, this._offset, value);
                } else {
                    _unsafe.putObject(obj, this._offset, null);
                }
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class SqlTimestampFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        SqlTimestampFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Timestamp value = null;
            try {
                java.util.Date date = (java.util.Date)in.readObject();
                if (date != null) {
                    value = new Timestamp(date.getTime());
                    _unsafe.putObject(obj, this._offset, value);
                } else {
                    _unsafe.putObject(obj, this._offset, null);
                }
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class SqlDateFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        SqlDateFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Date value = null;
            try {
                java.util.Date date = (java.util.Date)in.readObject();
                if (date != null) {
                    value = new Date(date.getTime());
                    _unsafe.putObject(obj, this._offset, value);
                } else {
                    _unsafe.putObject(obj, this._offset, null);
                }
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class StringFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        StringFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            String value = null;
            try {
                value = in.readString();
                _unsafe.putObject(obj, this._offset, value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class DoubleFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        DoubleFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            double value = 0.0;
            try {
                value = in.readDouble();
                _unsafe.putDouble(obj, this._offset, value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class FloatFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        FloatFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            double value = 0.0;
            try {
                value = in.readDouble();
                _unsafe.putFloat(obj, this._offset, (float)value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class LongFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        LongFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            long value = 0L;
            try {
                value = in.readLong();
                _unsafe.putLong(obj, this._offset, value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class IntFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        IntFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            int value = 0;
            try {
                value = in.readInt();
                _unsafe.putInt(obj, this._offset, value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class ShortFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        ShortFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            int value = 0;
            try {
                value = in.readInt();
                _unsafe.putShort(obj, this._offset, (short)value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class CharFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        CharFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            String value = null;
            try {
                value = in.readString();
                char ch = value != null && value.length() > 0 ? value.charAt(0) : (char)'\u0000';
                _unsafe.putChar(obj, this._offset, ch);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class ByteFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        ByteFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            int value = 0;
            try {
                value = in.readInt();
                _unsafe.putByte(obj, this._offset, (byte)value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class BooleanFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        BooleanFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            boolean value = false;
            try {
                value = in.readBoolean();
                _unsafe.putBoolean(obj, this._offset, value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class ObjectFieldDeserializer
    extends FieldDeserializer {
        private final Field _field;
        private final long _offset;

        ObjectFieldDeserializer(Field field) {
            this._field = field;
            this._offset = _unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Object value = null;
            try {
                value = in.readObject(this._field.getType());
                _unsafe.putObject(obj, this._offset, value);
            }
            catch (Exception e) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    static class NullFieldDeserializer
    extends FieldDeserializer {
        static NullFieldDeserializer DESER = new NullFieldDeserializer();

        NullFieldDeserializer() {
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            in.readObject();
        }
    }

    static abstract class FieldDeserializer {
        FieldDeserializer() {
        }

        abstract void deserialize(AbstractHessianInput var1, Object var2) throws IOException;
    }
}

