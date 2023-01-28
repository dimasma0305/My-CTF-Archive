/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.client;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianURLConnection;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HessianURLConnectionFactory
implements HessianConnectionFactory {
    private static final Logger log = Logger.getLogger(HessianURLConnectionFactory.class.getName());
    private HessianProxyFactory _proxyFactory;

    public void setHessianProxyFactory(HessianProxyFactory factory) {
        this._proxyFactory = factory;
    }

    public HessianConnection open(URL url) throws IOException {
        if (log.isLoggable(Level.FINER)) {
            log.finer(this + " open(" + url + ")");
        }
        URLConnection conn = url.openConnection();
        long connectTimeout = this._proxyFactory.getConnectTimeout();
        if (connectTimeout >= 0L) {
            conn.setConnectTimeout((int)connectTimeout);
        }
        conn.setDoOutput(true);
        long readTimeout = this._proxyFactory.getReadTimeout();
        if (readTimeout > 0L) {
            try {
                conn.setReadTimeout((int)readTimeout);
            }
            catch (Throwable e) {
                // empty catch block
            }
        }
        return new HessianURLConnection(url, conn);
    }
}

