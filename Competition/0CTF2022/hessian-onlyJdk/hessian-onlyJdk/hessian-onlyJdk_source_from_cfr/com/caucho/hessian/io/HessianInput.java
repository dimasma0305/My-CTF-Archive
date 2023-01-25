/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.HessianRemote;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.HessianServiceException;
import com.caucho.hessian.io.SerializerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.w3c.dom.Node;

public class HessianInput
extends AbstractHessianInput {
    private static int END_OF_DATA = -2;
    private static Field _detailMessageField;
    protected SerializerFactory _serializerFactory;
    protected ArrayList _refs;
    private InputStream _is;
    protected int _peek = -1;
    private String _method;
    private Reader _chunkReader;
    private InputStream _chunkInputStream;
    private Throwable _replyFault;
    private StringBuffer _sbuf = new StringBuffer();
    private boolean _isLastChunk;
    private int _chunkLength;

    public HessianInput() {
    }

    public HessianInput(InputStream is) {
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
        this._isLastChunk = true;
        this._chunkLength = 0;
        this._peek = -1;
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

    public int readCall() throws IOException {
        int tag = this.read();
        if (tag != 99) {
            throw this.error("expected hessian call ('c') at " + this.codeName(tag));
        }
        int major = this.read();
        int minor = this.read();
        return (major << 16) + minor;
    }

    public void skipOptionalCall() throws IOException {
        int tag = this.read();
        if (tag == 99) {
            this.read();
            this.read();
        } else {
            this._peek = tag;
        }
    }

    public String readMethod() throws IOException {
        int ch;
        int tag = this.read();
        if (tag != 109) {
            throw this.error("expected hessian method ('m') at " + this.codeName(tag));
        }
        int d1 = this.read();
        int d2 = this.read();
        this._isLastChunk = true;
        this._chunkLength = d1 * 256 + d2;
        this._sbuf.setLength(0);
        while ((ch = this.parseChar()) >= 0) {
            this._sbuf.append((char)ch);
        }
        this._method = this._sbuf.toString();
        return this._method;
    }

    public void startCall() throws IOException {
        this.readCall();
        while (this.readHeader() != null) {
            this.readObject();
        }
        this.readMethod();
    }

    public void completeCall() throws IOException {
        int tag = this.read();
        if (tag != 122) {
            throw this.error("expected end of call ('z') at " + this.codeName(tag) + ".  Check method arguments and ensure method overloading is enabled if necessary");
        }
    }

    public Object readReply(Class expectedClass) throws Throwable {
        int tag = this.read();
        if (tag != 114) {
            this.error("expected hessian reply at " + this.codeName(tag));
        }
        int major = this.read();
        int minor = this.read();
        tag = this.read();
        if (tag == 102) {
            throw this.prepareFault();
        }
        this._peek = tag;
        Object value = this.readObject(expectedClass);
        this.completeValueReply();
        return value;
    }

    public void startReply() throws Throwable {
        int tag = this.read();
        if (tag != 114) {
            this.error("expected hessian reply at " + this.codeName(tag));
        }
        int major = this.read();
        int minor = this.read();
        this.startReplyBody();
    }

    public void startReplyBody() throws Throwable {
        int tag = this.read();
        if (tag == 102) {
            throw this.prepareFault();
        }
        this._peek = tag;
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
        this._replyFault = new HessianServiceException(message, code, detail);
        return this._replyFault;
    }

    public void completeReply() throws IOException {
        int tag = this.read();
        if (tag != 122) {
            this.error("expected end of reply at " + this.codeName(tag));
        }
    }

    public void completeValueReply() throws IOException {
        int tag = this.read();
        if (tag != 122) {
            this.error("expected end of reply at " + this.codeName(tag));
        }
    }

    public String readHeader() throws IOException {
        int tag = this.read();
        if (tag == 72) {
            int ch;
            this._isLastChunk = true;
            this._chunkLength = (this.read() << 8) + this.read();
            this._sbuf.setLength(0);
            while ((ch = this.parseChar()) >= 0) {
                this._sbuf.append((char)ch);
            }
            return this._sbuf.toString();
        }
        this._peek = tag;
        return null;
    }

    public void readNull() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return;
            }
        }
        throw this.expect("null", tag);
    }

    public boolean readBoolean() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 84: {
                return true;
            }
            case 70: {
                return false;
            }
            case 73: {
                return this.parseInt() == 0;
            }
            case 76: {
                return this.parseLong() == 0L;
            }
            case 68: {
                return this.parseDouble() == 0.0;
            }
            case 78: {
                return false;
            }
        }
        throw this.expect("boolean", tag);
    }

    public short readShort() throws IOException {
        return (short)this.readInt();
    }

    public int readInt() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 84: {
                return 1;
            }
            case 70: {
                return 0;
            }
            case 73: {
                return this.parseInt();
            }
            case 76: {
                return (int)this.parseLong();
            }
            case 68: {
                return (int)this.parseDouble();
            }
        }
        throw this.expect("int", tag);
    }

    public long readLong() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 84: {
                return 1L;
            }
            case 70: {
                return 0L;
            }
            case 73: {
                return this.parseInt();
            }
            case 76: {
                return this.parseLong();
            }
            case 68: {
                return (long)this.parseDouble();
            }
        }
        throw this.expect("long", tag);
    }

    public float readFloat() throws IOException {
        return (float)this.readDouble();
    }

    public double readDouble() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 84: {
                return 1.0;
            }
            case 70: {
                return 0.0;
            }
            case 73: {
                return this.parseInt();
            }
            case 76: {
                return this.parseLong();
            }
            case 68: {
                return this.parseDouble();
            }
        }
        throw this.expect("long", tag);
    }

    public long readUTCDate() throws IOException {
        int tag = this.read();
        if (tag != 100) {
            throw this.error("expected date at " + this.codeName(tag));
        }
        long b64 = this.read();
        long b56 = this.read();
        long b48 = this.read();
        long b40 = this.read();
        long b32 = this.read();
        long b24 = this.read();
        long b16 = this.read();
        long b8 = this.read();
        return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    public int readChar() throws IOException {
        if (this._chunkLength > 0) {
            --this._chunkLength;
            if (this._chunkLength == 0 && this._isLastChunk) {
                this._chunkLength = END_OF_DATA;
            }
            int ch = this.parseUTF8Char();
            return ch;
        }
        if (this._chunkLength == END_OF_DATA) {
            this._chunkLength = 0;
            return -1;
        }
        int tag = this.read();
        switch (tag) {
            case 78: {
                return -1;
            }
            case 83: 
            case 88: 
            case 115: 
            case 120: {
                this._isLastChunk = tag == 83 || tag == 88;
                this._chunkLength = (this.read() << 8) + this.read();
                --this._chunkLength;
                int value = this.parseUTF8Char();
                if (this._chunkLength == 0 && this._isLastChunk) {
                    this._chunkLength = END_OF_DATA;
                }
                return value;
            }
        }
        throw new IOException("expected 'S' at " + (char)tag);
    }

    public int readString(char[] buffer, int offset, int length) throws IOException {
        int tag;
        int readLength = 0;
        if (this._chunkLength == END_OF_DATA) {
            this._chunkLength = 0;
            return -1;
        }
        if (this._chunkLength == 0) {
            tag = this.read();
            switch (tag) {
                case 78: {
                    return -1;
                }
                case 83: 
                case 88: 
                case 115: 
                case 120: {
                    this._isLastChunk = tag == 83 || tag == 88;
                    this._chunkLength = (this.read() << 8) + this.read();
                    break;
                }
                default: {
                    throw new IOException("expected 'S' at " + (char)tag);
                }
            }
        }
        block7: while (length > 0) {
            if (this._chunkLength > 0) {
                buffer[offset++] = (char)this.parseUTF8Char();
                --this._chunkLength;
                --length;
                ++readLength;
                continue;
            }
            if (this._isLastChunk) {
                if (readLength == 0) {
                    return -1;
                }
                this._chunkLength = END_OF_DATA;
                return readLength;
            }
            tag = this.read();
            switch (tag) {
                case 83: 
                case 88: 
                case 115: 
                case 120: {
                    this._isLastChunk = tag == 83 || tag == 88;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block7;
                }
            }
            throw new IOException("expected 'S' at " + (char)tag);
        }
        if (readLength == 0) {
            return -1;
        }
        if (this._chunkLength > 0 || !this._isLastChunk) {
            return readLength;
        }
        this._chunkLength = END_OF_DATA;
        return readLength;
    }

    public String readString() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 73: {
                return String.valueOf(this.parseInt());
            }
            case 76: {
                return String.valueOf(this.parseLong());
            }
            case 68: {
                return String.valueOf(this.parseDouble());
            }
            case 83: 
            case 88: 
            case 115: 
            case 120: {
                int ch;
                this._isLastChunk = tag == 83 || tag == 88;
                this._chunkLength = (this.read() << 8) + this.read();
                this._sbuf.setLength(0);
                while ((ch = this.parseChar()) >= 0) {
                    this._sbuf.append((char)ch);
                }
                return this._sbuf.toString();
            }
        }
        throw this.expect("string", tag);
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
                this._isLastChunk = tag == 83 || tag == 88;
                this._chunkLength = (this.read() << 8) + this.read();
                throw this.error("Can't handle string in this context");
            }
        }
        throw this.expect("string", tag);
    }

    public byte[] readBytes() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 66: 
            case 98: {
                int data;
                this._isLastChunk = tag == 66;
                this._chunkLength = (this.read() << 8) + this.read();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((data = this.parseByte()) >= 0) {
                    bos.write(data);
                }
                return bos.toByteArray();
            }
        }
        throw this.expect("bytes", tag);
    }

    public int readByte() throws IOException {
        if (this._chunkLength > 0) {
            --this._chunkLength;
            if (this._chunkLength == 0 && this._isLastChunk) {
                this._chunkLength = END_OF_DATA;
            }
            return this.read();
        }
        if (this._chunkLength == END_OF_DATA) {
            this._chunkLength = 0;
            return -1;
        }
        int tag = this.read();
        switch (tag) {
            case 78: {
                return -1;
            }
            case 66: 
            case 98: {
                this._isLastChunk = tag == 66;
                this._chunkLength = (this.read() << 8) + this.read();
                int value = this.parseByte();
                if (this._chunkLength == 0 && this._isLastChunk) {
                    this._chunkLength = END_OF_DATA;
                }
                return value;
            }
        }
        throw new IOException("expected 'B' at " + (char)tag);
    }

    public int readBytes(byte[] buffer, int offset, int length) throws IOException {
        int tag;
        int readLength = 0;
        if (this._chunkLength == END_OF_DATA) {
            this._chunkLength = 0;
            return -1;
        }
        if (this._chunkLength == 0) {
            tag = this.read();
            switch (tag) {
                case 78: {
                    return -1;
                }
                case 66: 
                case 98: {
                    this._isLastChunk = tag == 66;
                    this._chunkLength = (this.read() << 8) + this.read();
                    break;
                }
                default: {
                    throw new IOException("expected 'B' at " + (char)tag);
                }
            }
        }
        block7: while (length > 0) {
            if (this._chunkLength > 0) {
                buffer[offset++] = (byte)this.read();
                --this._chunkLength;
                --length;
                ++readLength;
                continue;
            }
            if (this._isLastChunk) {
                if (readLength == 0) {
                    return -1;
                }
                this._chunkLength = END_OF_DATA;
                return readLength;
            }
            tag = this.read();
            switch (tag) {
                case 66: 
                case 98: {
                    this._isLastChunk = tag == 66;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block7;
                }
            }
            throw new IOException("expected 'B' at " + (char)tag);
        }
        if (readLength == 0) {
            return -1;
        }
        if (this._chunkLength > 0 || !this._isLastChunk) {
            return readLength;
        }
        this._chunkLength = END_OF_DATA;
        return readLength;
    }

    private HashMap readFault() throws IOException {
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        int code = this.read();
        while (code > 0 && code != 122) {
            this._peek = code;
            Object key = this.readObject();
            Object value = this.readObject();
            if (key != null && value != null) {
                map.put(key, value);
            }
            code = this.read();
        }
        if (code != 122) {
            throw this.expect("fault", code);
        }
        return map;
    }

    public Object readObject(Class cl) throws IOException {
        if (cl == null || cl == Object.class) {
            return this.readObject();
        }
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 77: {
                String type = this.readType();
                if ("".equals(type)) {
                    Deserializer reader = this._serializerFactory.getDeserializer(cl);
                    return reader.readMap(this);
                }
                Deserializer reader = this._serializerFactory.getObjectDeserializer(type);
                return reader.readMap(this);
            }
            case 86: {
                String type = this.readType();
                int length = this.readLength();
                Deserializer reader = this._serializerFactory.getObjectDeserializer(type);
                if (cl != reader.getType() && cl.isAssignableFrom(reader.getType())) {
                    return reader.readList(this, length);
                }
                reader = this._serializerFactory.getDeserializer(cl);
                Object v = reader.readList(this, length);
                return v;
            }
            case 82: {
                int ref = this.parseInt();
                return this._refs.get(ref);
            }
            case 114: {
                String type = this.readType();
                String url = this.readString();
                return this.resolveRemote(type, url);
            }
        }
        this._peek = tag;
        Object value = this._serializerFactory.getDeserializer(cl).readObject(this);
        return value;
    }

    public Object readObject() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 84: {
                return true;
            }
            case 70: {
                return false;
            }
            case 73: {
                return this.parseInt();
            }
            case 76: {
                return this.parseLong();
            }
            case 68: {
                return this.parseDouble();
            }
            case 100: {
                return new Date(this.parseLong());
            }
            case 88: 
            case 120: {
                this._isLastChunk = tag == 88;
                this._chunkLength = (this.read() << 8) + this.read();
                return this.parseXML();
            }
            case 83: 
            case 115: {
                int data;
                this._isLastChunk = tag == 83;
                this._chunkLength = (this.read() << 8) + this.read();
                this._sbuf.setLength(0);
                while ((data = this.parseChar()) >= 0) {
                    this._sbuf.append((char)data);
                }
                return this._sbuf.toString();
            }
            case 66: 
            case 98: {
                int data;
                this._isLastChunk = tag == 66;
                this._chunkLength = (this.read() << 8) + this.read();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((data = this.parseByte()) >= 0) {
                    bos.write(data);
                }
                return bos.toByteArray();
            }
            case 86: {
                String type = this.readType();
                int length = this.readLength();
                return this._serializerFactory.readList(this, length, type);
            }
            case 77: {
                String type = this.readType();
                return this._serializerFactory.readMap(this, type);
            }
            case 82: {
                int ref = this.parseInt();
                return this._refs.get(ref);
            }
            case 114: {
                String type = this.readType();
                String url = this.readString();
                return this.resolveRemote(type, url);
            }
        }
        throw this.error("unknown code for readObject at " + this.codeName(tag));
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
        return this.read();
    }

    public int readMapStart() throws IOException {
        return this.read();
    }

    public boolean isEnd() throws IOException {
        int code;
        this._peek = code = this.read();
        return code < 0 || code == 122;
    }

    public void readEnd() throws IOException {
        int code = this.read();
        if (code != 122) {
            throw this.error("unknown code at " + this.codeName(code));
        }
    }

    public void readMapEnd() throws IOException {
        int code = this.read();
        if (code != 122) {
            throw this.error("expected end of map ('z') at " + this.codeName(code));
        }
    }

    public void readListEnd() throws IOException {
        int code = this.read();
        if (code != 122) {
            throw this.error("expected end of list ('z') at " + this.codeName(code));
        }
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

    public void resetReferences() {
        if (this._refs != null) {
            this._refs.clear();
        }
    }

    public Object resolveRemote(String type, String url) throws IOException {
        HessianRemoteResolver resolver = this.getRemoteResolver();
        if (resolver != null) {
            return resolver.lookup(type, url);
        }
        return new HessianRemote(type, url);
    }

    public String readType() throws IOException {
        int ch;
        int code = this.read();
        if (code != 116) {
            this._peek = code;
            return "";
        }
        this._isLastChunk = true;
        this._chunkLength = (this.read() << 8) + this.read();
        this._sbuf.setLength(0);
        while ((ch = this.parseChar()) >= 0) {
            this._sbuf.append((char)ch);
        }
        return this._sbuf.toString();
    }

    public int readLength() throws IOException {
        int code = this.read();
        if (code != 108) {
            this._peek = code;
            return -1;
        }
        return this.parseInt();
    }

    private int parseInt() throws IOException {
        int b32 = this.read();
        int b24 = this.read();
        int b16 = this.read();
        int b8 = this.read();
        return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    private long parseLong() throws IOException {
        long b64 = this.read();
        long b56 = this.read();
        long b48 = this.read();
        long b40 = this.read();
        long b32 = this.read();
        long b24 = this.read();
        long b16 = this.read();
        long b8 = this.read();
        return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    private double parseDouble() throws IOException {
        long b64 = this.read();
        long b56 = this.read();
        long b48 = this.read();
        long b40 = this.read();
        long b32 = this.read();
        long b24 = this.read();
        long b16 = this.read();
        long b8 = this.read();
        long bits = (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
        return Double.longBitsToDouble(bits);
    }

    Node parseXML() throws IOException {
        throw new UnsupportedOperationException();
    }

    private int parseChar() throws IOException {
        block4: while (this._chunkLength <= 0) {
            if (this._isLastChunk) {
                return -1;
            }
            int code = this.read();
            switch (code) {
                case 115: 
                case 120: {
                    this._isLastChunk = false;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block4;
                }
                case 83: 
                case 88: {
                    this._isLastChunk = true;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block4;
                }
            }
            throw this.expect("string", code);
        }
        --this._chunkLength;
        return this.parseUTF8Char();
    }

    private int parseUTF8Char() throws IOException {
        int ch = this.read();
        if (ch < 128) {
            return ch;
        }
        if ((ch & 0xE0) == 192) {
            int ch1 = this.read();
            int v = ((ch & 0x1F) << 6) + (ch1 & 0x3F);
            return v;
        }
        if ((ch & 0xF0) == 224) {
            int ch1 = this.read();
            int ch2 = this.read();
            int v = ((ch & 0xF) << 12) + ((ch1 & 0x3F) << 6) + (ch2 & 0x3F);
            return v;
        }
        throw this.error("bad utf-8 encoding at " + this.codeName(ch));
    }

    private int parseByte() throws IOException {
        block4: while (this._chunkLength <= 0) {
            if (this._isLastChunk) {
                return -1;
            }
            int code = this.read();
            switch (code) {
                case 98: {
                    this._isLastChunk = false;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block4;
                }
                case 66: {
                    this._isLastChunk = true;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block4;
                }
            }
            throw this.expect("byte[]", code);
        }
        --this._chunkLength;
        return this.read();
    }

    public InputStream readInputStream() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 66: 
            case 98: {
                this._isLastChunk = tag == 66;
                this._chunkLength = (this.read() << 8) + this.read();
                break;
            }
            default: {
                throw this.expect("inputStream", tag);
            }
        }
        return new InputStream(){
            boolean _isClosed = false;

            public int read() throws IOException {
                if (this._isClosed || HessianInput.this._is == null) {
                    return -1;
                }
                int ch = HessianInput.this.parseByte();
                if (ch < 0) {
                    this._isClosed = true;
                }
                return ch;
            }

            public int read(byte[] buffer, int offset, int length) throws IOException {
                if (this._isClosed || HessianInput.this._is == null) {
                    return -1;
                }
                int len = HessianInput.this.read(buffer, offset, length);
                if (len < 0) {
                    this._isClosed = true;
                }
                return len;
            }

            public void close() throws IOException {
                while (this.read() >= 0) {
                }
                this._isClosed = true;
            }
        };
    }

    int read(byte[] buffer, int offset, int length) throws IOException {
        int readLength = 0;
        while (length > 0) {
            block5: while (this._chunkLength <= 0) {
                if (this._isLastChunk) {
                    return readLength == 0 ? -1 : readLength;
                }
                int code = this.read();
                switch (code) {
                    case 98: {
                        this._isLastChunk = false;
                        this._chunkLength = (this.read() << 8) + this.read();
                        continue block5;
                    }
                    case 66: {
                        this._isLastChunk = true;
                        this._chunkLength = (this.read() << 8) + this.read();
                        continue block5;
                    }
                }
                throw this.expect("byte[]", code);
            }
            int sublen = this._chunkLength;
            if (length < sublen) {
                sublen = length;
            }
            sublen = this._is.read(buffer, offset, sublen);
            offset += sublen;
            readLength += sublen;
            length -= sublen;
            this._chunkLength -= sublen;
        }
        return readLength;
    }

    final int read() throws IOException {
        if (this._peek >= 0) {
            int value = this._peek;
            this._peek = -1;
            return value;
        }
        int ch = this._is.read();
        return ch;
    }

    public void close() {
        this._is = null;
    }

    public Reader getReader() {
        return null;
    }

    protected IOException expect(String expect, int ch) {
        return this.error("expected " + expect + " at " + this.codeName(ch));
    }

    protected String codeName(int ch) {
        if (ch < 0) {
            return "end of file";
        }
        return "0x" + Integer.toHexString(ch & 0xFF) + " (" + (char)ch + ")";
    }

    protected IOException error(String message) {
        if (this._method != null) {
            return new HessianProtocolException(this._method + ": " + message);
        }
        return new HessianProtocolException(message);
    }

    static {
        try {
            _detailMessageField = Throwable.class.getDeclaredField("detailMessage");
            _detailMessageField.setAccessible(true);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

