/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.HessianException;
import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.BasicDeserializer;
import com.caucho.hessian.io.BasicSerializer;
import com.caucho.hessian.io.ByteArraySerializer;
import com.caucho.hessian.io.ClassSerializer;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.InetAddressSerializer;
import com.caucho.hessian.io.JavaDeserializer;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SqlDateDeserializer;
import com.caucho.hessian.io.SqlDateSerializer;
import com.caucho.hessian.io.StackTraceElementDeserializer;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ContextSerializerFactory {
    private static final Logger log = Logger.getLogger(ContextSerializerFactory.class.getName());
    private static Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(14);
    private static final WeakHashMap<ClassLoader, SoftReference<ContextSerializerFactory>> _contextRefMap = new WeakHashMap();
    private static final ClassLoader _systemClassLoader;
    private static HashMap<String, Serializer> _staticSerializerMap;
    private static HashMap<String, Deserializer> _staticDeserializerMap;
    private static HashMap _staticClassNameMap;
    private ContextSerializerFactory _parent;
    private WeakReference<ClassLoader> _loaderRef;
    private final HashSet<String> _serializerFiles = new HashSet();
    private final HashSet<String> _deserializerFiles = new HashSet();
    private final HashMap<String, Serializer> _serializerClassMap = new HashMap();
    private final ConcurrentHashMap<String, Serializer> _customSerializerMap = new ConcurrentHashMap();
    private final HashMap<Class<?>, Serializer> _serializerInterfaceMap = new HashMap();
    private final HashMap<String, Deserializer> _deserializerClassMap = new HashMap();
    private final HashMap<String, Deserializer> _deserializerClassNameMap = new HashMap();
    private final ConcurrentHashMap<String, Deserializer> _customDeserializerMap = new ConcurrentHashMap();
    private final HashMap<Class<?>, Deserializer> _deserializerInterfaceMap = new HashMap();

    public ContextSerializerFactory(ContextSerializerFactory parent, ClassLoader loader) {
        if (loader == null) {
            loader = _systemClassLoader;
        }
        this._loaderRef = new WeakReference<ClassLoader>(loader);
        this.init();
    }

    public static ContextSerializerFactory create() {
        return ContextSerializerFactory.create(Thread.currentThread().getContextClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ContextSerializerFactory create(ClassLoader loader) {
        WeakHashMap<ClassLoader, SoftReference<ContextSerializerFactory>> weakHashMap = _contextRefMap;
        synchronized (weakHashMap) {
            SoftReference<ContextSerializerFactory> factoryRef = _contextRefMap.get(loader);
            ContextSerializerFactory factory = null;
            if (factoryRef != null) {
                factory = factoryRef.get();
            }
            if (factory == null) {
                ContextSerializerFactory parent = null;
                if (loader != null) {
                    parent = ContextSerializerFactory.create(loader.getParent());
                }
                factory = new ContextSerializerFactory(parent, loader);
                factoryRef = new SoftReference<ContextSerializerFactory>(factory);
                _contextRefMap.put(loader, factoryRef);
            }
            return factory;
        }
    }

    public ClassLoader getClassLoader() {
        WeakReference<ClassLoader> loaderRef = this._loaderRef;
        if (loaderRef != null) {
            return (ClassLoader)loaderRef.get();
        }
        return null;
    }

    public Serializer getSerializer(String className) {
        Serializer serializer = this._serializerClassMap.get(className);
        if (serializer == AbstractSerializer.NULL) {
            return null;
        }
        return serializer;
    }

    public Serializer getCustomSerializer(Class cl) {
        Serializer serializer = this._customSerializerMap.get(cl.getName());
        if (serializer == AbstractSerializer.NULL) {
            return null;
        }
        if (serializer != null) {
            return serializer;
        }
        try {
            Class<?> serClass = Class.forName(cl.getName() + "HessianSerializer", false, cl.getClassLoader());
            Serializer ser = (Serializer)serClass.newInstance();
            this._customSerializerMap.put(cl.getName(), ser);
            return ser;
        }
        catch (ClassNotFoundException e) {
            log.log(Level.ALL, e.toString(), e);
        }
        catch (Exception e) {
            throw new HessianException(e);
        }
        this._customSerializerMap.put(cl.getName(), AbstractSerializer.NULL);
        return null;
    }

    public Deserializer getDeserializer(String className) {
        Deserializer deserializer = this._deserializerClassMap.get(className);
        if (deserializer == AbstractDeserializer.NULL) {
            return null;
        }
        return deserializer;
    }

    public Deserializer getCustomDeserializer(Class cl) {
        Deserializer deserializer = this._customDeserializerMap.get(cl.getName());
        if (deserializer == AbstractDeserializer.NULL) {
            return null;
        }
        if (deserializer != null) {
            return deserializer;
        }
        try {
            Class<?> serClass = Class.forName(cl.getName() + "HessianDeserializer", false, cl.getClassLoader());
            Deserializer ser = (Deserializer)serClass.newInstance();
            this._customDeserializerMap.put(cl.getName(), ser);
            return ser;
        }
        catch (ClassNotFoundException e) {
            log.log(Level.ALL, e.toString(), e);
        }
        catch (Exception e) {
            throw new HessianException(e);
        }
        this._customDeserializerMap.put(cl.getName(), AbstractDeserializer.NULL);
        return null;
    }

    private void init() {
        Object ser;
        if (this._parent != null) {
            this._serializerFiles.addAll(this._parent._serializerFiles);
            this._deserializerFiles.addAll(this._parent._deserializerFiles);
            this._serializerClassMap.putAll(this._parent._serializerClassMap);
            this._deserializerClassMap.putAll(this._parent._deserializerClassMap);
        }
        if (this._parent == null) {
            this._serializerClassMap.putAll(_staticSerializerMap);
            this._deserializerClassMap.putAll(_staticDeserializerMap);
            this._deserializerClassNameMap.putAll(_staticClassNameMap);
        }
        HashMap<Class<Object>, Class<Object>> classMap = new HashMap<Class, Class>();
        this.initSerializerFiles("META-INF/hessian/serializers", this._serializerFiles, classMap, Serializer.class);
        for (Map.Entry<Class, Class> entry : classMap.entrySet()) {
            try {
                ser = (Serializer)entry.getValue().newInstance();
                if (entry.getKey().isInterface()) {
                    this._serializerInterfaceMap.put(entry.getKey(), (Serializer)ser);
                    continue;
                }
                this._serializerClassMap.put(entry.getKey().getName(), (Serializer)ser);
            }
            catch (Exception e) {
                throw new HessianException(e);
            }
        }
        classMap = new HashMap();
        this.initSerializerFiles("META-INF/hessian/deserializers", this._deserializerFiles, classMap, Deserializer.class);
        for (Map.Entry<Class, Class> entry : classMap.entrySet()) {
            try {
                ser = (Deserializer)entry.getValue().newInstance();
                if (entry.getKey().isInterface()) {
                    this._deserializerInterfaceMap.put(entry.getKey(), (Deserializer)ser);
                    continue;
                }
                this._deserializerClassMap.put(entry.getKey().getName(), (Deserializer)ser);
            }
            catch (Exception e) {
                throw new HessianException(e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initSerializerFiles(String fileName, HashSet<String> fileList, HashMap<Class, Class> classMap, Class type) {
        try {
            ClassLoader classLoader = this.getClassLoader();
            if (classLoader == null) {
                return;
            }
            Enumeration<URL> iter = classLoader.getResources(fileName);
            while (iter.hasMoreElements()) {
                URL url = iter.nextElement();
                if (fileList.contains(url.toString())) continue;
                fileList.add(url.toString());
                InputStream is = null;
                try {
                    is = url.openStream();
                    Properties props = new Properties();
                    props.load(is);
                    for (Map.Entry<Object, Object> entry : props.entrySet()) {
                        String apiName = (String)entry.getKey();
                        String serializerName = (String)entry.getValue();
                        Class<?> apiClass = null;
                        Class<?> serializerClass = null;
                        try {
                            apiClass = Class.forName(apiName, false, classLoader);
                        }
                        catch (ClassNotFoundException e) {
                            log.fine(url + ": " + apiName + " is not available in this context: " + this.getClassLoader());
                            continue;
                        }
                        try {
                            serializerClass = Class.forName(serializerName, false, classLoader);
                        }
                        catch (ClassNotFoundException e) {
                            log.fine(url + ": " + serializerName + " is not available in this context: " + this.getClassLoader());
                            continue;
                        }
                        if (!type.isAssignableFrom(serializerClass)) {
                            throw new HessianException(url + ": " + serializerClass.getName() + " is invalid because it does not implement " + type.getName());
                        }
                        classMap.put(apiClass, serializerClass);
                    }
                }
                finally {
                    if (is == null) continue;
                    is.close();
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new HessianException(e);
        }
    }

    private static void addBasic(Class cl, String typeName, int type) {
        _staticSerializerMap.put(cl.getName(), new BasicSerializer(type));
        BasicDeserializer deserializer = new BasicDeserializer(type);
        _staticDeserializerMap.put(cl.getName(), deserializer);
        _staticClassNameMap.put(typeName, deserializer);
    }

    static {
        _staticSerializerMap = new HashMap();
        _staticDeserializerMap = new HashMap();
        _staticClassNameMap = new HashMap();
        ContextSerializerFactory.addBasic(Void.TYPE, "void", 0);
        ContextSerializerFactory.addBasic(Boolean.class, "boolean", 1);
        ContextSerializerFactory.addBasic(Byte.class, "byte", 2);
        ContextSerializerFactory.addBasic(Short.class, "short", 3);
        ContextSerializerFactory.addBasic(Integer.class, "int", 4);
        ContextSerializerFactory.addBasic(Long.class, "long", 5);
        ContextSerializerFactory.addBasic(Float.class, "float", 6);
        ContextSerializerFactory.addBasic(Double.class, "double", 7);
        ContextSerializerFactory.addBasic(Character.class, "char", 9);
        ContextSerializerFactory.addBasic(String.class, "string", 10);
        ContextSerializerFactory.addBasic(Object.class, "object", 14);
        ContextSerializerFactory.addBasic(java.util.Date.class, "date", 12);
        ContextSerializerFactory.addBasic(Boolean.TYPE, "boolean", 1);
        ContextSerializerFactory.addBasic(Byte.TYPE, "byte", 2);
        ContextSerializerFactory.addBasic(Short.TYPE, "short", 3);
        ContextSerializerFactory.addBasic(Integer.TYPE, "int", 4);
        ContextSerializerFactory.addBasic(Long.TYPE, "long", 5);
        ContextSerializerFactory.addBasic(Float.TYPE, "float", 6);
        ContextSerializerFactory.addBasic(Double.TYPE, "double", 7);
        ContextSerializerFactory.addBasic(Character.TYPE, "char", 8);
        ContextSerializerFactory.addBasic(boolean[].class, "[boolean", 15);
        ContextSerializerFactory.addBasic(byte[].class, "[byte", 16);
        _staticSerializerMap.put(byte[].class.getName(), ByteArraySerializer.SER);
        ContextSerializerFactory.addBasic(short[].class, "[short", 17);
        ContextSerializerFactory.addBasic(int[].class, "[int", 18);
        ContextSerializerFactory.addBasic(long[].class, "[long", 19);
        ContextSerializerFactory.addBasic(float[].class, "[float", 20);
        ContextSerializerFactory.addBasic(double[].class, "[double", 21);
        ContextSerializerFactory.addBasic(char[].class, "[char", 22);
        ContextSerializerFactory.addBasic(String[].class, "[string", 23);
        ContextSerializerFactory.addBasic(Object[].class, "[object", 24);
        JavaDeserializer objectDeserializer = new JavaDeserializer(Object.class);
        _staticDeserializerMap.put("object", objectDeserializer);
        _staticClassNameMap.put("object", objectDeserializer);
        _staticSerializerMap.put(Class.class.getName(), new ClassSerializer());
        _staticDeserializerMap.put(Number.class.getName(), new BasicDeserializer(13));
        _staticSerializerMap.put(InetAddress.class.getName(), InetAddressSerializer.create());
        _staticSerializerMap.put(Date.class.getName(), new SqlDateSerializer());
        _staticSerializerMap.put(Time.class.getName(), new SqlDateSerializer());
        _staticSerializerMap.put(Timestamp.class.getName(), new SqlDateSerializer());
        _staticDeserializerMap.put(Date.class.getName(), new SqlDateDeserializer(Date.class));
        _staticDeserializerMap.put(Time.class.getName(), new SqlDateDeserializer(Time.class));
        _staticDeserializerMap.put(Timestamp.class.getName(), new SqlDateDeserializer(Timestamp.class));
        _staticDeserializerMap.put(StackTraceElement.class.getName(), new StackTraceElementDeserializer());
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

