/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.server;

import com.caucho.burlap.io.BurlapInput;
import com.caucho.burlap.io.BurlapOutput;
import com.caucho.services.server.AbstractSkeleton;
import com.caucho.services.server.ServiceContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BurlapSkeleton
extends AbstractSkeleton {
    private static final Logger log = Logger.getLogger(BurlapSkeleton.class.getName());
    private Object _service;

    public BurlapSkeleton(Object service, Class apiClass) {
        super(apiClass);
        this._service = service;
    }

    public BurlapSkeleton(Class apiClass) {
        super(apiClass);
    }

    public void invoke(BurlapInput in, BurlapOutput out) throws Exception {
        this.invoke(this._service, in, out);
    }

    public void invoke(Object service, BurlapInput in, BurlapOutput out) throws Exception {
        String header;
        in.readCall();
        ServiceContext context = ServiceContext.getContext();
        while ((header = in.readHeader()) != null) {
            Object value = in.readObject();
            context.addHeader(header, value);
        }
        String methodName = in.readMethod();
        Method method = this.getMethod(methodName);
        if (log.isLoggable(Level.FINE)) {
            log.fine(this + " invoking " + methodName + " (" + method + ")");
        }
        if (method == null) {
            if ("_burlap_getAttribute".equals(in.getMethod())) {
                String attrName = in.readString();
                in.completeCall();
                String value = null;
                if ("java.api.class".equals(attrName)) {
                    value = this.getAPIClassName();
                } else if ("java.home.class".equals(attrName)) {
                    value = this.getHomeClassName();
                } else if ("java.object.class".equals(attrName)) {
                    value = this.getObjectClassName();
                }
                out.startReply();
                out.writeObject(value);
                out.completeReply();
                return;
            }
            if (method == null) {
                out.startReply();
                out.writeFault("NoSuchMethodException", "The service has no method named: " + in.getMethod(), null);
                out.completeReply();
                return;
            }
        }
        Class<?>[] args = method.getParameterTypes();
        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; ++i) {
            values[i] = in.readObject(args[i]);
        }
        in.completeCall();
        Object result = null;
        try {
            result = method.invoke(service, values);
        }
        catch (Throwable e) {
            log.log(Level.FINE, service + "." + method.getName() + "() failed with exception:\n" + e.toString(), e);
            if (e instanceof InvocationTargetException && e.getCause() instanceof Exception) {
                e = ((InvocationTargetException)e).getTargetException();
            }
            out.startReply();
            out.writeFault("ServiceException", e.getMessage(), e);
            out.completeReply();
            return;
        }
        out.startReply();
        out.writeObject(result);
        out.completeReply();
    }
}

