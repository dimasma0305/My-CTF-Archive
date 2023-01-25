/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.caucho.services.server;

import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServiceContext {
    private static final ThreadLocal<ServiceContext> _localContext = new ThreadLocal();
    private ServletRequest _request;
    private ServletResponse _response;
    private String _serviceName;
    private String _objectId;
    private int _count;
    private HashMap _headers = new HashMap();

    private ServiceContext() {
    }

    public static void begin(ServletRequest request, ServletResponse response, String serviceName, String objectId) throws ServletException {
        ServiceContext context = _localContext.get();
        if (context == null) {
            context = new ServiceContext();
            _localContext.set(context);
        }
        context._request = request;
        context._response = response;
        context._serviceName = serviceName;
        context._objectId = objectId;
        ++context._count;
    }

    public static ServiceContext getContext() {
        return _localContext.get();
    }

    public void addHeader(String header, Object value) {
        this._headers.put(header, value);
    }

    public Object getHeader(String header) {
        return this._headers.get(header);
    }

    public static Object getContextHeader(String header) {
        ServiceContext context = _localContext.get();
        if (context != null) {
            return context.getHeader(header);
        }
        return null;
    }

    public static ServletRequest getContextRequest() {
        ServiceContext context = _localContext.get();
        if (context != null) {
            return context._request;
        }
        return null;
    }

    public static ServletResponse getContextResponse() {
        ServiceContext context = _localContext.get();
        if (context != null) {
            return context._response;
        }
        return null;
    }

    public static String getContextServiceName() {
        ServiceContext context = _localContext.get();
        if (context != null) {
            return context._serviceName;
        }
        return null;
    }

    public static String getContextObjectId() {
        ServiceContext context = _localContext.get();
        if (context != null) {
            return context._objectId;
        }
        return null;
    }

    public static void end() {
        ServiceContext context = _localContext.get();
        if (context != null && --context._count == 0) {
            context._request = null;
            context._response = null;
            context._headers.clear();
            _localContext.set(null);
        }
    }

    public static ServletRequest getRequest() {
        ServiceContext context = _localContext.get();
        if (context != null) {
            return context._request;
        }
        return null;
    }

    public static String getServiceName() {
        ServiceContext context = _localContext.get();
        if (context != null) {
            return context._serviceName;
        }
        return null;
    }

    public static String getObjectId() {
        ServiceContext context = _localContext.get();
        if (context != null) {
            return context._objectId;
        }
        return null;
    }
}

