/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Constants;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.util.IdentityIntMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class Hessian2Output
extends AbstractHessianOutput
implements Hessian2Constants {
    public static final int SIZE = 8192;
    protected OutputStream _os;
    private final IdentityIntMap _refs = new IdentityIntMap(256);
    private int _refCount = 0;
    private boolean _isCloseStreamOnClose;
    private final IdentityIntMap _classRefs = new IdentityIntMap(256);
    private HashMap<String, Integer> _typeRefs;
    private final byte[] _buffer = new byte[8192];
    private int _offset;
    private boolean _isPacket;
    private boolean _isUnshared;

    public Hessian2Output() {
    }

    public Hessian2Output(OutputStream os) {
        this.init(os);
    }

    public void init(OutputStream os) {
        this.reset();
        this._os = os;
    }

    public void initPacket(OutputStream os) {
        this.resetReferences();
        this._os = os;
    }

    public void setCloseStreamOnClose(boolean isClose) {
        this._isCloseStreamOnClose = isClose;
    }

    public boolean isCloseStreamOnClose() {
        return this._isCloseStreamOnClose;
    }

    public boolean setUnshared(boolean isUnshared) {
        boolean oldIsUnshared = this._isUnshared;
        this._isUnshared = isUnshared;
        return oldIsUnshared;
    }

    public void call(String method, Object[] args) throws IOException {
        this.writeVersion();
        int length = args != null ? args.length : 0;
        this.startCall(method, length);
        for (int i = 0; i < length; ++i) {
            this.writeObject(args[i]);
        }
        this.completeCall();
        this.flush();
    }

    public void startCall(String method, int length) throws IOException {
        int offset = this._offset;
        if (8192 < offset + 32) {
            this.flushBuffer();
            offset = this._offset;
        }
        byte[] buffer = this._buffer;
        buffer[this._offset++] = 67;
        this.writeString(method);
        this.writeInt(length);
    }

    public void startCall() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 67;
    }

    public void startEnvelope(String method) throws IOException {
        int offset = this._offset;
        if (8192 < offset + 32) {
            this.flushBuffer();
            offset = this._offset;
        }
        this._buffer[this._offset++] = 69;
        this.writeString(method);
    }

    public void completeEnvelope() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 90;
    }

    public void writeMethod(String method) throws IOException {
        this.writeString(method);
    }

    public void completeCall() throws IOException {
    }

    public void startReply() throws IOException {
        this.writeVersion();
        this.flushIfFull();
        this._buffer[this._offset++] = 82;
    }

    public void writeVersion() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 72;
        this._buffer[this._offset++] = 2;
        this._buffer[this._offset++] = 0;
    }

    public void completeReply() throws IOException {
    }

    public void startMessage() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 112;
        this._buffer[this._offset++] = 2;
        this._buffer[this._offset++] = 0;
    }

    public void completeMessage() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 122;
    }

    public void writeFault(String code, String message, Object detail) throws IOException {
        this.flushIfFull();
        this.writeVersion();
        this._buffer[this._offset++] = 70;
        this._buffer[this._offset++] = 72;
        this.addRef(new Object(), this._refCount++, false);
        this.writeString("code");
        this.writeString(code);
        this.writeString("message");
        this.writeString(message);
        if (detail != null) {
            this.writeString("detail");
            this.writeObject(detail);
        }
        this.flushIfFull();
        this._buffer[this._offset++] = 90;
    }

    public void writeObject(Object object) throws IOException {
        if (object == null) {
            this.writeNull();
            return;
        }
        Serializer serializer = this.findSerializerFactory().getObjectSerializer(object.getClass());
        serializer.writeObject(object, this);
    }

    public boolean writeListBegin(int length, String type) throws IOException {
        this.flushIfFull();
        if (length < 0) {
            if (type != null) {
                this._buffer[this._offset++] = 85;
                this.writeType(type);
            } else {
                this._buffer[this._offset++] = 87;
            }
            return true;
        }
        if (length <= 7) {
            if (type != null) {
                this._buffer[this._offset++] = (byte)(112 + length);
                this.writeType(type);
            } else {
                this._buffer[this._offset++] = (byte)(120 + length);
            }
            return false;
        }
        if (type != null) {
            this._buffer[this._offset++] = 86;
            this.writeType(type);
        } else {
            this._buffer[this._offset++] = 88;
        }
        this.writeInt(length);
        return false;
    }

    public void writeListEnd() throws IOException {
        this.flushIfFull();
        this._buffer[this._offset++] = 90;
    }

    public void writeMapBegin(String type) throws IOException {
        if (8192 < this._offset + 32) {
            this.flushBuffer();
        }
        if (type != null) {
            this._buffer[this._offset++] = 77;
            this.writeType(type);
        } else {
            this._buffer[this._offset++] = 72;
        }
    }

    public void writeMapEnd() throws IOException {
        if (8192 < this._offset + 32) {
            this.flushBuffer();
        }
        this._buffer[this._offset++] = 90;
    }

    public int writeObjectBegin(String type) throws IOException {
        int ref;
        int newRef = this._classRefs.size();
        if (newRef != (ref = this._classRefs.put(type, newRef, false))) {
            if (8192 < this._offset + 32) {
                this.flushBuffer();
            }
            if (ref <= 15) {
                this._buffer[this._offset++] = (byte)(96 + ref);
            } else {
                this._buffer[this._offset++] = 79;
                this.writeInt(ref);
            }
            return ref;
        }
        if (8192 < this._offset + 32) {
            this.flushBuffer();
        }
        this._buffer[this._offset++] = 67;
        this.writeString(type);
        return -1;
    }

    public void writeClassFieldLength(int len) throws IOException {
        this.writeInt(len);
    }

    public void writeObjectEnd() throws IOException {
    }

    private void writeType(String type) throws IOException {
        Integer typeRefV;
        this.flushIfFull();
        int len = type.length();
        if (len == 0) {
            throw new IllegalArgumentException("empty type is not allowed");
        }
        if (this._typeRefs == null) {
            this._typeRefs = new HashMap();
        }
        if ((typeRefV = this._typeRefs.get(type)) != null) {
            int typeRef = typeRefV;
            this.writeInt(typeRef);
        } else {
            this._typeRefs.put(type, this._typeRefs.size());
            this.writeString(type);
        }
    }

    public void writeBoolean(boolean value) throws IOException {
        if (8192 < this._offset + 16) {
            this.flushBuffer();
        }
        this._buffer[this._offset++] = value ? 84 : 70;
    }

    public void writeInt(int value) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (8192 <= offset + 16) {
            this.flushBuffer();
            offset = this._offset;
        }
        if (-16 <= value && value <= 47) {
            buffer[offset++] = (byte)(value + 144);
        } else if (-2048 <= value && value <= 2047) {
            buffer[offset++] = (byte)(200 + (value >> 8));
            buffer[offset++] = (byte)value;
        } else if (-262144 <= value && value <= 262143) {
            buffer[offset++] = (byte)(212 + (value >> 16));
            buffer[offset++] = (byte)(value >> 8);
            buffer[offset++] = (byte)value;
        } else {
            buffer[offset++] = 73;
            buffer[offset++] = (byte)(value >> 24);
            buffer[offset++] = (byte)(value >> 16);
            buffer[offset++] = (byte)(value >> 8);
            buffer[offset++] = (byte)value;
        }
        this._offset = offset;
    }

    public void writeLong(long value) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (8192 <= offset + 16) {
            this.flushBuffer();
            offset = this._offset;
        }
        if (-8L <= value && value <= 15L) {
            buffer[offset++] = (byte)(value + 224L);
        } else if (-2048L <= value && value <= 2047L) {
            buffer[offset++] = (byte)(248L + (value >> 8));
            buffer[offset++] = (byte)value;
        } else if (-262144L <= value && value <= 262143L) {
            buffer[offset++] = (byte)(60L + (value >> 16));
            buffer[offset++] = (byte)(value >> 8);
            buffer[offset++] = (byte)value;
        } else if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE) {
            buffer[offset + 0] = 89;
            buffer[offset + 1] = (byte)(value >> 24);
            buffer[offset + 2] = (byte)(value >> 16);
            buffer[offset + 3] = (byte)(value >> 8);
            buffer[offset + 4] = (byte)value;
            offset += 5;
        } else {
            buffer[offset + 0] = 76;
            buffer[offset + 1] = (byte)(value >> 56);
            buffer[offset + 2] = (byte)(value >> 48);
            buffer[offset + 3] = (byte)(value >> 40);
            buffer[offset + 4] = (byte)(value >> 32);
            buffer[offset + 5] = (byte)(value >> 24);
            buffer[offset + 6] = (byte)(value >> 16);
            buffer[offset + 7] = (byte)(value >> 8);
            buffer[offset + 8] = (byte)value;
            offset += 9;
        }
        this._offset = offset;
    }

    public void writeDouble(double value) throws IOException {
        int mills;
        int intValue;
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (8192 <= offset + 16) {
            this.flushBuffer();
            offset = this._offset;
        }
        if ((double)(intValue = (int)value) == value) {
            if (intValue == 0) {
                buffer[offset++] = 91;
                this._offset = offset;
                return;
            }
            if (intValue == 1) {
                buffer[offset++] = 92;
                this._offset = offset;
                return;
            }
            if (-128 <= intValue && intValue < 128) {
                buffer[offset++] = 93;
                buffer[offset++] = (byte)intValue;
                this._offset = offset;
                return;
            }
            if (-32768 <= intValue && intValue < 32768) {
                buffer[offset + 0] = 94;
                buffer[offset + 1] = (byte)(intValue >> 8);
                buffer[offset + 2] = (byte)intValue;
                this._offset = offset + 3;
                return;
            }
        }
        if (0.001 * (double)(mills = (int)(value * 1000.0)) == value) {
            buffer[offset + 0] = 95;
            buffer[offset + 1] = (byte)(mills >> 24);
            buffer[offset + 2] = (byte)(mills >> 16);
            buffer[offset + 3] = (byte)(mills >> 8);
            buffer[offset + 4] = (byte)mills;
            this._offset = offset + 5;
            return;
        }
        long bits = Double.doubleToLongBits(value);
        buffer[offset + 0] = 68;
        buffer[offset + 1] = (byte)(bits >> 56);
        buffer[offset + 2] = (byte)(bits >> 48);
        buffer[offset + 3] = (byte)(bits >> 40);
        buffer[offset + 4] = (byte)(bits >> 32);
        buffer[offset + 5] = (byte)(bits >> 24);
        buffer[offset + 6] = (byte)(bits >> 16);
        buffer[offset + 7] = (byte)(bits >> 8);
        buffer[offset + 8] = (byte)bits;
        this._offset = offset + 9;
    }

    public void writeUTCDate(long time) throws IOException {
        long minutes;
        if (8192 < this._offset + 32) {
            this.flushBuffer();
        }
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (time % 60000L == 0L && ((minutes = time / 60000L) >> 31 == 0L || minutes >> 31 == -1L)) {
            buffer[offset++] = 75;
            buffer[offset++] = (byte)(minutes >> 24);
            buffer[offset++] = (byte)(minutes >> 16);
            buffer[offset++] = (byte)(minutes >> 8);
            buffer[offset++] = (byte)(minutes >> 0);
            this._offset = offset;
            return;
        }
        buffer[offset++] = 74;
        buffer[offset++] = (byte)(time >> 56);
        buffer[offset++] = (byte)(time >> 48);
        buffer[offset++] = (byte)(time >> 40);
        buffer[offset++] = (byte)(time >> 32);
        buffer[offset++] = (byte)(time >> 24);
        buffer[offset++] = (byte)(time >> 16);
        buffer[offset++] = (byte)(time >> 8);
        buffer[offset++] = (byte)time;
        this._offset = offset;
    }

    public void writeNull() throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (8192 <= offset + 16) {
            this.flushBuffer();
            offset = this._offset;
        }
        buffer[offset++] = 78;
        this._offset = offset;
    }

    public void writeString(String value) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (8192 <= offset + 16) {
            this.flushBuffer();
            offset = this._offset;
        }
        if (value == null) {
            buffer[offset++] = 78;
            this._offset = offset;
        } else {
            int length = value.length();
            int strOffset = 0;
            while (length > 32768) {
                char tail;
                int sublen = 32768;
                offset = this._offset;
                if (8192 <= offset + 16) {
                    this.flushBuffer();
                    offset = this._offset;
                }
                if ('\ud800' <= (tail = value.charAt(strOffset + sublen - 1)) && tail <= '\udbff') {
                    --sublen;
                }
                buffer[offset + 0] = 82;
                buffer[offset + 1] = (byte)(sublen >> 8);
                buffer[offset + 2] = (byte)sublen;
                this._offset = offset + 3;
                this.printString(value, strOffset, sublen);
                length -= sublen;
                strOffset += sublen;
            }
            offset = this._offset;
            if (8192 <= offset + 16) {
                this.flushBuffer();
                offset = this._offset;
            }
            if (length <= 31) {
                buffer[offset++] = (byte)(0 + length);
            } else if (length <= 1023) {
                buffer[offset++] = (byte)(48 + (length >> 8));
                buffer[offset++] = (byte)length;
            } else {
                buffer[offset++] = 83;
                buffer[offset++] = (byte)(length >> 8);
                buffer[offset++] = (byte)length;
            }
            this._offset = offset;
            this.printString(value, strOffset, length);
        }
    }

    public void writeString(char[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            if (8192 < this._offset + 16) {
                this.flushBuffer();
            }
            this._buffer[this._offset++] = 78;
        } else {
            while (length > 32768) {
                char tail;
                int sublen = 32768;
                if (8192 < this._offset + 16) {
                    this.flushBuffer();
                }
                if ('\ud800' <= (tail = buffer[offset + sublen - 1]) && tail <= '\udbff') {
                    --sublen;
                }
                this._buffer[this._offset++] = 82;
                this._buffer[this._offset++] = (byte)(sublen >> 8);
                this._buffer[this._offset++] = (byte)sublen;
                this.printString(buffer, offset, sublen);
                length -= sublen;
                offset += sublen;
            }
            if (8192 < this._offset + 16) {
                this.flushBuffer();
            }
            if (length <= 31) {
                this._buffer[this._offset++] = (byte)(0 + length);
            } else if (length <= 1023) {
                this._buffer[this._offset++] = (byte)(48 + (length >> 8));
                this._buffer[this._offset++] = (byte)length;
            } else {
                this._buffer[this._offset++] = 83;
                this._buffer[this._offset++] = (byte)(length >> 8);
                this._buffer[this._offset++] = (byte)length;
            }
            this.printString(buffer, offset, length);
        }
    }

    public void writeBytes(byte[] buffer) throws IOException {
        if (buffer == null) {
            if (8192 < this._offset + 16) {
                this.flushBuffer();
            }
            this._buffer[this._offset++] = 78;
        } else {
            this.writeBytes(buffer, 0, buffer.length);
        }
    }

    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            if (8192 < this._offset + 16) {
                this.flushBuffer();
            }
            this._buffer[this._offset++] = 78;
        } else {
            while (8192 - this._offset - 3 < length) {
                int sublen = 8192 - this._offset - 3;
                if (sublen < 16) {
                    this.flushBuffer();
                    sublen = 8192 - this._offset - 3;
                    if (length < sublen) {
                        sublen = length;
                    }
                }
                this._buffer[this._offset++] = 65;
                this._buffer[this._offset++] = (byte)(sublen >> 8);
                this._buffer[this._offset++] = (byte)sublen;
                System.arraycopy(buffer, offset, this._buffer, this._offset, sublen);
                this._offset += sublen;
                length -= sublen;
                offset += sublen;
                this.flushBuffer();
            }
            if (8192 < this._offset + 16) {
                this.flushBuffer();
            }
            if (length <= 15) {
                this._buffer[this._offset++] = (byte)(32 + length);
            } else if (length <= 1023) {
                this._buffer[this._offset++] = (byte)(52 + (length >> 8));
                this._buffer[this._offset++] = (byte)length;
            } else {
                this._buffer[this._offset++] = 66;
                this._buffer[this._offset++] = (byte)(length >> 8);
                this._buffer[this._offset++] = (byte)length;
            }
            System.arraycopy(buffer, offset, this._buffer, this._offset, length);
            this._offset += length;
        }
    }

    public void writeByteBufferStart() throws IOException {
    }

    public void writeByteBufferPart(byte[] buffer, int offset, int length) throws IOException {
        while (length > 0) {
            this.flushIfFull();
            int sublen = this._buffer.length - this._offset;
            if (length < sublen) {
                sublen = length;
            }
            this._buffer[this._offset++] = 65;
            this._buffer[this._offset++] = (byte)(sublen >> 8);
            this._buffer[this._offset++] = (byte)sublen;
            System.arraycopy(buffer, offset, this._buffer, this._offset, sublen);
            this._offset += sublen;
            length -= sublen;
            offset += sublen;
        }
    }

    public void writeByteBufferEnd(byte[] buffer, int offset, int length) throws IOException {
        this.writeBytes(buffer, offset, length);
    }

    public OutputStream getBytesOutputStream() throws IOException {
        return new BytesOutputStream();
    }

    public void writeByteStream(InputStream is) throws IOException {
        while (true) {
            int len;
            if ((len = 8192 - this._offset - 3) < 16) {
                this.flushBuffer();
                len = 8192 - this._offset - 3;
            }
            if ((len = is.read(this._buffer, this._offset + 3, len)) <= 0) {
                this._buffer[this._offset++] = 32;
                return;
            }
            this._buffer[this._offset + 0] = 65;
            this._buffer[this._offset + 1] = (byte)(len >> 8);
            this._buffer[this._offset + 2] = (byte)len;
            this._offset += len + 3;
        }
    }

    protected void writeRef(int value) throws IOException {
        if (8192 < this._offset + 16) {
            this.flushBuffer();
        }
        this._buffer[this._offset++] = 81;
        this.writeInt(value);
    }

    public boolean addRef(Object object) throws IOException {
        int newRef;
        int ref;
        if (this._isUnshared) {
            ++this._refCount;
            return false;
        }
        if ((ref = this.addRef(object, newRef = this._refCount++, false)) != newRef) {
            this.writeRef(ref);
            return true;
        }
        return false;
    }

    public int getRef(Object obj) {
        if (this._isUnshared) {
            return -1;
        }
        return this._refs.get(obj);
    }

    public boolean removeRef(Object obj) throws IOException {
        if (this._isUnshared) {
            return false;
        }
        if (this._refs != null) {
            this._refs.remove(obj);
            return true;
        }
        return false;
    }

    public boolean replaceRef(Object oldRef, Object newRef) throws IOException {
        if (this._isUnshared) {
            return false;
        }
        int value = this._refs.get(oldRef);
        if (value >= 0) {
            this.addRef(newRef, value, true);
            this._refs.remove(oldRef);
            return true;
        }
        return false;
    }

    private int addRef(Object value, int newRef, boolean isReplace) {
        int prevRef = this._refs.put(value, newRef, isReplace);
        return prevRef;
    }

    public void writeStreamingObject(Object obj) throws IOException {
        this.startPacket();
        this.writeObject(obj);
        this.endPacket();
    }

    public void startPacket() throws IOException {
        if (this._refs != null) {
            this._refs.clear();
            this._refCount = 0;
        }
        this.flushBuffer();
        this._isPacket = true;
        this._offset = 4;
        this._buffer[0] = 5;
        this._buffer[1] = 85;
        this._buffer[2] = 85;
        this._buffer[3] = 85;
    }

    public void endPacket() throws IOException {
        int offset = this._offset;
        OutputStream os = this._os;
        if (os == null) {
            this._offset = 0;
            return;
        }
        int len = offset - 4;
        if (len < 126) {
            this._buffer[2] = this._buffer[0];
            this._buffer[3] = (byte)len;
        } else {
            this._buffer[1] = 126;
            this._buffer[2] = (byte)(len >> 8);
            this._buffer[3] = (byte)len;
        }
        this._isPacket = false;
        this._offset = 0;
        if (os != null) {
            if (len < 126) {
                os.write(this._buffer, 2, offset - 2);
            } else {
                os.write(this._buffer, 0, offset);
            }
        }
    }

    public void printLenString(String v) throws IOException {
        if (8192 < this._offset + 16) {
            this.flushBuffer();
        }
        if (v == null) {
            this._buffer[this._offset++] = 0;
            this._buffer[this._offset++] = 0;
        } else {
            int len = v.length();
            this._buffer[this._offset++] = (byte)(len >> 8);
            this._buffer[this._offset++] = (byte)len;
            this.printString(v, 0, len);
        }
    }

    public void printString(String v) throws IOException {
        this.printString(v, 0, v.length());
    }

    public void printString(String v, int strOffset, int length) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        for (int i = 0; i < length; ++i) {
            char ch;
            if (8192 <= offset + 16) {
                this._offset = offset;
                this.flushBuffer();
                offset = this._offset;
            }
            if ((ch = v.charAt(i + strOffset)) < '\u0080') {
                buffer[offset++] = (byte)ch;
                continue;
            }
            if (ch < '\u0800') {
                buffer[offset++] = (byte)(192 + (ch >> 6 & 0x1F));
                buffer[offset++] = (byte)(128 + (ch & 0x3F));
                continue;
            }
            buffer[offset++] = (byte)(224 + (ch >> 12 & 0xF));
            buffer[offset++] = (byte)(128 + (ch >> 6 & 0x3F));
            buffer[offset++] = (byte)(128 + (ch & 0x3F));
        }
        this._offset = offset;
    }

    public void printString(char[] v, int strOffset, int length) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        for (int i = 0; i < length; ++i) {
            char ch;
            if (8192 <= offset + 16) {
                this._offset = offset;
                this.flushBuffer();
                offset = this._offset;
            }
            if ((ch = v[i + strOffset]) < '\u0080') {
                buffer[offset++] = (byte)ch;
                continue;
            }
            if (ch < '\u0800') {
                buffer[offset++] = (byte)(192 + (ch >> 6 & 0x1F));
                buffer[offset++] = (byte)(128 + (ch & 0x3F));
                continue;
            }
            buffer[offset++] = (byte)(224 + (ch >> 12 & 0xF));
            buffer[offset++] = (byte)(128 + (ch >> 6 & 0x3F));
            buffer[offset++] = (byte)(128 + (ch & 0x3F));
        }
        this._offset = offset;
    }

    private final void flushIfFull() throws IOException {
        int offset = this._offset;
        if (8192 < offset + 32) {
            this.flushBuffer();
        }
    }

    public final void flush() throws IOException {
        this.flushBuffer();
        if (this._os != null) {
            this._os.flush();
        }
    }

    public final void flushBuffer() throws IOException {
        int offset = this._offset;
        OutputStream os = this._os;
        if (!this._isPacket && offset > 0) {
            this._offset = 0;
            if (os != null) {
                os.write(this._buffer, 0, offset);
            }
        } else if (this._isPacket && offset > 4) {
            int len = offset - 4;
            this._buffer[0] = (byte)(this._buffer[0] | 0xFFFFFF80);
            this._buffer[1] = 126;
            this._buffer[2] = (byte)(len >> 8);
            this._buffer[3] = (byte)len;
            this._offset = 4;
            if (os != null) {
                os.write(this._buffer, 0, offset);
            }
            this._buffer[0] = 0;
            this._buffer[1] = 86;
            this._buffer[2] = 86;
            this._buffer[3] = 86;
        }
    }

    public void close() throws IOException {
        this.flush();
        OutputStream os = this._os;
        this._os = null;
        if (os != null && this._isCloseStreamOnClose) {
            os.close();
        }
    }

    public void free() {
        this.reset();
        this._os = null;
        this._isCloseStreamOnClose = false;
    }

    public void resetReferences() {
        if (this._refs != null) {
            this._refs.clear();
            this._refCount = 0;
        }
    }

    public void reset() {
        if (this._refs != null) {
            this._refs.clear();
            this._refCount = 0;
        }
        this._classRefs.clear();
        this._typeRefs = null;
        this._offset = 0;
        this._isPacket = false;
        this._isUnshared = false;
    }

    class BytesOutputStream
    extends OutputStream {
        private int _startOffset;

        BytesOutputStream() throws IOException {
            if (8192 < Hessian2Output.this._offset + 16) {
                Hessian2Output.this.flushBuffer();
            }
            this._startOffset = Hessian2Output.this._offset;
            Hessian2Output.this._offset += 3;
        }

        public void write(int ch) throws IOException {
            if (8192 <= Hessian2Output.this._offset) {
                int length = Hessian2Output.this._offset - this._startOffset - 3;
                ((Hessian2Output)Hessian2Output.this)._buffer[this._startOffset] = 65;
                ((Hessian2Output)Hessian2Output.this)._buffer[this._startOffset + 1] = (byte)(length >> 8);
                ((Hessian2Output)Hessian2Output.this)._buffer[this._startOffset + 2] = (byte)length;
                Hessian2Output.this.flushBuffer();
                this._startOffset = Hessian2Output.this._offset;
                Hessian2Output.this._offset += 3;
            }
            ((Hessian2Output)Hessian2Output.this)._buffer[((Hessian2Output)Hessian2Output.this)._offset++] = (byte)ch;
        }

        public void write(byte[] buffer, int offset, int length) throws IOException {
            while (length > 0) {
                int sublen = 8192 - Hessian2Output.this._offset;
                if (length < sublen) {
                    sublen = length;
                }
                if (sublen > 0) {
                    System.arraycopy(buffer, offset, Hessian2Output.this._buffer, Hessian2Output.this._offset, sublen);
                    Hessian2Output.this._offset += sublen;
                }
                length -= sublen;
                offset += sublen;
                if (8192 > Hessian2Output.this._offset) continue;
                int chunkLength = Hessian2Output.this._offset - this._startOffset - 3;
                ((Hessian2Output)Hessian2Output.this)._buffer[this._startOffset] = 65;
                ((Hessian2Output)Hessian2Output.this)._buffer[this._startOffset + 1] = (byte)(chunkLength >> 8);
                ((Hessian2Output)Hessian2Output.this)._buffer[this._startOffset + 2] = (byte)chunkLength;
                Hessian2Output.this.flushBuffer();
                this._startOffset = Hessian2Output.this._offset;
                Hessian2Output.this._offset += 3;
            }
        }

        public void close() throws IOException {
            int startOffset = this._startOffset;
            this._startOffset = -1;
            if (startOffset < 0) {
                return;
            }
            int length = Hessian2Output.this._offset - startOffset - 3;
            ((Hessian2Output)Hessian2Output.this)._buffer[startOffset] = 66;
            ((Hessian2Output)Hessian2Output.this)._buffer[startOffset + 1] = (byte)(length >> 8);
            ((Hessian2Output)Hessian2Output.this)._buffer[startOffset + 2] = (byte)length;
            Hessian2Output.this.flushBuffer();
        }
    }
}

