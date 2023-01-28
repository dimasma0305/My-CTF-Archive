/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.client.HessianConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractHessianConnection
implements HessianConnection {
    public void addHeader(String key, String value) {
    }

    public abstract OutputStream getOutputStream() throws IOException;

    public abstract void sendRequest() throws IOException;

    public abstract int getStatusCode();

    public abstract String getStatusMessage();

    public abstract InputStream getInputStream() throws IOException;

    public String getContentEncoding() {
        return null;
    }

    public void close() throws IOException {
        this.destroy();
    }

    public abstract void destroy() throws IOException;
}

