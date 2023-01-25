/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.io;

public class BurlapRemote {
    private String type;
    private String url;

    public BurlapRemote(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public BurlapRemote() {
    }

    public String getType() {
        return this.type;
    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public int hashCode() {
        return this.url.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BurlapRemote)) {
            return false;
        }
        BurlapRemote remote = (BurlapRemote)obj;
        return this.url.equals(remote.url);
    }

    public String toString() {
        return "[BurlapRemote " + this.url + "]";
    }
}

