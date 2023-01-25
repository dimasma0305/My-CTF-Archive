/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.caucho.hessian.server;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import com.caucho.services.server.Service;
import com.caucho.services.server.ServiceContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Logger;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HessianServlet
extends HttpServlet {
    private Class<?> _homeAPI;
    private Object _homeImpl;
    private Class<?> _objectAPI;
    private Object _objectImpl;
    private HessianSkeleton _homeSkeleton;
    private HessianSkeleton _objectSkeleton;
    private SerializerFactory _serializerFactory;

    public String getServletInfo() {
        return "Hessian Servlet";
    }

    public void setHomeAPI(Class<?> api) {
        this._homeAPI = api;
    }

    public void setHome(Object home) {
        this._homeImpl = home;
    }

    public void setObjectAPI(Class<?> api) {
        this._objectAPI = api;
    }

    public void setObject(Object object) {
        this._objectImpl = object;
    }

    public void setService(Object service) {
        this.setHome(service);
    }

    public void setAPIClass(Class<?> api) {
        this.setHomeAPI(api);
    }

    public Class<?> getAPIClass() {
        return this._homeAPI;
    }

    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    public SerializerFactory getSerializerFactory() {
        if (this._serializerFactory == null) {
            this._serializerFactory = new SerializerFactory();
        }
        return this._serializerFactory;
    }

    public void setSendCollectionType(boolean sendType) {
        this.getSerializerFactory().setSendCollectionType(sendType);
    }

    public void setDebug(boolean isDebug) {
    }

    public void setLogName(String name) {
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            String className;
            if (this._homeImpl == null) {
                Class<?> homeClass;
                if (this.getInitParameter("home-class") != null) {
                    className = this.getInitParameter("home-class");
                    homeClass = this.loadClass(className);
                    this._homeImpl = homeClass.newInstance();
                    this.init(this._homeImpl);
                } else if (this.getInitParameter("service-class") != null) {
                    className = this.getInitParameter("service-class");
                    homeClass = this.loadClass(className);
                    this._homeImpl = homeClass.newInstance();
                    this.init(this._homeImpl);
                } else {
                    if (((Object)((Object)this)).getClass().equals(HessianServlet.class)) {
                        throw new ServletException("server must extend HessianServlet");
                    }
                    this._homeImpl = this;
                }
            }
            if (this._homeAPI == null) {
                if (this.getInitParameter("home-api") != null) {
                    className = this.getInitParameter("home-api");
                    this._homeAPI = this.loadClass(className);
                } else if (this.getInitParameter("api-class") != null) {
                    className = this.getInitParameter("api-class");
                    this._homeAPI = this.loadClass(className);
                } else if (this._homeImpl != null) {
                    this._homeAPI = this.findRemoteAPI(this._homeImpl.getClass());
                    if (this._homeAPI == null) {
                        this._homeAPI = this._homeImpl.getClass();
                    }
                    this._homeAPI = this._homeImpl.getClass();
                }
            }
            if (this._objectImpl == null && this.getInitParameter("object-class") != null) {
                className = this.getInitParameter("object-class");
                Class<?> objectClass = this.loadClass(className);
                this._objectImpl = objectClass.newInstance();
                this.init(this._objectImpl);
            }
            if (this._objectAPI == null) {
                if (this.getInitParameter("object-api") != null) {
                    className = this.getInitParameter("object-api");
                    this._objectAPI = this.loadClass(className);
                } else if (this._objectImpl != null) {
                    this._objectAPI = this._objectImpl.getClass();
                }
            }
            this._homeSkeleton = new HessianSkeleton(this._homeImpl, this._homeAPI);
            if (this._objectAPI != null) {
                this._homeSkeleton.setObjectClass(this._objectAPI);
            }
            if (this._objectImpl != null) {
                this._objectSkeleton = new HessianSkeleton(this._objectImpl, this._objectAPI);
                this._objectSkeleton.setHomeClass(this._homeAPI);
            } else {
                this._objectSkeleton = this._homeSkeleton;
            }
            if ("true".equals(this.getInitParameter("debug"))) {
                // empty if block
            }
            if ("false".equals(this.getInitParameter("send-collection-type"))) {
                this.setSendCollectionType(false);
            }
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException((Throwable)e);
        }
    }

    private Class<?> findRemoteAPI(Class<?> implClass) {
        return null;
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        ClassLoader loader = this.getContextClassLoader();
        if (loader != null) {
            return Class.forName(className, false, loader);
        }
        return Class.forName(className);
    }

    protected ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private void init(Object service) throws ServletException {
        if (((Object)((Object)this)).getClass().equals(HessianServlet.class)) {
            if (service instanceof Service) {
                ((Service)service).init(this.getServletConfig());
            } else if (service instanceof Servlet) {
                ((Servlet)service).init(this.getServletConfig());
            }
        }
    }

    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        if (!req.getMethod().equals("POST")) {
            res.setStatus(500);
            PrintWriter out = res.getWriter();
            res.setContentType("text/html");
            out.println("<h1>Hessian Requires POST</h1>");
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
            response.setContentType("x-application/hessian");
            SerializerFactory serializerFactory = this.getSerializerFactory();
            this.invoke((InputStream)is, (OutputStream)os, objectId, serializerFactory);
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

    protected void invoke(InputStream is, OutputStream os, String objectId, SerializerFactory serializerFactory) throws Exception {
        if (objectId != null) {
            this._objectSkeleton.invoke(is, os, serializerFactory);
        } else {
            this._homeSkeleton.invoke(is, os, serializerFactory);
        }
    }

    protected Hessian2Input createHessian2Input(InputStream is) {
        return new Hessian2Input(is);
    }

    static class LogWriter
    extends Writer {
        private Logger _log;
        private StringBuilder _sb = new StringBuilder();

        LogWriter(Logger log) {
            this._log = log;
        }

        public void write(char ch) {
            if (ch == '\n' && this._sb.length() > 0) {
                this._log.fine(this._sb.toString());
                this._sb.setLength(0);
            } else {
                this._sb.append(ch);
            }
        }

        public void write(char[] buffer, int offset, int length) {
            for (int i = 0; i < length; ++i) {
                char ch = buffer[offset + i];
                if (ch == '\n' && this._sb.length() > 0) {
                    this._log.fine(this._sb.toString());
                    this._sb.setLength(0);
                    continue;
                }
                this._sb.append(ch);
            }
        }

        public void flush() {
        }

        public void close() {
        }
    }
}

