/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  javax.servlet.GenericServlet
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.caucho.burlap.server;

import com.caucho.burlap.io.BurlapInput;
import com.caucho.burlap.io.BurlapOutput;
import com.caucho.burlap.server.BurlapSkeleton;
import com.caucho.services.server.Service;
import com.caucho.services.server.ServiceContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BurlapServlet
extends GenericServlet {
    private Class<?> _apiClass;
    private Object _service;
    private BurlapSkeleton _skeleton;

    public String getServletInfo() {
        return "Burlap Servlet";
    }

    public void setService(Object service) {
        this._service = service;
    }

    public void setAPIClass(Class<?> apiClass) {
        this._apiClass = apiClass;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            String className;
            if (this._service == null) {
                className = this.getInitParameter("service-class");
                Class<?> serviceClass = null;
                if (className != null) {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    serviceClass = loader != null ? Class.forName(className, false, loader) : Class.forName(className);
                } else {
                    if (((Object)((Object)this)).getClass().equals(BurlapServlet.class)) {
                        throw new ServletException("server must extend BurlapServlet");
                    }
                    serviceClass = ((Object)((Object)this)).getClass();
                }
                this._service = serviceClass.newInstance();
                if (this._service instanceof BurlapServlet) {
                    ((BurlapServlet)((Object)this._service)).setService((Object)this);
                }
                if (this._service instanceof Service) {
                    ((Service)this._service).init(this.getServletConfig());
                } else if (this._service instanceof Servlet) {
                    ((Servlet)this._service).init(this.getServletConfig());
                }
            }
            if (this._apiClass == null) {
                ClassLoader loader;
                className = this.getInitParameter("api-class");
                this._apiClass = className != null ? ((loader = Thread.currentThread().getContextClassLoader()) != null ? Class.forName(className, false, loader) : Class.forName(className)) : this._service.getClass();
            }
            this._skeleton = new BurlapSkeleton(this._service, this._apiClass);
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException((Throwable)e);
        }
    }

    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        if (!req.getMethod().equals("POST")) {
            res.setStatus(500, "Burlap Requires POST");
            PrintWriter out = res.getWriter();
            res.setContentType("text/html");
            out.println("<h1>Burlap Requires POST</h1>");
            return;
        }
        String serviceId = req.getPathInfo();
        String objectId = req.getParameter("id");
        if (objectId == null) {
            objectId = req.getParameter("ejbid");
        }
        ServiceContext.begin((ServletRequest)req, (ServletResponse)res, serviceId, objectId);
        try {
            ServletInputStream is = request.getInputStream();
            ServletOutputStream os = response.getOutputStream();
            BurlapInput in = new BurlapInput((InputStream)is);
            BurlapOutput out = new BurlapOutput((OutputStream)os);
            this._skeleton.invoke(in, out);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new ServletException(e);
        }
        finally {
            ServiceContext.end();
        }
    }
}

