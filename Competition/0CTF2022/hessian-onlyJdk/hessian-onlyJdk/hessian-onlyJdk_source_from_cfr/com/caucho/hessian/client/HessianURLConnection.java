/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.client.AbstractHessianConnection;
import com.caucho.hessian.client.HessianConnectionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HessianURLConnection
extends AbstractHessianConnection {
    private URL _url;
    private URLConnection _conn;
    private int _statusCode;
    private String _statusMessage;
    private InputStream _inputStream;
    private InputStream _errorStream;

    HessianURLConnection(URL url, URLConnection conn) {
        this._url = url;
        this._conn = conn;
    }

    public void addHeader(String key, String value) {
        this._conn.setRequestProperty(key, value);
    }

    public OutputStream getOutputStream() throws IOException {
        return this._conn.getOutputStream();
    }

    public void sendRequest() throws IOException {
        if (this._conn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = (HttpURLConnection)this._conn;
            this._statusCode = 500;
            try {
                this._statusCode = httpConn.getResponseCode();
            }
            catch (Exception e) {
                // empty catch block
            }
            this.parseResponseHeaders(httpConn);
            InputStream is = null;
            if (this._statusCode != 200) {
                StringBuffer sb = new StringBuffer();
                try {
                    int ch;
                    is = httpConn.getInputStream();
                    if (is != null) {
                        while ((ch = is.read()) >= 0) {
                            sb.append((char)ch);
                        }
                        is.close();
                    }
                    if ((is = httpConn.getErrorStream()) != null) {
                        while ((ch = is.read()) >= 0) {
                            sb.append((char)ch);
                        }
                    }
                    this._statusMessage = sb.toString();
                }
                catch (FileNotFoundException e) {
                    throw new HessianConnectionException("HessianProxy cannot connect to '" + this._url, e);
                }
                catch (IOException e) {
                    if (is == null) {
                        throw new HessianConnectionException(this._statusCode + ": " + e, e);
                    }
                    throw new HessianConnectionException(this._statusCode + ": " + sb, e);
                }
                if (is != null) {
                    is.close();
                }
                throw new HessianConnectionException(this._statusCode + ": " + sb.toString());
            }
        }
    }

    protected void parseResponseHeaders(HttpURLConnection conn) throws IOException {
    }

    public int getStatusCode() {
        return this._statusCode;
    }

    public String getStatusMessage() {
        return this._statusMessage;
    }

    public InputStream getInputStream() throws IOException {
        return this._conn.getInputStream();
    }

    public String getContentEncoding() {
        return this._conn.getContentEncoding();
    }

    public void close() {
        this._inputStream = null;
    }

    public void destroy() {
        this.close();
        URLConnection conn = this._conn;
        this._conn = null;
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection)conn).disconnect();
        }
    }
}

