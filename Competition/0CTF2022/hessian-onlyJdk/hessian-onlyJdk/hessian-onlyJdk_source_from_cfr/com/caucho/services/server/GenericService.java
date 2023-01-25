/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 */
package com.caucho.services.server;

import com.caucho.services.server.Service;
import com.caucho.services.server.ServiceContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

public class GenericService
implements Service {
    protected ServletConfig config;

    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        this.init();
    }

    public void init() throws ServletException {
    }

    public String getInitParameter(String name) {
        return this.config.getInitParameter(name);
    }

    public ServletConfig getServletConfig() {
        return this.config;
    }

    public ServletContext getServletContext() {
        return this.config.getServletContext();
    }

    public void log(String message) {
        this.getServletContext().log(message);
    }

    public ServletRequest getRequest() {
        return ServiceContext.getRequest();
    }

    public String getServiceName() {
        return ServiceContext.getServiceName();
    }

    public String getServiceId() {
        return this.getServiceName();
    }

    public String getObjectId() {
        return ServiceContext.getObjectId();
    }

    public void destroy() {
    }
}

