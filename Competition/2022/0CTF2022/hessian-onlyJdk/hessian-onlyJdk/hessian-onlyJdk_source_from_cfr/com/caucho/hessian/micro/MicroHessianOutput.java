/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.micro;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MicroHessianOutput {
    protected OutputStream os;

    public MicroHessianOutput(OutputStream os) {
        this.init(os);
    }

    public MicroHessianOutput() {
    }

    public void init(OutputStream os) {
        this.os = os;
    }

    public void startCall(String method) throws IOException {
        this.os.write(99);
        this.os.write(0);
        this.os.write(1);
        this.os.write(109);
        int len = method.length();
        this.os.write(len >> 8);
        this.os.write(len);
        this.printString(method, 0, len);
    }

    public void completeCall() throws IOException {
        this.os.write(122);
    }

    public void writeBoolean(boolean value) throws IOException {
        if (value) {
            this.os.write(84);
        } else {
            this.os.write(70);
        }
    }

    public void writeInt(int value) throws IOException {
        this.os.write(73);
        this.os.write(value >> 24);
        this.os.write(value >> 16);
        this.os.write(value >> 8);
        this.os.write(value);
    }

    public void writeLong(long value) throws IOException {
        this.os.write(76);
        this.os.write((byte)(value >> 56));
        this.os.write((byte)(value >> 48));
        this.os.write((byte)(value >> 40));
        this.os.write((byte)(value >> 32));
        this.os.write((byte)(value >> 24));
        this.os.write((byte)(value >> 16));
        this.os.write((byte)(value >> 8));
        this.os.write((byte)value);
    }

    public void writeUTCDate(long time) throws IOException {
        this.os.write(100);
        this.os.write((byte)(time >> 56));
        this.os.write((byte)(time >> 48));
        this.os.write((byte)(time >> 40));
        this.os.write((byte)(time >> 32));
        this.os.write((byte)(time >> 24));
        this.os.write((byte)(time >> 16));
        this.os.write((byte)(time >> 8));
        this.os.write((byte)time);
    }

    public void writeNull() throws IOException {
        this.os.write(78);
    }

    public void writeString(String value) throws IOException {
        if (value == null) {
            this.os.write(78);
        } else {
            int len = value.length();
            this.os.write(83);
            this.os.write(len >> 8);
            this.os.write(len);
            this.printString(value);
        }
    }

    public void writeBytes(byte[] buffer) throws IOException {
        if (buffer == null) {
            this.os.write(78);
        } else {
            this.writeBytes(buffer, 0, buffer.length);
        }
    }

    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            this.os.write(78);
        } else {
            this.os.write(66);
            this.os.write(length << 8);
            this.os.write(length);
            this.os.write(buffer, offset, length);
        }
    }

    public void writeRef(int value) throws IOException {
        this.os.write(82);
        this.os.write(value << 24);
        this.os.write(value << 16);
        this.os.write(value << 8);
        this.os.write(value);
    }

    public void writeObject(Object object) throws IOException {
        if (object == null) {
            this.writeNull();
        } else if (object instanceof String) {
            this.writeString((String)object);
        } else if (object instanceof Boolean) {
            this.writeBoolean((Boolean)object);
        } else if (object instanceof Integer) {
            this.writeInt((Integer)object);
        } else if (object instanceof Long) {
            this.writeLong((Long)object);
        } else if (object instanceof Date) {
            this.writeUTCDate(((Date)object).getTime());
        } else if (object instanceof byte[]) {
            byte[] data = (byte[])object;
            this.writeBytes(data, 0, data.length);
        } else if (object instanceof Vector) {
            Vector vector = (Vector)object;
            int size = vector.size();
            this.writeListBegin(size, null);
            for (int i = 0; i < size; ++i) {
                this.writeObject(vector.elementAt(i));
            }
            this.writeListEnd();
        } else if (object instanceof Hashtable) {
            Hashtable hashtable = (Hashtable)object;
            this.writeMapBegin(null);
            Enumeration e = hashtable.keys();
            while (e.hasMoreElements()) {
                Object key = e.nextElement();
                Object value = hashtable.get(key);
                this.writeObject(key);
                this.writeObject(value);
            }
            this.writeMapEnd();
        } else {
            this.writeCustomObject(object);
        }
    }

    public void writeCustomObject(Object object) throws IOException {
        throw new IOException("unexpected object: " + object);
    }

    public void writeListBegin(int length, String type) throws IOException {
        this.os.write(86);
        this.os.write(116);
        this.printLenString(type);
        this.os.write(108);
        this.os.write(length >> 24);
        this.os.write(length >> 16);
        this.os.write(length >> 8);
        this.os.write(length);
    }

    public void writeListEnd() throws IOException {
        this.os.write(122);
    }

    public void writeMapBegin(String type) throws IOException {
        this.os.write(77);
        this.os.write(116);
        this.printLenString(type);
    }

    public void writeMapEnd() throws IOException {
        this.os.write(122);
    }

    public void writeRemote(String type, String url) throws IOException {
        this.os.write(114);
        this.os.write(116);
        this.printLenString(type);
        this.os.write(83);
        this.printLenString(url);
    }

    public void printLenString(String v) throws IOException {
        if (v == null) {
            this.os.write(0);
            this.os.write(0);
        } else {
            int len = v.length();
            this.os.write(len >> 8);
            this.os.write(len);
            this.printString(v, 0, len);
        }
    }

    public void printString(String v) throws IOException {
        this.printString(v, 0, v.length());
    }

    public void printString(String v, int offset, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            char ch = v.charAt(i + offset);
            if (ch < '\u0080') {
                this.os.write(ch);
                continue;
            }
            if (ch < '\u0800') {
                this.os.write(192 + (ch >> 6 & 0x1F));
                this.os.write(128 + (ch & 0x3F));
                continue;
            }
            this.os.write(224 + (ch >> 12 & 0xF));
            this.os.write(128 + (ch >> 6 & 0x3F));
            this.os.write(128 + (ch & 0x3F));
        }
    }
}

