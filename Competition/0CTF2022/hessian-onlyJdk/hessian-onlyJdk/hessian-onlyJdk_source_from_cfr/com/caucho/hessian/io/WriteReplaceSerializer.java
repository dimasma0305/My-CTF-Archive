/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.Serializer;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WriteReplaceSerializer
extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(WriteReplaceSerializer.class.getName());
    private Object _writeReplaceFactory;
    private Method _writeReplace;
    private Serializer _baseSerializer;

    public WriteReplaceSerializer(Class<?> cl, ClassLoader loader, Serializer baseSerializer) {
        this.introspectWriteReplace(cl, loader);
        this._baseSerializer = baseSerializer;
    }

    private void introspectWriteReplace(Class<?> cl, ClassLoader loader) {
        try {
            String className = cl.getName() + "HessianSerializer";
            Class<?> serializerClass = Class.forName(className, false, loader);
            Object serializerObject = serializerClass.newInstance();
            Method writeReplace = WriteReplaceSerializer.getWriteReplace(serializerClass, cl);
            if (writeReplace != null) {
                this._writeReplaceFactory = serializerObject;
                this._writeReplace = writeReplace;
            }
        }
        catch (ClassNotFoundException e) {
        }
        catch (Exception e) {
            log.log(Level.FINER, e.toString(), e);
        }
        this._writeReplace = WriteReplaceSerializer.getWriteReplace(cl);
        if (this._writeReplace != null) {
            this._writeReplace.setAccessible(true);
        }
    }

    protected static Method getWriteReplace(Class<?> cl, Class<?> param) {
        while (cl != null) {
            for (Method method : cl.getDeclaredMethods()) {
                if (!method.getName().equals("writeReplace") || method.getParameterTypes().length != 1 || !param.equals(method.getParameterTypes()[0])) continue;
                return method;
            }
            cl = cl.getSuperclass();
        }
        return null;
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

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        int ref = out.getRef(obj);
        if (ref >= 0) {
            out.writeRef(ref);
            return;
        }
        try {
            Object repl = this.writeReplace(obj);
            if (obj == repl) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine(this + ": Hessian writeReplace error.  The writeReplace method (" + this._writeReplace + ") must not return the same object: " + obj);
                }
                this._baseSerializer.writeObject(obj, out);
                return;
            }
            out.writeObject(repl);
            out.replaceRef(repl, obj);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object writeReplace(Object obj) {
        try {
            if (this._writeReplaceFactory != null) {
                return this._writeReplace.invoke(this._writeReplaceFactory, obj);
            }
            return this._writeReplace.invoke(obj, new Object[0]);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

