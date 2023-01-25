/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BeanSerializer
extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(BeanSerializer.class.getName());
    private static final Object[] NULL_ARGS = new Object[0];
    private Method[] _methods;
    private String[] _names;
    private Object _writeReplaceFactory;
    private Method _writeReplace;

    public BeanSerializer(Class<?> cl, ClassLoader loader) {
        int i;
        this.introspectWriteReplace(cl, loader);
        ArrayList<Method> primitiveMethods = new ArrayList<Method>();
        ArrayList<Method> compoundMethods = new ArrayList<Method>();
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (i = 0; i < methods.length; ++i) {
                Class<?> type;
                String name;
                Method method = methods[i];
                if (Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 0 || !(name = method.getName()).startsWith("get") || (type = method.getReturnType()).equals(Void.TYPE) || this.findSetter(methods, name, type) == null) continue;
                method.setAccessible(true);
                if (type.isPrimitive() || type.getName().startsWith("java.lang.") && !type.equals(Object.class)) {
                    primitiveMethods.add(method);
                    continue;
                }
                compoundMethods.add(method);
            }
            cl = cl.getSuperclass();
        }
        ArrayList<Method> methodList = new ArrayList<Method>();
        methodList.addAll(primitiveMethods);
        methodList.addAll(compoundMethods);
        Collections.sort(methodList, new MethodNameCmp());
        this._methods = new Method[methodList.size()];
        methodList.toArray(this._methods);
        this._names = new String[this._methods.length];
        for (i = 0; i < this._methods.length; ++i) {
            int j;
            String name = this._methods[i].getName();
            name = name.substring(3);
            for (j = 0; j < name.length() && Character.isUpperCase(name.charAt(j)); ++j) {
            }
            if (j == 1) {
                name = name.substring(0, j).toLowerCase(Locale.ENGLISH) + name.substring(j);
            } else if (j > 1) {
                name = name.substring(0, j - 1).toLowerCase(Locale.ENGLISH) + name.substring(j - 1);
            }
            this._names[i] = name;
        }
    }

    private void introspectWriteReplace(Class cl, ClassLoader loader) {
        try {
            String className = cl.getName() + "HessianSerializer";
            Class<?> serializerClass = Class.forName(className, false, loader);
            Object serializerObject = serializerClass.newInstance();
            Method writeReplace = this.getWriteReplace(serializerClass, cl);
            if (writeReplace != null) {
                this._writeReplaceFactory = serializerObject;
                this._writeReplace = writeReplace;
                return;
            }
        }
        catch (ClassNotFoundException e) {
        }
        catch (Exception e) {
            log.log(Level.FINER, e.toString(), e);
        }
        this._writeReplace = this.getWriteReplace(cl);
    }

    protected Method getWriteReplace(Class cl) {
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

    protected Method getWriteReplace(Class cl, Class param) {
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
                out.writeObject(repl);
                out.replaceRef(repl, obj);
                return;
            }
        }
        catch (Exception e) {
            log.log(Level.FINER, e.toString(), e);
        }
        int ref = out.writeObjectBegin(cl.getName());
        if (ref < -1) {
            for (int i = 0; i < this._methods.length; ++i) {
                Method method = this._methods[i];
                Object value = null;
                try {
                    value = this._methods[i].invoke(obj, null);
                }
                catch (Exception e) {
                    log.log(Level.FINE, e.toString(), e);
                }
                out.writeString(this._names[i]);
                out.writeObject(value);
            }
            out.writeMapEnd();
        } else {
            int i;
            if (ref == -1) {
                out.writeInt(this._names.length);
                for (i = 0; i < this._names.length; ++i) {
                    out.writeString(this._names[i]);
                }
                out.writeObjectBegin(cl.getName());
            }
            for (i = 0; i < this._methods.length; ++i) {
                Method method = this._methods[i];
                Object value = null;
                try {
                    value = this._methods[i].invoke(obj, null);
                }
                catch (Exception e) {
                    log.log(Level.FINER, e.toString(), e);
                }
                out.writeObject(value);
            }
        }
    }

    private Method findSetter(Method[] methods, String getterName, Class arg) {
        String setterName = "set" + getterName.substring(3);
        for (int i = 0; i < methods.length; ++i) {
            Class<?>[] params;
            Method method = methods[i];
            if (!method.getName().equals(setterName) || !method.getReturnType().equals(Void.TYPE) || (params = method.getParameterTypes()).length != 1 || !params[0].equals(arg)) continue;
            return method;
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class MethodNameCmp
    implements Comparator<Method> {
        MethodNameCmp() {
        }

        @Override
        public int compare(Method a, Method b) {
            return a.getName().compareTo(b.getName());
        }
    }
}

