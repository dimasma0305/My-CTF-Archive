/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.io;

import com.caucho.burlap.io.AbstractBurlapOutput;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SerializerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.TimeZone;

public class BurlapOutput
extends AbstractBurlapOutput {
    protected OutputStream os;
    private IdentityHashMap _refs;
    private Date date;
    private Calendar utcCalendar;
    private Calendar localCalendar;

    public BurlapOutput(OutputStream os) {
        this.init(os);
    }

    public BurlapOutput() {
    }

    public void init(OutputStream os) {
        this.os = os;
        this._refs = null;
        if (this._serializerFactory == null) {
            this._serializerFactory = new SerializerFactory();
        }
    }

    public void call(String method, Object[] args) throws IOException {
        this.startCall(method);
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                this.writeObject(args[i]);
            }
        }
        this.completeCall();
    }

    public void startCall(String method) throws IOException {
        this.print("<burlap:call><method>");
        this.print(method);
        this.print("</method>");
    }

    public void startCall() throws IOException {
        this.print("<burlap:call>");
    }

    public void writeMethod(String method) throws IOException {
        this.print("<method>");
        this.print(method);
        this.print("</method>");
    }

    public void completeCall() throws IOException {
        this.print("</burlap:call>");
    }

    public void startReply() throws IOException {
        this.print("<burlap:reply>");
    }

    public void completeReply() throws IOException {
        this.print("</burlap:reply>");
    }

    public void writeHeader(String name) throws IOException {
        this.print("<header>");
        this.printString(name);
        this.print("</header>");
    }

    public void writeFault(String code, String message, Object detail) throws IOException {
        this.print("<fault>");
        this.writeString("code");
        this.writeString(code);
        this.writeString("message");
        this.writeString(message);
        if (detail != null) {
            this.writeString("detail");
            this.writeObject(detail);
        }
        this.print("</fault>");
    }

    public void writeObject(Object object) throws IOException {
        if (object == null) {
            this.writeNull();
            return;
        }
        Serializer serializer = this._serializerFactory.getSerializer(object.getClass());
        serializer.writeObject(object, this);
    }

    public boolean writeListBegin(int length, String type) throws IOException {
        this.print("<list><type>");
        if (type != null) {
            this.print(type);
        }
        this.print("</type><length>");
        this.print(length);
        this.print("</length>");
        return true;
    }

    public void writeListEnd() throws IOException {
        this.print("</list>");
    }

    public void writeMapBegin(String type) throws IOException {
        this.print("<map><type>");
        if (type != null) {
            this.print(type);
        }
        this.print("</type>");
    }

    public void writeMapEnd() throws IOException {
        this.print("</map>");
    }

    public void writeRemote(String type, String url) throws IOException {
        this.print("<remote><type>");
        this.print(type);
        this.print("</type><string>");
        this.print(url);
        this.print("</string></remote>");
    }

    public void writeBoolean(boolean value) throws IOException {
        if (value) {
            this.print("<boolean>1</boolean>");
        } else {
            this.print("<boolean>0</boolean>");
        }
    }

    public void writeInt(int value) throws IOException {
        this.print("<int>");
        this.print(value);
        this.print("</int>");
    }

    public void writeLong(long value) throws IOException {
        this.print("<long>");
        this.print(value);
        this.print("</long>");
    }

    public void writeDouble(double value) throws IOException {
        this.print("<double>");
        this.print(value);
        this.print("</double>");
    }

    public void writeUTCDate(long time) throws IOException {
        this.print("<date>");
        if (this.utcCalendar == null) {
            this.utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            this.date = new Date();
        }
        this.date.setTime(time);
        this.utcCalendar.setTime(this.date);
        this.printDate(this.utcCalendar);
        this.print("</date>");
    }

    public void writeNull() throws IOException {
        this.print("<null></null>");
    }

    public void writeString(String value) throws IOException {
        if (value == null) {
            this.print("<null></null>");
        } else {
            this.print("<string>");
            this.printString(value);
            this.print("</string>");
        }
    }

    public void writeString(char[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            this.print("<null></null>");
        } else {
            this.print("<string>");
            this.printString(buffer, offset, length);
            this.print("</string>");
        }
    }

    public void writeBytes(byte[] buffer) throws IOException {
        if (buffer == null) {
            this.print("<null></null>");
        } else {
            this.writeBytes(buffer, 0, buffer.length);
        }
    }

    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            this.print("<null></null>");
        } else {
            int v;
            this.print("<base64>");
            int i = 0;
            while (i + 2 < length) {
                if (i != 0 && (i & 0x3F) == 0) {
                    this.print('\n');
                }
                v = ((buffer[offset + i] & 0xFF) << 16) + ((buffer[offset + i + 1] & 0xFF) << 8) + (buffer[offset + i + 2] & 0xFF);
                this.print(this.encode(v >> 18));
                this.print(this.encode(v >> 12));
                this.print(this.encode(v >> 6));
                this.print(this.encode(v));
                i += 3;
            }
            if (i + 1 < length) {
                v = ((buffer[offset + i] & 0xFF) << 8) + (buffer[offset + i + 1] & 0xFF);
                this.print(this.encode(v >> 10));
                this.print(this.encode(v >> 4));
                this.print(this.encode(v << 2));
                this.print('=');
            } else if (i < length) {
                v = buffer[offset + i] & 0xFF;
                this.print(this.encode(v >> 2));
                this.print(this.encode(v << 4));
                this.print('=');
                this.print('=');
            }
            this.print("</base64>");
        }
    }

    public void writeByteBufferStart() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeByteBufferPart(byte[] buffer, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void writeByteBufferEnd(byte[] buffer, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    private char encode(int d) {
        if ((d &= 0x3F) < 26) {
            return (char)(d + 65);
        }
        if (d < 52) {
            return (char)(d + 97 - 26);
        }
        if (d < 62) {
            return (char)(d + 48 - 52);
        }
        if (d == 62) {
            return '+';
        }
        return '/';
    }

    public void writeRef(int value) throws IOException {
        this.print("<ref>");
        this.print(value);
        this.print("</ref>");
    }

    public boolean addRef(Object object) throws IOException {
        Integer ref;
        if (this._refs == null) {
            this._refs = new IdentityHashMap();
        }
        if ((ref = (Integer)this._refs.get(object)) != null) {
            int value = ref;
            this.writeRef(value);
            return true;
        }
        this._refs.put(object, new Integer(this._refs.size()));
        return false;
    }

    public int getRef(Object obj) {
        if (this._refs == null) {
            return -1;
        }
        Integer ref = (Integer)this._refs.get(obj);
        if (ref != null) {
            return ref;
        }
        return -1;
    }

    public boolean removeRef(Object obj) throws IOException {
        if (this._refs != null) {
            this._refs.remove(obj);
            return true;
        }
        return false;
    }

    public boolean replaceRef(Object oldRef, Object newRef) throws IOException {
        Integer value = (Integer)this._refs.remove(oldRef);
        if (value != null) {
            this._refs.put(newRef, value);
            return true;
        }
        return false;
    }

    public void printString(String v) throws IOException {
        this.printString(v, 0, v.length());
    }

    public void printString(String v, int offset, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            char ch = v.charAt(i + offset);
            if (ch == '<') {
                this.os.write(38);
                this.os.write(35);
                this.os.write(54);
                this.os.write(48);
                this.os.write(59);
                continue;
            }
            if (ch == '&') {
                this.os.write(38);
                this.os.write(35);
                this.os.write(51);
                this.os.write(56);
                this.os.write(59);
                continue;
            }
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

    public void printString(char[] v, int offset, int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            char ch = v[i + offset];
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

    public void printDate(Calendar calendar) throws IOException {
        int year = calendar.get(1);
        this.os.write((char)(48 + year / 1000 % 10));
        this.os.write((char)(48 + year / 100 % 10));
        this.os.write((char)(48 + year / 10 % 10));
        this.os.write((char)(48 + year % 10));
        int month = calendar.get(2) + 1;
        this.os.write((char)(48 + month / 10 % 10));
        this.os.write((char)(48 + month % 10));
        int day = calendar.get(5);
        this.os.write((char)(48 + day / 10 % 10));
        this.os.write((char)(48 + day % 10));
        this.os.write(84);
        int hour = calendar.get(11);
        this.os.write((char)(48 + hour / 10 % 10));
        this.os.write((char)(48 + hour % 10));
        int minute = calendar.get(12);
        this.os.write((char)(48 + minute / 10 % 10));
        this.os.write((char)(48 + minute % 10));
        int second = calendar.get(13);
        this.os.write((char)(48 + second / 10 % 10));
        this.os.write((char)(48 + second % 10));
        int ms = calendar.get(14);
        this.os.write(46);
        this.os.write((char)(48 + ms / 100 % 10));
        this.os.write((char)(48 + ms / 10 % 10));
        this.os.write((char)(48 + ms % 10));
        this.os.write(90);
    }

    protected void print(char v) throws IOException {
        this.os.write(v);
    }

    protected void print(int v) throws IOException {
        this.print(String.valueOf(v));
    }

    protected void print(long v) throws IOException {
        this.print(String.valueOf(v));
    }

    protected void print(double v) throws IOException {
        this.print(String.valueOf(v));
    }

    protected void print(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char ch = s.charAt(i);
            this.os.write(ch);
        }
    }
}

