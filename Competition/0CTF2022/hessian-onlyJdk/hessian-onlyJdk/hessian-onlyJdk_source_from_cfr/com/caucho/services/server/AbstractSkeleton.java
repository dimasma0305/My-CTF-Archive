/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.services.server;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class AbstractSkeleton {
    private Class _apiClass;
    private Class _homeClass;
    private Class _objectClass;
    private HashMap _methodMap = new HashMap();

    protected AbstractSkeleton(Class apiClass) {
        this._apiClass = apiClass;
        Method[] methodList = apiClass.getMethods();
        for (int i = 0; i < methodList.length; ++i) {
            Method method = methodList[i];
            if (this._methodMap.get(method.getName()) == null) {
                this._methodMap.put(method.getName(), methodList[i]);
            }
            Class<?>[] param = method.getParameterTypes();
            String mangledName = method.getName() + "__" + param.length;
            this._methodMap.put(mangledName, methodList[i]);
            this._methodMap.put(AbstractSkeleton.mangleName(method, false), methodList[i]);
        }
    }

    public String getAPIClassName() {
        return this._apiClass.getName();
    }

    public String getHomeClassName() {
        if (this._homeClass != null) {
            return this._homeClass.getName();
        }
        return this.getAPIClassName();
    }

    public void setHomeClass(Class homeAPI) {
        this._homeClass = homeAPI;
    }

    public String getObjectClassName() {
        if (this._objectClass != null) {
            return this._objectClass.getName();
        }
        return this.getAPIClassName();
    }

    public void setObjectClass(Class objectAPI) {
        this._objectClass = objectAPI;
    }

    protected Method getMethod(String mangledName) {
        return (Method)this._methodMap.get(mangledName);
    }

    public static String mangleName(Method method, boolean isFull) {
        StringBuffer sb = new StringBuffer();
        sb.append(method.getName());
        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            sb.append('_');
            sb.append(AbstractSkeleton.mangleClass(params[i], isFull));
        }
        return sb.toString();
    }

    public static String mangleClass(Class cl, boolean isFull) {
        String name = cl.getName();
        if (name.equals("boolean") || name.equals("java.lang.Boolean")) {
            return "boolean";
        }
        if (name.equals("int") || name.equals("java.lang.Integer") || name.equals("short") || name.equals("java.lang.Short") || name.equals("byte") || name.equals("java.lang.Byte")) {
            return "int";
        }
        if (name.equals("long") || name.equals("java.lang.Long")) {
            return "long";
        }
        if (name.equals("float") || name.equals("java.lang.Float") || name.equals("double") || name.equals("java.lang.Double")) {
            return "double";
        }
        if (name.equals("java.lang.String") || name.equals("com.caucho.util.CharBuffer") || name.equals("char") || name.equals("java.lang.Character") || name.equals("java.io.Reader")) {
            return "string";
        }
        if (name.equals("java.util.Date") || name.equals("com.caucho.util.QDate")) {
            return "date";
        }
        if (InputStream.class.isAssignableFrom(cl) || name.equals("[B")) {
            return "binary";
        }
        if (cl.isArray()) {
            return "[" + AbstractSkeleton.mangleClass(cl.getComponentType(), isFull);
        }
        if (name.equals("org.w3c.dom.Node") || name.equals("org.w3c.dom.Element") || name.equals("org.w3c.dom.Document")) {
            return "xml";
        }
        if (isFull) {
            return name;
        }
        int p = name.lastIndexOf(46);
        if (p > 0) {
            return name.substring(p + 1);
        }
        return name;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this._apiClass.getName() + "]";
    }
}

