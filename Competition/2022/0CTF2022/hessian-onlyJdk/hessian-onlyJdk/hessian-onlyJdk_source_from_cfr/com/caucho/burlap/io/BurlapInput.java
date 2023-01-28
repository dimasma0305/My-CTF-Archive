/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.burlap.io;

import com.caucho.burlap.io.AbstractBurlapInput;
import com.caucho.burlap.io.BurlapProtocolException;
import com.caucho.burlap.io.BurlapRemote;
import com.caucho.burlap.io.BurlapServiceException;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.SerializerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import org.w3c.dom.Node;

public class BurlapInput
extends AbstractBurlapInput {
    private static int[] base64Decode;
    public static final int TAG_EOF = -1;
    public static final int TAG_NULL = 0;
    public static final int TAG_BOOLEAN = 1;
    public static final int TAG_INT = 2;
    public static final int TAG_LONG = 3;
    public static final int TAG_DOUBLE = 4;
    public static final int TAG_DATE = 5;
    public static final int TAG_STRING = 6;
    public static final int TAG_XML = 7;
    public static final int TAG_BASE64 = 8;
    public static final int TAG_MAP = 9;
    public static final int TAG_LIST = 10;
    public static final int TAG_TYPE = 11;
    public static final int TAG_LENGTH = 12;
    public static final int TAG_REF = 13;
    public static final int TAG_REMOTE = 14;
    public static final int TAG_CALL = 15;
    public static final int TAG_REPLY = 16;
    public static final int TAG_FAULT = 17;
    public static final int TAG_METHOD = 18;
    public static final int TAG_HEADER = 19;
    public static final int TAG_NULL_END = 100;
    public static final int TAG_BOOLEAN_END = 101;
    public static final int TAG_INT_END = 102;
    public static final int TAG_LONG_END = 103;
    public static final int TAG_DOUBLE_END = 104;
    public static final int TAG_DATE_END = 105;
    public static final int TAG_STRING_END = 106;
    public static final int TAG_XML_END = 107;
    public static final int TAG_BASE64_END = 108;
    public static final int TAG_MAP_END = 109;
    public static final int TAG_LIST_END = 110;
    public static final int TAG_TYPE_END = 111;
    public static final int TAG_LENGTH_END = 112;
    public static final int TAG_REF_END = 113;
    public static final int TAG_REMOTE_END = 114;
    public static final int TAG_CALL_END = 115;
    public static final int TAG_REPLY_END = 116;
    public static final int TAG_FAULT_END = 117;
    public static final int TAG_METHOD_END = 118;
    public static final int TAG_HEADER_END = 119;
    private static HashMap _tagMap;
    private static Field _detailMessageField;
    protected SerializerFactory _serializerFactory;
    protected ArrayList _refs;
    private InputStream _is;
    protected int _peek = -1;
    private String _method;
    private int _peekTag;
    private Throwable _replyFault;
    protected StringBuffer _sbuf = new StringBuffer();
    protected StringBuffer _entityBuffer = new StringBuffer();
    protected Calendar _utcCalendar;
    protected Calendar _localCalendar;

    public BurlapInput() {
    }

    public BurlapInput(InputStream is) {
        this.init(is);
    }

    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    public SerializerFactory getSerializerFactory() {
        return this._serializerFactory;
    }

    public void init(InputStream is) {
        this._is = is;
        this._method = null;
        this._peek = -1;
        this._peekTag = -1;
        this._refs = null;
        this._replyFault = null;
        if (this._serializerFactory == null) {
            this._serializerFactory = new SerializerFactory();
        }
    }

    public String getMethod() {
        return this._method;
    }

    public Throwable getReplyFault() {
        return this._replyFault;
    }

    public void startCall() throws IOException {
        this.readCall();
        while (this.readHeader() != null) {
            this.readObject();
        }
        this.readMethod();
    }

    public int readCall() throws IOException {
        this.expectTag(15);
        int major = 1;
        int minor = 0;
        return (major << 16) + minor;
    }

    public String readMethod() throws IOException {
        this.expectTag(18);
        this._method = this.parseString();
        this.expectTag(118);
        return this._method;
    }

    public void completeCall() throws IOException {
        this.expectTag(115);
    }

    public Object readReply(Class expectedClass) throws Throwable {
        this.expectTag(16);
        int tag = this.parseTag();
        if (tag == 17) {
            throw this.prepareFault();
        }
        this._peekTag = tag;
        Object value = this.readObject(expectedClass);
        this.expectTag(116);
        return value;
    }

    public void startReply() throws Throwable {
        this.expectTag(16);
        int tag = this.parseTag();
        if (tag == 17) {
            throw this.prepareFault();
        }
        this._peekTag = tag;
    }

    private Throwable prepareFault() throws IOException {
        HashMap fault = this.readFault();
        Object detail = fault.get("detail");
        String message = (String)fault.get("message");
        if (detail instanceof Throwable) {
            this._replyFault = (Throwable)detail;
            if (message != null && _detailMessageField != null) {
                try {
                    _detailMessageField.set(this._replyFault, message);
                }
                catch (Throwable e) {
                    // empty catch block
                }
            }
            return this._replyFault;
        }
        String code = (String)fault.get("code");
        this._replyFault = new BurlapServiceException(message, code, detail);
        return this._replyFault;
    }

    public void completeReply() throws IOException {
        this.expectTag(116);
    }

    public String readHeader() throws IOException {
        int tag = this.parseTag();
        if (tag == 19) {
            this._sbuf.setLength(0);
            String value = this.parseString(this._sbuf).toString();
            this.expectTag(119);
            return value;
        }
        this._peekTag = tag;
        return null;
    }

    public void readNull() throws IOException {
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                this.expectTag(100);
                return;
            }
        }
        throw this.expectedTag("null", tag);
    }

    public boolean readBoolean() throws IOException {
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                boolean value = false;
                this.expectTag(100);
                return value;
            }
            case 1: {
                boolean value = this.parseInt() != 0;
                this.expectTag(101);
                return value;
            }
            case 2: {
                boolean value = this.parseInt() != 0;
                this.expectTag(102);
                return value;
            }
            case 3: {
                boolean value = this.parseLong() != 0L;
                this.expectTag(103);
                return value;
            }
            case 4: {
                boolean value = this.parseDouble() != 0.0;
                this.expectTag(104);
                return value;
            }
        }
        throw this.expectedTag("boolean", tag);
    }

    public byte readByte() throws IOException {
        return (byte)this.readInt();
    }

    public short readShort() throws IOException {
        return (short)this.readInt();
    }

    public int readInt() throws IOException {
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                int value = 0;
                this.expectTag(100);
                return value;
            }
            case 1: {
                int value = this.parseInt();
                this.expectTag(101);
                return value;
            }
            case 2: {
                int value = this.parseInt();
                this.expectTag(102);
                return value;
            }
            case 3: {
                int value = (int)this.parseLong();
                this.expectTag(103);
                return value;
            }
            case 4: {
                int value = (int)this.parseDouble();
                this.expectTag(104);
                return value;
            }
        }
        throw this.expectedTag("int", tag);
    }

    public long readLong() throws IOException {
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                long value = 0L;
                this.expectTag(100);
                return value;
            }
            case 1: {
                long value = this.parseInt();
                this.expectTag(101);
                return value;
            }
            case 2: {
                long value = this.parseInt();
                this.expectTag(102);
                return value;
            }
            case 3: {
                long value = this.parseLong();
                this.expectTag(103);
                return value;
            }
            case 4: {
                long value = (long)this.parseDouble();
                this.expectTag(104);
                return value;
            }
        }
        throw this.expectedTag("long", tag);
    }

    public float readFloat() throws IOException {
        return (float)this.readDouble();
    }

    public double readDouble() throws IOException {
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                double value = 0.0;
                this.expectTag(100);
                return value;
            }
            case 1: {
                double value = this.parseInt();
                this.expectTag(101);
                return value;
            }
            case 2: {
                double value = this.parseInt();
                this.expectTag(102);
                return value;
            }
            case 3: {
                double value = this.parseLong();
                this.expectTag(103);
                return value;
            }
            case 4: {
                double value = this.parseDouble();
                this.expectTag(104);
                return value;
            }
        }
        throw this.expectedTag("double", tag);
    }

    public long readUTCDate() throws IOException {
        int tag = this.parseTag();
        if (tag != 5) {
            throw this.error("expected date");
        }
        if (this._utcCalendar == null) {
            this._utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        }
        long value = this.parseDate(this._utcCalendar);
        this.expectTag(105);
        return value;
    }

    public long readLocalDate() throws IOException {
        int tag = this.parseTag();
        if (tag != 5) {
            throw this.error("expected date");
        }
        if (this._localCalendar == null) {
            this._localCalendar = Calendar.getInstance();
        }
        long value = this.parseDate(this._localCalendar);
        this.expectTag(105);
        return value;
    }

    public String readString() throws IOException {
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                this.expectTag(100);
                return null;
            }
            case 6: {
                this._sbuf.setLength(0);
                String value = this.parseString(this._sbuf).toString();
                this.expectTag(106);
                return value;
            }
            case 7: {
                this._sbuf.setLength(0);
                String value = this.parseString(this._sbuf).toString();
                this.expectTag(107);
                return value;
            }
        }
        throw this.expectedTag("string", tag);
    }

    public Node readNode() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 83: 
            case 88: 
            case 115: 
            case 120: {
                throw this.error("can't cope");
            }
        }
        throw this.expectedTag("string", tag);
    }

    public byte[] readBytes() throws IOException {
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                this.expectTag(100);
                return null;
            }
            case 8: {
                byte[] data = this.parseBytes();
                this.expectTag(108);
                return data;
            }
        }
        throw this.expectedTag("bytes", tag);
    }

    public int readLength() throws IOException {
        int tag = this.parseTag();
        if (tag != 12) {
            this._peekTag = tag;
            return -1;
        }
        int value = this.parseInt();
        this.expectTag(112);
        return value;
    }

    private HashMap readFault() throws IOException {
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        int code = this.parseTag();
        while (code >= 0 && code != 117) {
            this._peekTag = code;
            Object key = this.readObject();
            Object value = this.readObject();
            if (key != null && value != null) {
                map.put(key, value);
            }
            code = this.parseTag();
        }
        if (code != 117) {
            throw this.expectedTag("fault", code);
        }
        return map;
    }

    public Object readObject(Class cl) throws IOException {
        if (cl == null || cl.equals(Object.class)) {
            return this.readObject();
        }
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                this.expectTag(100);
                return null;
            }
            case 9: {
                String type = this.readType();
                Deserializer reader = this._serializerFactory.getObjectDeserializer(type, cl);
                return reader.readMap(this);
            }
            case 10: {
                String type = this.readType();
                int length = this.readLength();
                Deserializer reader = this._serializerFactory.getObjectDeserializer(type, cl);
                return reader.readList(this, length);
            }
            case 13: {
                int ref = this.parseInt();
                this.expectTag(113);
                return this._refs.get(ref);
            }
            case 14: {
                String type = this.readType();
                String url = this.readString();
                this.expectTag(114);
                Object remote = this.resolveRemote(type, url);
                return remote;
            }
        }
        this._peekTag = tag;
        Object value = this._serializerFactory.getDeserializer(cl).readObject(this);
        return value;
    }

    public Object readObject() throws IOException {
        int tag = this.parseTag();
        switch (tag) {
            case 0: {
                this.expectTag(100);
                return null;
            }
            case 1: {
                int value = this.parseInt();
                this.expectTag(101);
                return new Boolean(value != 0);
            }
            case 2: {
                int value = this.parseInt();
                this.expectTag(102);
                return new Integer(value);
            }
            case 3: {
                long value = this.parseLong();
                this.expectTag(103);
                return new Long(value);
            }
            case 4: {
                double value = this.parseDouble();
                this.expectTag(104);
                return new Double(value);
            }
            case 5: {
                long value = this.parseDate();
                this.expectTag(105);
                return new Date(value);
            }
            case 7: {
                return this.parseXML();
            }
            case 6: {
                this._sbuf.setLength(0);
                String value = this.parseString(this._sbuf).toString();
                this.expectTag(106);
                return value;
            }
            case 8: {
                byte[] data = this.parseBytes();
                this.expectTag(108);
                return data;
            }
            case 10: {
                String type = this.readType();
                int length = this.readLength();
                return this._serializerFactory.readList(this, length, type);
            }
            case 9: {
                String type = this.readType();
                Deserializer deserializer = this._serializerFactory.getObjectDeserializer(type);
                return deserializer.readMap(this);
            }
            case 13: {
                int ref = this.parseInt();
                this.expectTag(113);
                return this._refs.get(ref);
            }
            case 14: {
                String type = this.readType();
                String url = this.readString();
                this.expectTag(114);
                return this.resolveRemote(type, url);
            }
        }
        throw this.error("unknown code:" + BurlapInput.tagName(tag));
    }

    public Object readRemote() throws IOException {
        String type = this.readType();
        String url = this.readString();
        return this.resolveRemote(type, url);
    }

    public Object readRef() throws IOException {
        return this._refs.get(this.parseInt());
    }

    public int readListStart() throws IOException {
        return this.parseTag();
    }

    public int readMapStart() throws IOException {
        return this.parseTag();
    }

    public boolean isEnd() throws IOException {
        int code;
        this._peekTag = code = this.parseTag();
        return code < 0 || code >= 100;
    }

    public void readEnd() throws IOException {
        int code = this.parseTag();
        if (code < 100) {
            throw this.error("unknown code:" + (char)code);
        }
    }

    public void readMapEnd() throws IOException {
        this.expectTag(109);
    }

    public void readListEnd() throws IOException {
        this.expectTag(110);
    }

    public int addRef(Object ref) {
        if (this._refs == null) {
            this._refs = new ArrayList();
        }
        this._refs.add(ref);
        return this._refs.size() - 1;
    }

    public void setRef(int i, Object ref) {
        this._refs.set(i, ref);
    }

    public Object resolveRemote(String type, String url) throws IOException {
        HessianRemoteResolver resolver = this.getRemoteResolver();
        if (resolver != null) {
            return resolver.lookup(type, url);
        }
        return new BurlapRemote(type, url);
    }

    public String readType() throws IOException {
        int ch;
        int code = this.parseTag();
        if (code != 11) {
            this._peekTag = code;
            return "";
        }
        this._sbuf.setLength(0);
        while ((ch = this.readChar()) >= 0) {
            this._sbuf.append((char)ch);
        }
        String type = this._sbuf.toString();
        this.expectTag(111);
        return type;
    }

    private int parseInt() throws IOException {
        int sign = 1;
        int ch = this.read();
        if (ch == 45) {
            sign = -1;
            ch = this.read();
        }
        int value = 0;
        while (ch >= 48 && ch <= 57) {
            value = 10 * value + ch - 48;
            ch = this.read();
        }
        this._peek = ch;
        return sign * value;
    }

    private long parseLong() throws IOException {
        int sign = 1;
        int ch = this.read();
        if (ch == 45) {
            sign = -1;
            ch = this.read();
        }
        long value = 0L;
        while (ch >= 48 && ch <= 57) {
            value = 10L * value + (long)ch - 48L;
            ch = this.read();
        }
        this._peek = ch;
        return (long)sign * value;
    }

    private double parseDouble() throws IOException {
        int ch = this.skipWhitespace();
        this._sbuf.setLength(0);
        while (!this.isWhitespace(ch) && ch != 60) {
            this._sbuf.append((char)ch);
            ch = this.read();
        }
        this._peek = ch;
        return new Double(this._sbuf.toString());
    }

    protected long parseDate() throws IOException {
        if (this._utcCalendar == null) {
            this._utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        }
        return this.parseDate(this._utcCalendar);
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
        int ms = 0;
        if (ch == 46) {
            ch = this.read();
            while (ch >= 48 && ch <= 57) {
                ms = 10 * ms + ch - 48;
                ch = this.read();
            }
        }
        while (ch > 0 && ch != 60) {
            ch = this.read();
        }
        this._peek = ch;
        calendar.set(1, year);
        calendar.set(2, month - 1);
        calendar.set(5, day);
        calendar.set(11, hour);
        calendar.set(12, minute);
        calendar.set(13, second);
        calendar.set(14, ms);
        return calendar.getTime().getTime();
    }

    protected String parseString() throws IOException {
        this._sbuf.setLength(0);
        return this.parseString(this._sbuf).toString();
    }

    protected StringBuffer parseString(StringBuffer sbuf) throws IOException {
        int ch;
        while ((ch = this.readChar()) >= 0) {
            sbuf.append((char)ch);
        }
        return sbuf;
    }

    Node parseXML() throws IOException {
        throw this.error("help!");
    }

    int readChar() throws IOException {
        int ch = this.read();
        if (ch == 60 || ch < 0) {
            this._peek = ch;
            return -1;
        }
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
                    if (ch != 59) {
                        throw this.error("expected ';' at " + (char)ch);
                    }
                    return (char)v;
                }
                throw this.error("expected digit at " + (char)ch);
            }
            this._entityBuffer.setLength(0);
            while (ch >= 97 && ch <= 122) {
                this._entityBuffer.append((char)ch);
                ch = this.read();
            }
            String entity = this._entityBuffer.toString();
            if (ch != 59) {
                throw this.expectedChar("';'", ch);
            }
            if (entity.equals("amp")) {
                return 38;
            }
            if (entity.equals("apos")) {
                return 39;
            }
            if (entity.equals("quot")) {
                return 34;
            }
            if (entity.equals("lt")) {
                return 60;
            }
            if (entity.equals("gt")) {
                return 62;
            }
            throw new BurlapProtocolException("unknown XML entity &" + entity + "; at `" + (char)ch + "'");
        }
        if (ch < 128) {
            return (char)ch;
        }
        if ((ch & 0xE0) == 192) {
            int ch1 = this.read();
            int v = ((ch & 0x1F) << 6) + (ch1 & 0x3F);
            return (char)v;
        }
        if ((ch & 0xF0) == 224) {
            int ch1 = this.read();
            int ch2 = this.read();
            int v = ((ch & 0xF) << 12) + ((ch1 & 0x3F) << 6) + (ch2 & 0x3F);
            return (char)v;
        }
        throw new BurlapProtocolException("bad utf-8 encoding");
    }

    protected byte[] parseBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.parseBytes(bos);
        return bos.toByteArray();
    }

    protected ByteArrayOutputStream parseBytes(ByteArrayOutputStream bos) throws IOException {
        int ch = this.skipWhitespace();
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
                chunk = (base64Decode[b1] << 10) + (base64Decode[b2] << 4) + (base64Decode[b3] >> 2);
                bos.write(chunk >> 8);
                bos.write(chunk);
            } else {
                chunk = (base64Decode[b1] << 2) + (base64Decode[b2] >> 4);
                bos.write(chunk);
            }
            ch = this.skipWhitespace();
        }
        if (ch == 60) {
            this._peek = ch;
        }
        return bos;
    }

    public void expectTag(int expectTag) throws IOException {
        int tag = this.parseTag();
        if (tag != expectTag) {
            throw this.error("expected " + BurlapInput.tagName(expectTag) + " at " + BurlapInput.tagName(tag));
        }
    }

    protected int parseTag() throws IOException {
        if (this._peekTag >= 0) {
            int tag = this._peekTag;
            this._peekTag = -1;
            return tag;
        }
        int ch = this.skipWhitespace();
        int endTagDelta = 0;
        if (ch != 60) {
            throw this.expectedChar("'<'", ch);
        }
        ch = this.read();
        if (ch == 47) {
            endTagDelta = 100;
            ch = this._is.read();
        }
        if (!this.isTagChar(ch)) {
            throw this.expectedChar("tag", ch);
        }
        this._sbuf.setLength(0);
        while (this.isTagChar(ch)) {
            this._sbuf.append((char)ch);
            ch = this.read();
        }
        if (ch != 62) {
            throw this.expectedChar("'>'", ch);
        }
        Integer value = (Integer)_tagMap.get(this._sbuf.toString());
        if (value == null) {
            throw this.error("Unknown tag <" + this._sbuf + ">");
        }
        return value + endTagDelta;
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

    int read(byte[] buffer, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    int read() throws IOException {
        if (this._peek >= 0) {
            int value = this._peek;
            this._peek = -1;
            return value;
        }
        int ch = this._is.read();
        return ch;
    }

    public Reader getReader() {
        return null;
    }

    public InputStream readInputStream() {
        return null;
    }

    public InputStream getInputStream() {
        return null;
    }

    protected IOException expectBeginTag(String expect, String tag) {
        return new BurlapProtocolException("expected <" + expect + "> at <" + tag + ">");
    }

    protected IOException expectedChar(String expect, int ch) {
        if (ch < 0) {
            return this.error("expected " + expect + " at end of file");
        }
        return this.error("expected " + expect + " at " + (char)ch);
    }

    protected IOException expectedTag(String expect, int tag) {
        return this.error("expected " + expect + " at " + BurlapInput.tagName(tag));
    }

    protected IOException error(String message) {
        return new BurlapProtocolException(message);
    }

    protected static String tagName(int tag) {
        switch (tag) {
            case 0: {
                return "<null>";
            }
            case 100: {
                return "</null>";
            }
            case 1: {
                return "<boolean>";
            }
            case 101: {
                return "</boolean>";
            }
            case 2: {
                return "<int>";
            }
            case 102: {
                return "</int>";
            }
            case 3: {
                return "<long>";
            }
            case 103: {
                return "</long>";
            }
            case 4: {
                return "<double>";
            }
            case 104: {
                return "</double>";
            }
            case 6: {
                return "<string>";
            }
            case 106: {
                return "</string>";
            }
            case 7: {
                return "<xml>";
            }
            case 107: {
                return "</xml>";
            }
            case 8: {
                return "<base64>";
            }
            case 108: {
                return "</base64>";
            }
            case 9: {
                return "<map>";
            }
            case 109: {
                return "</map>";
            }
            case 10: {
                return "<list>";
            }
            case 110: {
                return "</list>";
            }
            case 11: {
                return "<type>";
            }
            case 111: {
                return "</type>";
            }
            case 12: {
                return "<length>";
            }
            case 112: {
                return "</length>";
            }
            case 13: {
                return "<ref>";
            }
            case 113: {
                return "</ref>";
            }
            case 14: {
                return "<remote>";
            }
            case 114: {
                return "</remote>";
            }
            case 15: {
                return "<burlap:call>";
            }
            case 115: {
                return "</burlap:call>";
            }
            case 16: {
                return "<burlap:reply>";
            }
            case 116: {
                return "</burlap:reply>";
            }
            case 19: {
                return "<header>";
            }
            case 119: {
                return "</header>";
            }
            case 17: {
                return "<fault>";
            }
            case 117: {
                return "</fault>";
            }
            case -1: {
                return "end of file";
            }
        }
        return "unknown " + tag;
    }

    static {
        int i;
        _tagMap = new HashMap();
        _tagMap.put("null", new Integer(0));
        _tagMap.put("boolean", new Integer(1));
        _tagMap.put("int", new Integer(2));
        _tagMap.put("long", new Integer(3));
        _tagMap.put("double", new Integer(4));
        _tagMap.put("date", new Integer(5));
        _tagMap.put("string", new Integer(6));
        _tagMap.put("xml", new Integer(7));
        _tagMap.put("base64", new Integer(8));
        _tagMap.put("map", new Integer(9));
        _tagMap.put("list", new Integer(10));
        _tagMap.put("type", new Integer(11));
        _tagMap.put("length", new Integer(12));
        _tagMap.put("ref", new Integer(13));
        _tagMap.put("remote", new Integer(14));
        _tagMap.put("burlap:call", new Integer(15));
        _tagMap.put("burlap:reply", new Integer(16));
        _tagMap.put("fault", new Integer(17));
        _tagMap.put("method", new Integer(18));
        _tagMap.put("header", new Integer(19));
        base64Decode = new int[256];
        for (i = 65; i <= 90; ++i) {
            BurlapInput.base64Decode[i] = i - 65;
        }
        for (i = 97; i <= 122; ++i) {
            BurlapInput.base64Decode[i] = i - 97 + 26;
        }
        for (i = 48; i <= 57; ++i) {
            BurlapInput.base64Decode[i] = i - 48 + 52;
        }
        BurlapInput.base64Decode[43] = 62;
        BurlapInput.base64Decode[47] = 63;
        try {
            _detailMessageField = Throwable.class.getDeclaredField("detailMessage");
            _detailMessageField.setAccessible(true);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

