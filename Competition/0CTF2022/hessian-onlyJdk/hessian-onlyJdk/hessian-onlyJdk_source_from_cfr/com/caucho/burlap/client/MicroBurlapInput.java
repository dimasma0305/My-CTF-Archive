/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.client;

import com.caucho.burlap.client.BurlapProtocolException;
import com.caucho.burlap.client.BurlapRemote;
import com.caucho.burlap.client.BurlapServiceException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

public class MicroBurlapInput {
    private static int[] base64Decode;
    private InputStream is;
    protected int peek;
    protected boolean peekTag;
    protected Date date;
    protected Calendar utcCalendar;
    private Calendar localCalendar;
    protected Vector refs;
    protected String method;
    protected StringBuffer sbuf = new StringBuffer();
    protected StringBuffer entity = new StringBuffer();

    public MicroBurlapInput(InputStream is) {
        this.init(is);
    }

    public MicroBurlapInput() {
    }

    public String getMethod() {
        return this.method;
    }

    public void init(InputStream is) {
        this.is = is;
        this.refs = null;
    }

    public void startCall() throws IOException {
        this.expectStartTag("burlap:call");
        this.expectStartTag("method");
        this.method = this.parseString();
        this.expectEndTag("method");
        this.refs = null;
    }

    public void completeCall() throws IOException {
        this.expectEndTag("burlap:call");
    }

    public Object readReply(Class expectedClass) throws Exception {
        if (this.startReply()) {
            Object value = this.readObject(expectedClass);
            this.completeReply();
            return value;
        }
        Hashtable fault = this.readFault();
        Object detail = fault.get("detail");
        if (detail instanceof Exception) {
            throw (Exception)detail;
        }
        String code = (String)fault.get("code");
        String message = (String)fault.get("message");
        throw new BurlapServiceException(message, code, detail);
    }

    public boolean startReply() throws IOException {
        this.refs = null;
        this.expectStartTag("burlap:reply");
        if (!this.parseTag()) {
            throw new BurlapProtocolException("expected <value>");
        }
        String tag = this.sbuf.toString();
        if (tag.equals("fault")) {
            this.peekTag = true;
            return false;
        }
        this.peekTag = true;
        return true;
    }

    public void completeReply() throws IOException {
        this.expectEndTag("burlap:reply");
    }

    public boolean readBoolean() throws IOException {
        this.expectStartTag("boolean");
        int value = this.parseInt();
        this.expectEndTag("boolean");
        return value != 0;
    }

    public int readInt() throws IOException {
        this.expectStartTag("int");
        int value = this.parseInt();
        this.expectEndTag("int");
        return value;
    }

    public long readLong() throws IOException {
        this.expectStartTag("long");
        long value = this.parseLong();
        this.expectEndTag("long");
        return value;
    }

    public long readUTCDate() throws IOException {
        this.expectStartTag("date");
        if (this.utcCalendar == null) {
            this.utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        }
        long value = this.parseDate(this.utcCalendar);
        this.expectEndTag("date");
        return value;
    }

    public long readLocalDate() throws IOException {
        this.expectStartTag("date");
        if (this.localCalendar == null) {
            this.localCalendar = Calendar.getInstance();
        }
        long value = this.parseDate(this.localCalendar);
        this.expectEndTag("date");
        return value;
    }

    public BurlapRemote readRemote() throws IOException {
        this.expectStartTag("remote");
        String type = this.readType();
        String url = this.readString();
        this.expectEndTag("remote");
        return new BurlapRemote(type, url);
    }

    public String readString() throws IOException {
        if (!this.parseTag()) {
            throw new BurlapProtocolException("expected <string>");
        }
        String tag = this.sbuf.toString();
        if (tag.equals("null")) {
            this.expectEndTag("null");
            return null;
        }
        if (tag.equals("string")) {
            this.sbuf.setLength(0);
            this.parseString(this.sbuf);
            String value = this.sbuf.toString();
            this.expectEndTag("string");
            return value;
        }
        throw this.expectBeginTag("string", tag);
    }

    public byte[] readBytes() throws IOException {
        if (!this.parseTag()) {
            throw new BurlapProtocolException("expected <base64>");
        }
        String tag = this.sbuf.toString();
        if (tag.equals("null")) {
            this.expectEndTag("null");
            return null;
        }
        if (tag.equals("base64")) {
            this.sbuf.setLength(0);
            byte[] value = this.parseBytes();
            this.expectEndTag("base64");
            return value;
        }
        throw this.expectBeginTag("base64", tag);
    }

    public Object readObject(Class expectedClass) throws IOException {
        if (!this.parseTag()) {
            throw new BurlapProtocolException("expected <tag>");
        }
        String tag = this.sbuf.toString();
        if (tag.equals("null")) {
            this.expectEndTag("null");
            return null;
        }
        if (tag.equals("boolean")) {
            int value = this.parseInt();
            this.expectEndTag("boolean");
            return new Boolean(value != 0);
        }
        if (tag.equals("int")) {
            int value = this.parseInt();
            this.expectEndTag("int");
            return new Integer(value);
        }
        if (tag.equals("long")) {
            long value = this.parseLong();
            this.expectEndTag("long");
            return new Long(value);
        }
        if (tag.equals("string")) {
            this.sbuf.setLength(0);
            this.parseString(this.sbuf);
            String value = this.sbuf.toString();
            this.expectEndTag("string");
            return value;
        }
        if (tag.equals("xml")) {
            this.sbuf.setLength(0);
            this.parseString(this.sbuf);
            String value = this.sbuf.toString();
            this.expectEndTag("xml");
            return value;
        }
        if (tag.equals("date")) {
            if (this.utcCalendar == null) {
                this.utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            }
            long value = this.parseDate(this.utcCalendar);
            this.expectEndTag("date");
            return new Date(value);
        }
        if (tag.equals("map")) {
            String type = this.readType();
            return this.readMap(expectedClass, type);
        }
        if (tag.equals("list")) {
            String type = this.readType();
            int length = this.readLength();
            return this.readList(expectedClass, type, length);
        }
        if (tag.equals("ref")) {
            int value = this.parseInt();
            this.expectEndTag("ref");
            return this.refs.elementAt(value);
        }
        if (tag.equals("remote")) {
            String type = this.readType();
            String url = this.readString();
            this.expectEndTag("remote");
            return this.resolveRemote(type, url);
        }
        return this.readExtensionObject(expectedClass, tag);
    }

    public String readType() throws IOException {
        if (!this.parseTag()) {
            throw new BurlapProtocolException("expected <type>");
        }
        String tag = this.sbuf.toString();
        if (!tag.equals("type")) {
            throw new BurlapProtocolException("expected <type>");
        }
        this.sbuf.setLength(0);
        this.parseString(this.sbuf);
        String value = this.sbuf.toString();
        this.expectEndTag("type");
        return value;
    }

    public int readLength() throws IOException {
        int ch;
        this.expectStartTag("length");
        this.peek = ch = this.skipWhitespace();
        if (ch == 60) {
            this.expectEndTag("length");
            return -1;
        }
        int value = this.parseInt();
        this.expectEndTag("length");
        return value;
    }

    public Object resolveRemote(String type, String url) throws IOException {
        return new BurlapRemote(type, url);
    }

    public Hashtable readFault() throws IOException {
        this.expectStartTag("fault");
        Hashtable<Object, Object> map = new Hashtable<Object, Object>();
        while (this.parseTag()) {
            this.peekTag = true;
            Object key = this.readObject(null);
            Object value = this.readObject(null);
            if (key == null || value == null) continue;
            map.put(key, value);
        }
        if (!this.sbuf.toString().equals("fault")) {
            throw new BurlapProtocolException("expected </fault>");
        }
        return map;
    }

    public Object readMap(Class expectedClass, String type) throws IOException {
        Hashtable<Object, Object> map = new Hashtable<Object, Object>();
        if (this.refs == null) {
            this.refs = new Vector();
        }
        this.refs.addElement(map);
        while (this.parseTag()) {
            this.peekTag = true;
            Object key = this.readObject(null);
            Object value = this.readObject(null);
            map.put(key, value);
        }
        if (!this.sbuf.toString().equals("map")) {
            throw new BurlapProtocolException("expected </map>");
        }
        return map;
    }

    protected Object readExtensionObject(Class expectedClass, String tag) throws IOException {
        throw new BurlapProtocolException("unknown object tag <" + tag + ">");
    }

    public Object readList(Class expectedClass, String type, int length) throws IOException {
        Vector<Object> list = new Vector<Object>();
        if (this.refs == null) {
            this.refs = new Vector();
        }
        this.refs.addElement(list);
        while (this.parseTag()) {
            this.peekTag = true;
            Object value = this.readObject(null);
            list.addElement(value);
        }
        if (!this.sbuf.toString().equals("list")) {
            throw new BurlapProtocolException("expected </list>");
        }
        return list;
    }

    protected int parseInt() throws IOException {
        int sign = 1;
        int value = 0;
        int ch = this.skipWhitespace();
        if (ch == 43) {
            ch = this.read();
        } else if (ch == 45) {
            sign = -1;
            ch = this.read();
        }
        while (ch >= 48 && ch <= 57) {
            value = 10 * value + ch - 48;
            ch = this.read();
        }
        this.peek = ch;
        return sign * value;
    }

    protected long parseLong() throws IOException {
        long sign = 1L;
        long value = 0L;
        int ch = this.skipWhitespace();
        if (ch == 43) {
            ch = this.read();
        } else if (ch == 45) {
            sign = -1L;
            ch = this.read();
        }
        while (ch >= 48 && ch <= 57) {
            value = 10L * value + (long)ch - 48L;
            ch = this.read();
        }
        this.peek = ch;
        return sign * value;
    }

    protected long parseDate(Calendar calendar) throws IOException {
        int ch = this.skipWhitespace();
        int year = 0;
        for (int i = 0; i < 4; ++i) {
            if (ch < 48 || ch > 57) {
                throw this.expectedChar("year", ch);
            }
            year = 10 * year + ch - 48;
            ch = this.read();
        }
        int month = 0;
        for (int i = 0; i < 2; ++i) {
            if (ch < 48 || ch > 57) {
                throw this.expectedChar("month", ch);
            }
            month = 10 * month + ch - 48;
            ch = this.read();
        }
        int day = 0;
        for (int i = 0; i < 2; ++i) {
            if (ch < 48 || ch > 57) {
                throw this.expectedChar("day", ch);
            }
            day = 10 * day + ch - 48;
            ch = this.read();
        }
        if (ch != 84) {
            throw this.expectedChar("`T'", ch);
        }
        ch = this.read();
        int hour = 0;
        for (int i = 0; i < 2; ++i) {
            if (ch < 48 || ch > 57) {
                throw this.expectedChar("hour", ch);
            }
            hour = 10 * hour + ch - 48;
            ch = this.read();
        }
        int minute = 0;
        for (int i = 0; i < 2; ++i) {
            if (ch < 48 || ch > 57) {
                throw this.expectedChar("minute", ch);
            }
            minute = 10 * minute + ch - 48;
            ch = this.read();
        }
        int second = 0;
        for (int i = 0; i < 2; ++i) {
            if (ch < 48 || ch > 57) {
                throw this.expectedChar("second", ch);
            }
            second = 10 * second + ch - 48;
            ch = this.read();
        }
        while (ch > 0 && ch != 60) {
            ch = this.read();
        }
        this.peek = ch;
        calendar.set(1, year);
        calendar.set(2, month - 1);
        calendar.set(5, day);
        calendar.set(11, hour);
        calendar.set(12, minute);
        calendar.set(13, second);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }

    protected String parseString() throws IOException {
        StringBuffer sbuf = new StringBuffer();
        return this.parseString(sbuf).toString();
    }

    protected StringBuffer parseString(StringBuffer sbuf) throws IOException {
        int ch = this.read();
        while (ch >= 0 && ch != 60) {
            int ch1;
            if (ch == 38) {
                ch = this.read();
                if (ch == 35) {
                    ch = this.read();
                    if (ch >= 48 && ch <= 57) {
                        int v = 0;
                        while (ch >= 48 && ch <= 57) {
                            v = 10 * v + ch - 48;
                            ch = this.read();
                        }
                        sbuf.append((char)v);
                    }
                } else {
                    StringBuffer entityBuffer = new StringBuffer();
                    while (ch >= 97 && ch <= 122) {
                        entityBuffer.append((char)ch);
                        ch = this.read();
                    }
                    String entity = entityBuffer.toString();
                    if (entity.equals("amp")) {
                        sbuf.append('&');
                    } else if (entity.equals("apos")) {
                        sbuf.append('\'');
                    } else if (entity.equals("quot")) {
                        sbuf.append('\"');
                    } else if (entity.equals("lt")) {
                        sbuf.append('<');
                    } else if (entity.equals("gt")) {
                        sbuf.append('>');
                    } else {
                        throw new BurlapProtocolException("unknown XML entity &" + entity + "; at `" + (char)ch + "'");
                    }
                }
                if (ch != 59) {
                    throw this.expectedChar("';'", ch);
                }
            } else if (ch < 128) {
                sbuf.append((char)ch);
            } else if ((ch & 0xE0) == 192) {
                ch1 = this.read();
                int v = ((ch & 0x1F) << 6) + (ch1 & 0x3F);
                sbuf.append((char)v);
            } else if ((ch & 0xF0) == 224) {
                ch1 = this.read();
                int ch2 = this.read();
                int v = ((ch & 0xF) << 12) + ((ch1 & 0x3F) << 6) + (ch2 & 0x3F);
                sbuf.append((char)v);
            } else {
                throw new BurlapProtocolException("bad utf-8 encoding");
            }
            ch = this.read();
        }
        this.peek = ch;
        return sbuf;
    }

    protected byte[] parseBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.parseBytes(bos);
        return bos.toByteArray();
    }

    protected ByteArrayOutputStream parseBytes(ByteArrayOutputStream bos) throws IOException {
        int ch = this.read();
        while (ch >= 0 && ch != 60) {
            int chunk;
            int b1 = ch;
            int b2 = this.read();
            int b3 = this.read();
            int b4 = this.read();
            if (b4 != 61) {
                chunk = (base64Decode[b1] << 18) + (base64Decode[b2] << 12) + (base64Decode[b3] << 6) + base64Decode[b4];
                bos.write(chunk >> 16);
                bos.write(chunk >> 8);
                bos.write(chunk);
            } else if (b3 != 61) {
                chunk = (base64Decode[b1] << 12) + (base64Decode[b2] << 6) + base64Decode[b3];
                bos.write(chunk >> 8);
                bos.write(chunk);
            } else {
                chunk = (base64Decode[b1] << 6) + base64Decode[b2];
                bos.write(chunk);
            }
            ch = this.read();
        }
        if (ch == 60) {
            this.peek = ch;
        }
        return bos;
    }

    protected void expectStartTag(String tag) throws IOException {
        if (!this.parseTag()) {
            throw new BurlapProtocolException("expected <" + tag + ">");
        }
        if (!this.sbuf.toString().equals(tag)) {
            throw new BurlapProtocolException("expected <" + tag + "> at <" + this.sbuf + ">");
        }
    }

    protected void expectEndTag(String tag) throws IOException {
        if (this.parseTag()) {
            throw new BurlapProtocolException("expected </" + tag + ">");
        }
        if (!this.sbuf.toString().equals(tag)) {
            throw new BurlapProtocolException("expected </" + tag + "> at </" + this.sbuf + ">");
        }
    }

    protected boolean parseTag() throws IOException {
        if (this.peekTag) {
            this.peekTag = false;
            return true;
        }
        int ch = this.skipWhitespace();
        boolean isStartTag = true;
        if (ch != 60) {
            throw this.expectedChar("'<'", ch);
        }
        ch = this.read();
        if (ch == 47) {
            isStartTag = false;
            ch = this.is.read();
        }
        if (!this.isTagChar(ch)) {
            throw this.expectedChar("tag", ch);
        }
        this.sbuf.setLength(0);
        while (this.isTagChar(ch)) {
            this.sbuf.append((char)ch);
            ch = this.read();
        }
        if (ch != 62) {
            throw this.expectedChar("'>'", ch);
        }
        return isStartTag;
    }

    protected IOException expectedChar(String expect, int actualChar) {
        return new BurlapProtocolException("expected " + expect + " at " + (char)actualChar + "'");
    }

    protected IOException expectBeginTag(String expect, String tag) {
        return new BurlapProtocolException("expected <" + expect + "> at <" + tag + ">");
    }

    private boolean isTagChar(int ch) {
        return ch >= 97 && ch <= 122 || ch >= 65 && ch <= 90 || ch >= 48 && ch <= 57 || ch == 58 || ch == 45;
    }

    protected int skipWhitespace() throws IOException {
        int ch = this.read();
        while (ch == 32 || ch == 9 || ch == 10 || ch == 13) {
            ch = this.read();
        }
        return ch;
    }

    protected boolean isWhitespace(int ch) throws IOException {
        return ch == 32 || ch == 9 || ch == 10 || ch == 13;
    }

    protected int read() throws IOException {
        if (this.peek > 0) {
            int value = this.peek;
            this.peek = 0;
            return value;
        }
        return this.is.read();
    }

    static {
        int i;
        base64Decode = new int[256];
        for (i = 65; i <= 90; ++i) {
            MicroBurlapInput.base64Decode[i] = i - 65;
        }
        for (i = 97; i <= 122; ++i) {
            MicroBurlapInput.base64Decode[i] = i - 97 + 26;
        }
        for (i = 48; i <= 57; ++i) {
            MicroBurlapInput.base64Decode[i] = i - 48 + 52;
        }
        MicroBurlapInput.base64Decode[43] = 62;
        MicroBurlapInput.base64Decode[47] = 63;
    }
}

