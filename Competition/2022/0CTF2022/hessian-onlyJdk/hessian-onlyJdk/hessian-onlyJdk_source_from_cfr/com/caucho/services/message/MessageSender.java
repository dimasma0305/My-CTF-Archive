/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.services.message;

import com.caucho.services.message.MessageServiceException;
import java.util.HashMap;

public interface MessageSender {
    public void send(HashMap var1, Object var2) throws MessageServiceException;
}

