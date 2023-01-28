/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 */
package com.caucho.services.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public interface Service {
    public void init(ServletConfig var1) throws ServletException;

    public void destroy();
}

