/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InetAddressHandle
implements Serializable,
HessianHandle {
    private static final Logger log = Logger.getLogger(InetAddressHandle.class.getName());
    private String hostName;
    private byte[] address;

    public InetAddressHandle(String hostName, byte[] address) {
        this.hostName = hostName;
        this.address = address;
    }

    private Object readResolve() {
        try {
            return InetAddress.getByAddress(this.hostName, this.address);
        }
        catch (Exception e) {
            log.log(Level.FINE, e.toString(), e);
            return null;
        }
    }
}

