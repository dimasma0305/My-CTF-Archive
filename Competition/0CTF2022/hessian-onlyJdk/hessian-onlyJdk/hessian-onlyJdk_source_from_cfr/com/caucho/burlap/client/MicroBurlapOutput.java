/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

public class MicroBurlapOutput {
    private OutputStream os;
    private Date date;
    private Calendar utcCalendar;
    private Calendar localCalendar;

    public MicroBurlapOutput(OutputStream os) {
        this.init(os);
    }

    public MicroBurlapOutput() {
    }

    public void init(OutputStream os) {
        this.os = os;
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

    public void completeCall() throws IOException {
        this.print("</burlap:call>");
    }

    public void writeBoolean(boolean value) throws IOException {
        this.print("<boolean>");
        this.printInt(value ? 1 : 0);
        this.print("</boolean>");
    }

    public void writeInt(int value) throws IOException {
        this.print("<int>");
        this.printInt(value);
        this.print("</int>");
    }

    public void writeLong(long value) throws IOException {
        this.print("<long>");
        this.printLong(value);
        this.print("</long>");
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

    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            this.print("<null></null>");
        } else {
            this.print("<base64>");
            this.printBytes(buffer, offset, length);
            this.print("</base64>");
        }
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

    public void writeLocalDate(long time) throws IOException {
        this.print("<date>");
        if (this.localCalendar == null) {
            this.localCalendar = Calendar.getInstance();
            this.date = new Date();
        }
        this.date.setTime(time);
        this.localCalendar.setTime(this.date);
        this.printDate(this.localCalendar);
        this.print("</date>");
    }

    public void writeRef(int value) throws IOException {
        this.print("<ref>");
        this.printInt(value);
        this.print("</ref>");
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
        this.print("<list><type>");
        if (type != null) {
            this.print(type);
        }
        this.print("</type><length>");
        this.printInt(length);
        this.print("</length>");
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
        if (type != null) {
            this.print(type);
        }
        this.print("</type><string>");
        this.print(url);
        this.print("</string></remote>");
    }

    public void printInt(int v) throws IOException {
        this.print(String.valueOf(v));
    }

    public void printLong(long v) throws IOException {
        this.print(String.valueOf(v));
    }

    public void printString(String v) throws IOException {
        int len = v.length();
        block5: for (int i = 0; i < len; ++i) {
            char ch = v.charAt(i);
            switch (ch) {
                case '<': {
                    this.print("&lt;");
                    continue block5;
                }
                case '&': {
                    this.print("&amp;");
                    continue block5;
                }
                case '\r': {
                    this.print("&#13;");
                    continue block5;
                }
                default: {
                    if (ch < '\u0080') {
                        this.os.write(ch);
                        continue block5;
                    }
                    if (ch < '\u0800') {
                        this.os.write(192 + (ch >> 6 & 0x1F));
                        this.os.write(128 + (ch & 0x3F));
                        continue block5;
                    }
                    this.os.write(224 + (ch >> 12 & 0xF));
                    this.os.write(128 + (ch >> 6 & 0x3F));
                    this.os.write(128 + (ch & 0x3F));
                }
            }
        }
    }

    public void printBytes(byte[] data, int offset, int length) throws IOException {
        int chunk;
        while (length >= 3) {
            chunk = ((data[offset] & 0xFF) << 16) + ((data[offset + 1] & 0xFF) << 8) + (data[offset + 2] & 0xFF);
            this.os.write(MicroBurlapOutput.base64encode(chunk >> 18));
            this.os.write(MicroBurlapOutput.base64encode(chunk >> 12));
            this.os.write(MicroBurlapOutput.base64encode(chunk >> 6));
            this.os.write(MicroBurlapOutput.base64encode(chunk));
            offset += 3;
            length -= 3;
        }
        if (length == 2) {
            chunk = ((data[offset] & 0xFF) << 8) + (data[offset + 1] & 0xFF);
            this.os.write(MicroBurlapOutput.base64encode(chunk >> 12));
            this.os.write(MicroBurlapOutput.base64encode(chunk >> 6));
            this.os.write(MicroBurlapOutput.base64encode(chunk));
            this.os.write(61);
        } else if (length == 1) {
            chunk = data[offset] & 0xFF;
            this.os.write(MicroBurlapOutput.base64encode(chunk >> 6));
            this.os.write(MicroBurlapOutput.base64encode(chunk));
            this.os.write(61);
            this.os.write(61);
        }
    }

    public static char base64encode(int d) {
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
        this.os.write(90);
    }

    public void print(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char ch = s.charAt(i);
            this.os.write(ch);
        }
    }
}

