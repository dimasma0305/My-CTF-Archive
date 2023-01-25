/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.burlap.io.BurlapRemoteObject;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.AnnotationDeserializer;
import com.caucho.hessian.io.AnnotationSerializer;
import com.caucho.hessian.io.ArrayDeserializer;
import com.caucho.hessian.io.ArraySerializer;
import com.caucho.hessian.io.BasicDeserializer;
import com.caucho.hessian.io.CalendarSerializer;
import com.caucho.hessian.io.ClassDeserializer;
import com.caucho.hessian.io.CollectionDeserializer;
import com.caucho.hessian.io.CollectionSerializer;
import com.caucho.hessian.io.ContextSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.EnumDeserializer;
import com.caucho.hessian.io.EnumSerializer;
import com.caucho.hessian.io.EnumerationDeserializer;
import com.caucho.hessian.io.EnumerationSerializer;
import com.caucho.hessian.io.HessianHandle;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.HessianRemote;
import com.caucho.hessian.io.HessianRemoteObject;
import com.caucho.hessian.io.InetAddressSerializer;
import com.caucho.hessian.io.InputStreamDeserializer;
import com.caucho.hessian.io.InputStreamSerializer;
import com.caucho.hessian.io.IteratorDeserializer;
import com.caucho.hessian.io.IteratorSerializer;
import com.caucho.hessian.io.JavaDeserializer;
import com.caucho.hessian.io.JavaSerializer;
import com.caucho.hessian.io.MapDeserializer;
import com.caucho.hessian.io.MapSerializer;
import com.caucho.hessian.io.ObjectDeserializer;
import com.caucho.hessian.io.ObjectSerializer;
import com.caucho.hessian.io.RemoteDeserializer;
import com.caucho.hessian.io.RemoteSerializer;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.ThrowableSerializer;
import com.caucho.hessian.io.UnsafeDeserializer;
import com.caucho.hessian.io.UnsafeSerializer;
import com.caucho.hessian.io.WriteReplaceSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SerializerFactory
extends AbstractSerializerFactory {
    private static final Logger log = Logger.getLogger(SerializerFactory.class.getName());
    private static final Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(14);
    private static final ClassLoader _systemClassLoader;
    private static final HashMap _staticTypeMap;
    private static final WeakHashMap<ClassLoader, SoftReference<SerializerFactory>> _defaultFactoryRefMap;
    private ContextSerializerFactory _contextFactory;
    private WeakReference<ClassLoader> _loaderRef;
    protected Serializer _defaultSerializer;
    protected ArrayList _factories = new ArrayList();
    protected CollectionSerializer _collectionSerializer;
    protected MapSerializer _mapSerializer;
    private Deserializer _hashMapDeserializer;
    private Deserializer _arrayListDeserializer;
    private ConcurrentHashMap _cachedSerializerMap;
    private ConcurrentHashMap _cachedDeserializerMap;
    private HashMap _cachedTypeDeserializerMap;
    private boolean _isAllowNonSerializable;
    private boolean _isEnableUnsafeSerializer = UnsafeSerializer.isEnabled() && UnsafeDeserializer.isEnabled();

    public SerializerFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public SerializerFactory(ClassLoader loader) {
        this._loaderRef = new WeakReference<ClassLoader>(loader);
        this._contextFactory = ContextSerializerFactory.create(loader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static SerializerFactory createDefault() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        WeakHashMap<ClassLoader, SoftReference<SerializerFactory>> weakHashMap = _defaultFactoryRefMap;
        synchronized (weakHashMap) {
            SoftReference<SerializerFactory> factoryRef = _defaultFactoryRefMap.get(loader);
            SerializerFactory factory = null;
            if (factoryRef != null) {
                factory = factoryRef.get();
            }
            if (factory == null) {
                factory = new SerializerFactory();
                factoryRef = new SoftReference<SerializerFactory>(factory);
                _defaultFactoryRefMap.put(loader, factoryRef);
            }
            return factory;
        }
    }

    public ClassLoader getClassLoader() {
        return (ClassLoader)this._loaderRef.get();
    }

    public void setSendCollectionType(boolean isSendType) {
        if (this._collectionSerializer == null) {
            this._collectionSerializer = new CollectionSerializer();
        }
        this._collectionSerializer.setSendJavaType(isSendType);
        if (this._mapSerializer == null) {
            this._mapSerializer = new MapSerializer();
        }
        this._mapSerializer.setSendJavaType(isSendType);
    }

    public void addFactory(AbstractSerializerFactory factory) {
        this._factories.add(factory);
    }

    public void setAllowNonSerializable(boolean allow) {
        this._isAllowNonSerializable = allow;
    }

    public boolean isAllowNonSerializable() {
        return this._isAllowNonSerializable;
    }

    public Serializer getObjectSerializer(Class<?> cl) throws HessianProtocolException {
        Serializer serializer = this.getSerializer(cl);
        if (serializer instanceof ObjectSerializer) {
            return ((ObjectSerializer)((Object)serializer)).getObjectSerializer();
        }
        return serializer;
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        Serializer serializer;
        if (this._cachedSerializerMap != null && (serializer = (Serializer)this._cachedSerializerMap.get(cl)) != null) {
            return serializer;
        }
        serializer = this.loadSerializer(cl);
        if (this._cachedSerializerMap == null) {
            this._cachedSerializerMap = new ConcurrentHashMap(8);
        }
        this._cachedSerializerMap.put(cl, serializer);
        return serializer;
    }

    protected Serializer loadSerializer(Class<?> cl) throws HessianProtocolException {
        Object factory;
        Serializer serializer = null;
        for (int i = 0; this._factories != null && i < this._factories.size(); ++i) {
            factory = (AbstractSerializerFactory)this._factories.get(i);
            serializer = ((AbstractSerializerFactory)factory).getSerializer(cl);
            if (serializer == null) continue;
            return serializer;
        }
        serializer = this._contextFactory.getSerializer(cl.getName());
        if (serializer != null) {
            return serializer;
        }
        ClassLoader loader = cl.getClassLoader();
        if (loader == null) {
            loader = _systemClassLoader;
        }
        factory = null;
        factory = ContextSerializerFactory.create(loader);
        serializer = ((ContextSerializerFactory)factory).getCustomSerializer(cl);
        if (serializer != null) {
            return serializer;
        }
        if (HessianRemoteObject.class.isAssignableFrom(cl)) {
            return new RemoteSerializer();
        }
        if (BurlapRemoteObject.class.isAssignableFrom(cl)) {
            return new RemoteSerializer();
        }
        if (InetAddress.class.isAssignableFrom(cl)) {
            return InetAddressSerializer.create();
        }
        if (JavaSerializer.getWriteReplace(cl) != null) {
            Serializer baseSerializer = this.getDefaultSerializer(cl);
            return new WriteReplaceSerializer(cl, this.getClassLoader(), baseSerializer);
        }
        if (Map.class.isAssignableFrom(cl)) {
            if (this._mapSerializer == null) {
                this._mapSerializer = new MapSerializer();
            }
            return this._mapSerializer;
        }
        if (Collection.class.isAssignableFrom(cl)) {
            if (this._collectionSerializer == null) {
                this._collectionSerializer = new CollectionSerializer();
            }
            return this._collectionSerializer;
        }
        if (cl.isArray()) {
            return new ArraySerializer();
        }
        if (Throwable.class.isAssignableFrom(cl)) {
            return new ThrowableSerializer(cl, this.getClassLoader());
        }
        if (InputStream.class.isAssignableFrom(cl)) {
            return new InputStreamSerializer();
        }
        if (Iterator.class.isAssignableFrom(cl)) {
            return IteratorSerializer.create();
        }
        if (Calendar.class.isAssignableFrom(cl)) {
            return CalendarSerializer.SER;
        }
        if (Enumeration.class.isAssignableFrom(cl)) {
            return EnumerationSerializer.create();
        }
        if (Enum.class.isAssignableFrom(cl)) {
            return new EnumSerializer(cl);
        }
        if (Annotation.class.isAssignableFrom(cl)) {
            return new AnnotationSerializer(cl);
        }
        return this.getDefaultSerializer(cl);
    }

    protected Serializer getDefaultSerializer(Class cl) {
        if (this._defaultSerializer != null) {
            return this._defaultSerializer;
        }
        if (!Serializable.class.isAssignableFrom(cl) && !this._isAllowNonSerializable) {
            throw new IllegalStateException("Serialized class " + cl.getName() + " must implement java.io.Serializable");
        }
        if (this._isEnableUnsafeSerializer && JavaSerializer.getWriteReplace(cl) == null) {
            return UnsafeSerializer.create(cl);
        }
        return JavaSerializer.create(cl);
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        Deserializer deserializer;
        if (this._cachedDeserializerMap != null && (deserializer = (Deserializer)this._cachedDeserializerMap.get(cl)) != null) {
            return deserializer;
        }
        deserializer = this.loadDeserializer(cl);
        if (this._cachedDeserializerMap == null) {
            this._cachedDeserializerMap = new ConcurrentHashMap(8);
        }
        this._cachedDeserializerMap.put(cl, deserializer);
        return deserializer;
    }

    protected Deserializer loadDeserializer(Class cl) throws HessianProtocolException {
        Deserializer deserializer = null;
        for (int i = 0; deserializer == null && this._factories != null && i < this._factories.size(); ++i) {
            AbstractSerializerFactory factory = (AbstractSerializerFactory)this._factories.get(i);
            deserializer = factory.getDeserializer(cl);
        }
        if (deserializer != null) {
            return deserializer;
        }
        deserializer = this._contextFactory.getDeserializer(cl.getName());
        if (deserializer != null) {
            return deserializer;
        }
        ContextSerializerFactory factory = null;
        factory = cl.getClassLoader() != null ? ContextSerializerFactory.create(cl.getClassLoader()) : ContextSerializerFactory.create(_systemClassLoader);
        deserializer = factory.getCustomDeserializer(cl);
        if (deserializer != null) {
            return deserializer;
        }
        deserializer = Collection.class.isAssignableFrom(cl) ? new CollectionDeserializer(cl) : (Map.class.isAssignableFrom(cl) ? new MapDeserializer(cl) : (Iterator.class.isAssignableFrom(cl) ? IteratorDeserializer.create() : (Annotation.class.isAssignableFrom(cl) ? new AnnotationDeserializer(cl) : (cl.isInterface() ? new ObjectDeserializer(cl) : (cl.isArray() ? new ArrayDeserializer(cl.getComponentType()) : (Enumeration.class.isAssignableFrom(cl) ? EnumerationDeserializer.create() : (Enum.class.isAssignableFrom(cl) ? new EnumDeserializer(cl) : (Class.class.equals((Object)cl) ? new ClassDeserializer(this.getClassLoader()) : this.getDefaultDeserializer(cl)))))))));
        return deserializer;
    }

    protected Deserializer getCustomDeserializer(Class cl) {
        try {
            Class<?> serClass = Class.forName(cl.getName() + "HessianDeserializer", false, cl.getClassLoader());
            Deserializer ser = (Deserializer)serClass.newInstance();
            return ser;
        }
        catch (ClassNotFoundException e) {
            log.log(Level.FINEST, e.toString(), e);
            return null;
        }
        catch (Exception e) {
            log.log(Level.FINE, e.toString(), e);
            return null;
        }
    }

    protected Deserializer getDefaultDeserializer(Class cl) {
        if (InputStream.class.equals((Object)cl)) {
            return InputStreamDeserializer.DESER;
        }
        if (this._isEnableUnsafeSerializer) {
            return new UnsafeDeserializer(cl);
        }
        return new JavaDeserializer(cl);
    }

    public Object readList(AbstractHessianInput in, int length, String type) throws HessianProtocolException, IOException {
        Deserializer deserializer = this.getDeserializer(type);
        if (deserializer != null) {
            return deserializer.readList(in, length);
        }
        return new CollectionDeserializer(ArrayList.class).readList(in, length);
    }

    public Object readMap(AbstractHessianInput in, String type) throws HessianProtocolException, IOException {
        Deserializer deserializer = this.getDeserializer(type);
        if (deserializer != null) {
            return deserializer.readMap(in);
        }
        if (this._hashMapDeserializer != null) {
            return this._hashMapDeserializer.readMap(in);
        }
        this._hashMapDeserializer = new MapDeserializer(HashMap.class);
        return this._hashMapDeserializer.readMap(in);
    }

    public Object readObject(AbstractHessianInput in, String type, String[] fieldNames) throws HessianProtocolException, IOException {
        Deserializer deserializer = this.getDeserializer(type);
        if (deserializer != null) {
            return deserializer.readObject(in, fieldNames);
        }
        if (this._hashMapDeserializer != null) {
            return this._hashMapDeserializer.readObject(in, fieldNames);
        }
        this._hashMapDeserializer = new MapDeserializer(HashMap.class);
        return this._hashMapDeserializer.readObject(in, fieldNames);
    }

    public Deserializer getObjectDeserializer(String type, Class cl) throws HessianProtocolException {
        Deserializer reader = this.getObjectDeserializer(type);
        if (cl == null || cl.equals(reader.getType()) || cl.isAssignableFrom(reader.getType()) || reader.isReadResolve() || HessianHandle.class.isAssignableFrom(reader.getType())) {
            return reader;
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("hessian: expected deserializer '" + cl.getName() + "' at '" + type + "' (" + reader.getType().getName() + ")");
        }
        return this.getDeserializer(cl);
    }

    public Deserializer getObjectDeserializer(String type) throws HessianProtocolException {
        Deserializer deserializer = this.getDeserializer(type);
        if (deserializer != null) {
            return deserializer;
        }
        if (this._hashMapDeserializer != null) {
            return this._hashMapDeserializer;
        }
        this._hashMapDeserializer = new MapDeserializer(HashMap.class);
        return this._hashMapDeserializer;
    }

    public Deserializer getListDeserializer(String type, Class cl) throws HessianProtocolException {
        Deserializer reader = this.getListDeserializer(type);
        if (cl == null || cl.equals(reader.getType()) || cl.isAssignableFrom(reader.getType())) {
            return reader;
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("hessian: expected '" + cl.getName() + "' at '" + type + "' (" + reader.getType().getName() + ")");
        }
        return this.getDeserializer(cl);
    }

    public Deserializer getListDeserializer(String type) throws HessianProtocolException {
        Deserializer deserializer = this.getDeserializer(type);
        if (deserializer != null) {
            return deserializer;
        }
        if (this._arrayListDeserializer != null) {
            return this._arrayListDeserializer;
        }
        this._arrayListDeserializer = new CollectionDeserializer(ArrayList.class);
        return this._arrayListDeserializer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Deserializer getDeserializer(String type) throws HessianProtocolException {
        Deserializer deserializer;
        HashMap hashMap;
        if (type == null || type.equals("")) {
            return null;
        }
        if (this._cachedTypeDeserializerMap != null) {
            hashMap = this._cachedTypeDeserializerMap;
            synchronized (hashMap) {
                deserializer = (Deserializer)this._cachedTypeDeserializerMap.get(type);
            }
            if (deserializer != null) {
                return deserializer;
            }
        }
        if ((deserializer = (Deserializer)_staticTypeMap.get(type)) != null) {
            return deserializer;
        }
        if (type.startsWith("[")) {
            Deserializer subDeserializer = this.getDeserializer(type.substring(1));
            deserializer = subDeserializer != null ? new ArrayDeserializer(subDeserializer.getType()) : new ArrayDeserializer(Object.class);
        } else {
            try {
                Class<?> cl = Class.forName(type, false, this.getClassLoader());
                deserializer = this.getDeserializer(cl);
            }
            catch (Exception e) {
                log.warning("Hessian/Burlap: '" + type + "' is an unknown class in " + this.getClassLoader() + ":\n" + e);
                log.log(Level.FINER, e.toString(), e);
            }
        }
        if (deserializer != null) {
            if (this._cachedTypeDeserializerMap == null) {
                this._cachedTypeDeserializerMap = new HashMap(8);
            }
            hashMap = this._cachedTypeDeserializerMap;
            synchronized (hashMap) {
                this._cachedTypeDeserializerMap.put(type, deserializer);
            }
        }
        return deserializer;
    }

    private static void addBasic(Class<?> cl, String typeName, int type) {
        BasicDeserializer deserializer = new BasicDeserializer(type);
        _staticTypeMap.put(typeName, deserializer);
    }

    static {
        _defaultFactoryRefMap = new WeakHashMap();
        _staticTypeMap = new HashMap();
        SerializerFactory.addBasic(Void.TYPE, "void", 0);
        SerializerFactory.addBasic(Boolean.class, "boolean", 1);
        SerializerFactory.addBasic(Byte.class, "byte", 2);
        SerializerFactory.addBasic(Short.class, "short", 3);
        SerializerFactory.addBasic(Integer.class, "int", 4);
        SerializerFactory.addBasic(Long.class, "long", 5);
        SerializerFactory.addBasic(Float.class, "float", 6);
        SerializerFactory.addBasic(Double.class, "double", 7);
        SerializerFactory.addBasic(Character.class, "char", 9);
        SerializerFactory.addBasic(String.class, "string", 10);
        SerializerFactory.addBasic(StringBuilder.class, "string", 11);
        SerializerFactory.addBasic(Object.class, "object", 14);
        SerializerFactory.addBasic(Date.class, "date", 12);
        SerializerFactory.addBasic(Boolean.TYPE, "boolean", 1);
        SerializerFactory.addBasic(Byte.TYPE, "byte", 2);
        SerializerFactory.addBasic(Short.TYPE, "short", 3);
        SerializerFactory.addBasic(Integer.TYPE, "int", 4);
        SerializerFactory.addBasic(Long.TYPE, "long", 5);
        SerializerFactory.addBasic(Float.TYPE, "float", 6);
        SerializerFactory.addBasic(Double.TYPE, "double", 7);
        SerializerFactory.addBasic(Character.TYPE, "char", 8);
        SerializerFactory.addBasic(boolean[].class, "[boolean", 15);
        SerializerFactory.addBasic(byte[].class, "[byte", 16);
        SerializerFactory.addBasic(short[].class, "[short", 17);
        SerializerFactory.addBasic(int[].class, "[int", 18);
        SerializerFactory.addBasic(long[].class, "[long", 19);
        SerializerFactory.addBasic(float[].class, "[float", 20);
        SerializerFactory.addBasic(double[].class, "[double", 21);
        SerializerFactory.addBasic(char[].class, "[char", 22);
        SerializerFactory.addBasic(String[].class, "[string", 23);
        SerializerFactory.addBasic(Object[].class, "[object", 24);
        JavaDeserializer objectDeserializer = new JavaDeserializer(Object.class);
        _staticTypeMap.put("object", objectDeserializer);
        _staticTypeMap.put(HessianRemote.class.getName(), RemoteDeserializer.DESER);
        ClassLoader systemClassLoader = null;
        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        }
        catch (Exception exception) {
            // empty catch block
        }
        _systemClassLoader = systemClassLoader;
    }
}

