/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.io;

import com.caucho.burlap.io.BurlapRemote;
import com.caucho.burlap.io.BurlapRemoteResolver;
import java.io.IOException;

public class AbstractBurlapResolver
implements BurlapRemoteResolver {
    public Object lookup(String type, String url) throws IOException {
        return new BurlapRemote(type, url);
    }
}

