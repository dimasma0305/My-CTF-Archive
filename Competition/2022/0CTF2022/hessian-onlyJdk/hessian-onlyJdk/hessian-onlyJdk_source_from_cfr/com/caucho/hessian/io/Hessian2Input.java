/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.Hessian2Constants;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.HessianRemote;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.HessianServiceException;
import com.caucho.hessian.io.SerializerFactory;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Node;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Hessian2Input
extends AbstractHessianInput
implements Hessian2Constants {
    private static final Logger log = Logger.getLogger(Hessian2Input.class.getName());
    private static final int END_OF_DATA = -2;
    private static Field _detailMessageField;
    private static final int SIZE = 1024;
    private static final int GAP = 16;
    protected SerializerFactory _defaultSerializerFactory;
    protected SerializerFactory _serializerFactory;
    private static boolean _isCloseStreamOnClose;
    protected ArrayList<Object> _refs = new ArrayList();
    protected ArrayList<ObjectDefinition> _classDefs = new ArrayList();
    protected ArrayList<String> _types = new ArrayList();
    private InputStream _is;
    private final byte[] _buffer = new byte[1024];
    private int _offset;
    private int _length;
    private String _method;
    private Throwable _replyFault;
    private StringBuilder _sbuf = new StringBuilder();
    private boolean _isLastChunk;
    private int _chunkLength;
    private HessianDebugInputStream _dIs;

    public Hessian2Input() {
        if (log.isLoggable(Level.FINEST)) {
            this._dIs = new HessianDebugInputStream(log, Level.FINEST);
        }
    }

    public Hessian2Input(InputStream is) {
        this();
        this.init(is);
    }

    @Override
    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    public SerializerFactory getSerializerFactory() {
        if (this._serializerFactory == this._defaultSerializerFactory) {
            this._serializerFactory = new SerializerFactory();
        }
        return this._serializerFactory;
    }

    protected final SerializerFactory findSerializerFactory() {
        SerializerFactory factory = this._serializerFactory;
        if (factory == null) {
            this._defaultSerializerFactory = factory = SerializerFactory.createDefault();
            this._serializerFactory = factory;
        }
        return factory;
    }

    public void setCloseStreamOnClose(boolean isClose) {
        _isCloseStreamOnClose = isClose;
    }

    public boolean isCloseStreamOnClose() {
        return _isCloseStreamOnClose;
    }

    @Override
    public String getMethod() {
        return this._method;
    }

    public Throwable getReplyFault() {
        return this._replyFault;
    }

    @Override
    public void init(InputStream is) {
        if (this._dIs != null) {
            this._dIs.initPacket(is);
            is = this._dIs;
        }
        this._is = is;
        this.reset();
    }

    public void initPacket(InputStream is) {
        if (this._dIs != null) {
            this._dIs.initPacket(is);
            is = this._dIs;
        }
        this._is = is;
        this.resetReferences();
    }

    @Override
    public int readCall() throws IOException {
        int tag = this.read();
        if (tag != 67) {
            throw this.error("expected hessian call ('C') at " + this.codeName(tag));
        }
        return 0;
    }

    public int readEnvelope() throws IOException {
        int tag = this.read();
        int version = 0;
        if (tag == 72) {
            int major = this.read();
            int minor = this.read();
            version = (major << 16) + minor;
            tag = this.read();
        }
        if (tag != 69) {
            throw this.error("expected hessian Envelope ('E') at " + this.codeName(tag));
        }
        return version;
    }

    public void completeEnvelope() throws IOException {
        int tag = this.read();
        if (tag != 90) {
            this.error("expected end of envelope at " + this.codeName(tag));
        }
    }

    @Override
    public String readMethod() throws IOException {
        this._method = this.readString();
        return this._method;
    }

    @Override
    public int readMethodArgLength() throws IOException {
        return this.readInt();
    }

    @Override
    public void startCall() throws IOException {
        this.readCall();
        this.readMethod();
    }

    public Object[] readArguments() throws IOException {
        int len = this.readInt();
        Object[] args = new Object[len];
        for (int i = 0; i < len; ++i) {
            args[i] = this.readObject();
        }
        return args;
    }

    @Override
    public void completeCall() throws IOException {
    }

    @Override
    public Object readReply(Class expectedClass) throws Throwable {
        int tag = this.read();
        if (tag == 82) {
            return this.readObject(expectedClass);
        }
        if (tag == 70) {
            HashMap map = (HashMap)this.readObject(HashMap.class);
            throw this.prepareFault(map);
        }
        StringBuilder sb = new StringBuilder();
        sb.append((char)tag);
        try {
            int ch;
            while ((ch = this.read()) >= 0) {
                sb.append((char)ch);
            }
        }
        catch (IOException e) {
            log.log(Level.FINE, e.toString(), e);
        }
        throw this.error("expected hessian reply at " + this.codeName(tag) + "\n" + sb);
    }

    @Override
    public void startReply() throws Throwable {
        this.readReply(Object.class);
    }

    private Throwable prepareFault(HashMap fault) throws IOException {
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

    @Override
    public void completeReply() throws IOException {
    }

    public void completeValueReply() throws IOException {
        int tag = this.read();
        if (tag != 90) {
            this.error("expected end of reply at " + this.codeName(tag));
        }
    }

    @Override
    public String readHeader() throws IOException {
        return null;
    }

    public int startMessage() throws IOException {
        int tag = this.read();
        if (tag != 112 && tag != 80) {
            throw this.error("expected Hessian message ('p') at " + this.codeName(tag));
        }
        int major = this.read();
        int minor = this.read();
        return (major << 16) + minor;
    }

    public void completeMessage() throws IOException {
        int tag = this.read();
        if (tag != 90) {
            this.error("expected end of message at " + this.codeName(tag));
        }
    }

    @Override
    public void readNull() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return;
            }
        }
        throw this.expect("null", tag);
    }

    @Override
    public boolean readBoolean() throws IOException {
        int tag = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
        switch (tag) {
            case 84: {
                return true;
            }
            case 70: {
                return false;
            }
            case 128: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 177: 
            case 178: 
            case 179: 
            case 180: 
            case 181: 
            case 182: 
            case 183: 
            case 184: 
            case 185: 
            case 186: 
            case 187: 
            case 188: 
            case 189: 
            case 190: 
            case 191: {
                return tag != 144;
            }
            case 200: {
                return this.read() != 0;
            }
            case 192: 
            case 193: 
            case 194: 
            case 195: 
            case 196: 
            case 197: 
            case 198: 
            case 199: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: 
            case 206: 
            case 207: {
                this.read();
                return true;
            }
            case 212: {
                return 256 * this.read() + this.read() != 0;
            }
            case 208: 
            case 209: 
            case 210: 
            case 211: 
            case 213: 
            case 214: 
            case 215: {
                this.read();
                this.read();
                return true;
            }
            case 73: {
                return this.parseInt() != 0;
            }
            case 216: 
            case 217: 
            case 218: 
            case 219: 
            case 220: 
            case 221: 
            case 222: 
            case 223: 
            case 224: 
            case 225: 
            case 226: 
            case 227: 
            case 228: 
            case 229: 
            case 230: 
            case 231: 
            case 232: 
            case 233: 
            case 234: 
            case 235: 
            case 236: 
            case 237: 
            case 238: 
            case 239: {
                return tag != 224;
            }
            case 248: {
                return this.read() != 0;
            }
            case 240: 
            case 241: 
            case 242: 
            case 243: 
            case 244: 
            case 245: 
            case 246: 
            case 247: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                this.read();
                return true;
            }
            case 60: {
                return 256 * this.read() + this.read() != 0;
            }
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 61: 
            case 62: 
            case 63: {
                this.read();
                this.read();
                return true;
            }
            case 89: {
                return 0x1000000L * (long)this.read() + 65536L * (long)this.read() + (long)(256 * this.read()) + (long)this.read() != 0L;
            }
            case 76: {
                return this.parseLong() != 0L;
            }
            case 91: {
                return false;
            }
            case 92: {
                return true;
            }
            case 93: {
                return this.read() != 0;
            }
            case 94: {
                return 256 * this.read() + this.read() != 0;
            }
            case 95: {
                int mills = this.parseInt();
                return mills != 0;
            }
            case 68: {
                return this.parseDouble() != 0.0;
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

    @Override
    public final int readInt() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return 0;
            }
            case 70: {
                return 0;
            }
            case 84: {
                return 1;
            }
            case 128: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 177: 
            case 178: 
            case 179: 
            case 180: 
            case 181: 
            case 182: 
            case 183: 
            case 184: 
            case 185: 
            case 186: 
            case 187: 
            case 188: 
            case 189: 
            case 190: 
            case 191: {
                return tag - 144;
            }
            case 192: 
            case 193: 
            case 194: 
            case 195: 
            case 196: 
            case 197: 
            case 198: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: 
            case 206: 
            case 207: {
                return (tag - 200 << 8) + this.read();
            }
            case 208: 
            case 209: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: {
                return (tag - 212 << 16) + 256 * this.read() + this.read();
            }
            case 73: 
            case 89: {
                return (this.read() << 24) + (this.read() << 16) + (this.read() << 8) + this.read();
            }
            case 216: 
            case 217: 
            case 218: 
            case 219: 
            case 220: 
            case 221: 
            case 222: 
            case 223: 
            case 224: 
            case 225: 
            case 226: 
            case 227: 
            case 228: 
            case 229: 
            case 230: 
            case 231: 
            case 232: 
            case 233: 
            case 234: 
            case 235: 
            case 236: 
            case 237: 
            case 238: 
            case 239: {
                return tag - 224;
            }
            case 240: 
            case 241: 
            case 242: 
            case 243: 
            case 244: 
            case 245: 
            case 246: 
            case 247: 
            case 248: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                return (tag - 248 << 8) + this.read();
            }
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: {
                return (tag - 60 << 16) + 256 * this.read() + this.read();
            }
            case 76: {
                return (int)this.parseLong();
            }
            case 91: {
                return 0;
            }
            case 92: {
                return 1;
            }
            case 93: {
                return (byte)(this._offset < this._length ? this._buffer[this._offset++] : this.read());
            }
            case 94: {
                return (short)(256 * this.read() + this.read());
            }
            case 95: {
                int mills = this.parseInt();
                return (int)(0.001 * (double)mills);
            }
            case 68: {
                return (int)this.parseDouble();
            }
        }
        throw this.expect("integer", tag);
    }

    @Override
    public long readLong() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return 0L;
            }
            case 70: {
                return 0L;
            }
            case 84: {
                return 1L;
            }
            case 128: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 177: 
            case 178: 
            case 179: 
            case 180: 
            case 181: 
            case 182: 
            case 183: 
            case 184: 
            case 185: 
            case 186: 
            case 187: 
            case 188: 
            case 189: 
            case 190: 
            case 191: {
                return tag - 144;
            }
            case 192: 
            case 193: 
            case 194: 
            case 195: 
            case 196: 
            case 197: 
            case 198: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: 
            case 206: 
            case 207: {
                return (tag - 200 << 8) + this.read();
            }
            case 208: 
            case 209: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: {
                return (tag - 212 << 16) + 256 * this.read() + this.read();
            }
            case 93: {
                return (byte)(this._offset < this._length ? this._buffer[this._offset++] : this.read());
            }
            case 94: {
                return (short)(256 * this.read() + this.read());
            }
            case 73: 
            case 89: {
                return this.parseInt();
            }
            case 216: 
            case 217: 
            case 218: 
            case 219: 
            case 220: 
            case 221: 
            case 222: 
            case 223: 
            case 224: 
            case 225: 
            case 226: 
            case 227: 
            case 228: 
            case 229: 
            case 230: 
            case 231: 
            case 232: 
            case 233: 
            case 234: 
            case 235: 
            case 236: 
            case 237: 
            case 238: 
            case 239: {
                return tag - 224;
            }
            case 240: 
            case 241: 
            case 242: 
            case 243: 
            case 244: 
            case 245: 
            case 246: 
            case 247: 
            case 248: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                return (tag - 248 << 8) + this.read();
            }
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: {
                return (tag - 60 << 16) + 256 * this.read() + this.read();
            }
            case 76: {
                return this.parseLong();
            }
            case 91: {
                return 0L;
            }
            case 92: {
                return 1L;
            }
            case 95: {
                int mills = this.parseInt();
                return (long)(0.001 * (double)mills);
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

    @Override
    public double readDouble() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return 0.0;
            }
            case 70: {
                return 0.0;
            }
            case 84: {
                return 1.0;
            }
            case 128: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 177: 
            case 178: 
            case 179: 
            case 180: 
            case 181: 
            case 182: 
            case 183: 
            case 184: 
            case 185: 
            case 186: 
            case 187: 
            case 188: 
            case 189: 
            case 190: 
            case 191: {
                return tag - 144;
            }
            case 192: 
            case 193: 
            case 194: 
            case 195: 
            case 196: 
            case 197: 
            case 198: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: 
            case 206: 
            case 207: {
                return (tag - 200 << 8) + this.read();
            }
            case 208: 
            case 209: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: {
                return (tag - 212 << 16) + 256 * this.read() + this.read();
            }
            case 73: 
            case 89: {
                return this.parseInt();
            }
            case 216: 
            case 217: 
            case 218: 
            case 219: 
            case 220: 
            case 221: 
            case 222: 
            case 223: 
            case 224: 
            case 225: 
            case 226: 
            case 227: 
            case 228: 
            case 229: 
            case 230: 
            case 231: 
            case 232: 
            case 233: 
            case 234: 
            case 235: 
            case 236: 
            case 237: 
            case 238: 
            case 239: {
                return tag - 224;
            }
            case 240: 
            case 241: 
            case 242: 
            case 243: 
            case 244: 
            case 245: 
            case 246: 
            case 247: 
            case 248: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                return (tag - 248 << 8) + this.read();
            }
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: {
                return (tag - 60 << 16) + 256 * this.read() + this.read();
            }
            case 76: {
                return this.parseLong();
            }
            case 91: {
                return 0.0;
            }
            case 92: {
                return 1.0;
            }
            case 93: {
                return (byte)(this._offset < this._length ? this._buffer[this._offset++] : this.read());
            }
            case 94: {
                return (short)(256 * this.read() + this.read());
            }
            case 95: {
                int mills = this.parseInt();
                return 0.001 * (double)mills;
            }
            case 68: {
                return this.parseDouble();
            }
        }
        throw this.expect("double", tag);
    }

    @Override
    public long readUTCDate() throws IOException {
        int tag = this.read();
        if (tag == 74) {
            return this.parseLong();
        }
        if (tag == 75) {
            return (long)this.parseInt() * 60000L;
        }
        throw this.expect("date", tag);
    }

    public int readChar() throws IOException {
        if (this._chunkLength > 0) {
            --this._chunkLength;
            if (this._chunkLength == 0 && this._isLastChunk) {
                this._chunkLength = -2;
            }
            int ch = this.parseUTF8Char();
            return ch;
        }
        if (this._chunkLength == -2) {
            this._chunkLength = 0;
            return -1;
        }
        int tag = this.read();
        switch (tag) {
            case 78: {
                return -1;
            }
            case 82: 
            case 83: {
                this._isLastChunk = tag == 83;
                this._chunkLength = (this.read() << 8) + this.read();
                --this._chunkLength;
                int value = this.parseUTF8Char();
                if (this._chunkLength == 0 && this._isLastChunk) {
                    this._chunkLength = -2;
                }
                return value;
            }
        }
        throw this.expect("char", tag);
    }

    public int readString(char[] buffer, int offset, int length) throws IOException {
        int tag;
        int readLength = 0;
        if (this._chunkLength == -2) {
            this._chunkLength = 0;
            return -1;
        }
        if (this._chunkLength == 0) {
            tag = this.read();
            switch (tag) {
                case 78: {
                    return -1;
                }
                case 82: 
                case 83: {
                    this._isLastChunk = tag == 83;
                    this._chunkLength = (this.read() << 8) + this.read();
                    break;
                }
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: {
                    this._isLastChunk = true;
                    this._chunkLength = tag - 0;
                    break;
                }
                case 48: 
                case 49: 
                case 50: 
                case 51: {
                    this._isLastChunk = true;
                    this._chunkLength = (tag - 48) * 256 + this.read();
                    break;
                }
                default: {
                    throw this.expect("string", tag);
                }
            }
        }
        block11: while (length > 0) {
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
                this._chunkLength = -2;
                return readLength;
            }
            tag = this.read();
            switch (tag) {
                case 82: 
                case 83: {
                    this._isLastChunk = tag == 83;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block11;
                }
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: {
                    this._isLastChunk = true;
                    this._chunkLength = tag - 0;
                    continue block11;
                }
                case 48: 
                case 49: 
                case 50: 
                case 51: {
                    this._isLastChunk = true;
                    this._chunkLength = (tag - 48) * 256 + this.read();
                    continue block11;
                }
            }
            throw this.expect("string", tag);
        }
        if (readLength == 0) {
            return -1;
        }
        if (this._chunkLength > 0 || !this._isLastChunk) {
            return readLength;
        }
        this._chunkLength = -2;
        return readLength;
    }

    @Override
    public String readString() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 84: {
                return "true";
            }
            case 70: {
                return "false";
            }
            case 128: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 177: 
            case 178: 
            case 179: 
            case 180: 
            case 181: 
            case 182: 
            case 183: 
            case 184: 
            case 185: 
            case 186: 
            case 187: 
            case 188: 
            case 189: 
            case 190: 
            case 191: {
                return String.valueOf(tag - 144);
            }
            case 192: 
            case 193: 
            case 194: 
            case 195: 
            case 196: 
            case 197: 
            case 198: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: 
            case 206: 
            case 207: {
                return String.valueOf((tag - 200 << 8) + this.read());
            }
            case 208: 
            case 209: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: {
                return String.valueOf((tag - 212 << 16) + 256 * this.read() + this.read());
            }
            case 73: 
            case 89: {
                return String.valueOf(this.parseInt());
            }
            case 216: 
            case 217: 
            case 218: 
            case 219: 
            case 220: 
            case 221: 
            case 222: 
            case 223: 
            case 224: 
            case 225: 
            case 226: 
            case 227: 
            case 228: 
            case 229: 
            case 230: 
            case 231: 
            case 232: 
            case 233: 
            case 234: 
            case 235: 
            case 236: 
            case 237: 
            case 238: 
            case 239: {
                return String.valueOf(tag - 224);
            }
            case 240: 
            case 241: 
            case 242: 
            case 243: 
            case 244: 
            case 245: 
            case 246: 
            case 247: 
            case 248: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                return String.valueOf((tag - 248 << 8) + this.read());
            }
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: {
                return String.valueOf((tag - 60 << 16) + 256 * this.read() + this.read());
            }
            case 76: {
                return String.valueOf(this.parseLong());
            }
            case 91: {
                return "0.0";
            }
            case 92: {
                return "1.0";
            }
            case 93: {
                return String.valueOf((byte)(this._offset < this._length ? this._buffer[this._offset++] : this.read()));
            }
            case 94: {
                return String.valueOf((short)(256 * this.read() + this.read()));
            }
            case 95: {
                int mills = this.parseInt();
                return String.valueOf(0.001 * (double)mills);
            }
            case 68: {
                return String.valueOf(this.parseDouble());
            }
            case 82: 
            case 83: {
                int ch;
                this._isLastChunk = tag == 83;
                this._chunkLength = (this.read() << 8) + this.read();
                this._sbuf.setLength(0);
                while ((ch = this.parseChar()) >= 0) {
                    this._sbuf.append((char)ch);
                }
                return this._sbuf.toString();
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: {
                int ch;
                this._isLastChunk = true;
                this._chunkLength = tag - 0;
                this._sbuf.setLength(0);
                while ((ch = this.parseChar()) >= 0) {
                    this._sbuf.append((char)ch);
                }
                return this._sbuf.toString();
            }
            case 48: 
            case 49: 
            case 50: 
            case 51: {
                int ch;
                this._isLastChunk = true;
                this._chunkLength = (tag - 48) * 256 + this.read();
                this._sbuf.setLength(0);
                while ((ch = this.parseChar()) >= 0) {
                    this._sbuf.append((char)ch);
                }
                return this._sbuf.toString();
            }
        }
        throw this.expect("string", tag);
    }

    @Override
    public byte[] readBytes() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 65: 
            case 66: {
                int data;
                this._isLastChunk = tag == 66;
                this._chunkLength = (this.read() << 8) + this.read();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((data = this.parseByte()) >= 0) {
                    bos.write(data);
                }
                return bos.toByteArray();
            }
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 46: 
            case 47: {
                int sublen;
                this._isLastChunk = true;
                this._chunkLength = tag - 32;
                byte[] buffer = new byte[this._chunkLength];
                for (int offset = 0; offset < this._chunkLength && (sublen = this.read(buffer, 0, this._chunkLength - offset)) > 0; offset += sublen) {
                }
                return buffer;
            }
            case 52: 
            case 53: 
            case 54: 
            case 55: {
                int sublen;
                this._isLastChunk = true;
                this._chunkLength = (tag - 52) * 256 + this.read();
                byte[] buffer = new byte[this._chunkLength];
                for (int offset = 0; offset < this._chunkLength && (sublen = this.read(buffer, 0, this._chunkLength - offset)) > 0; offset += sublen) {
                }
                return buffer;
            }
        }
        throw this.expect("bytes", tag);
    }

    public int readByte() throws IOException {
        if (this._chunkLength > 0) {
            --this._chunkLength;
            if (this._chunkLength == 0 && this._isLastChunk) {
                this._chunkLength = -2;
            }
            return this.read();
        }
        if (this._chunkLength == -2) {
            this._chunkLength = 0;
            return -1;
        }
        int tag = this.read();
        switch (tag) {
            case 78: {
                return -1;
            }
            case 65: 
            case 66: {
                this._isLastChunk = tag == 66;
                this._chunkLength = (this.read() << 8) + this.read();
                int value = this.parseByte();
                if (this._chunkLength == 0 && this._isLastChunk) {
                    this._chunkLength = -2;
                }
                return value;
            }
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 46: 
            case 47: {
                this._isLastChunk = true;
                this._chunkLength = tag - 32;
                int value = this.parseByte();
                if (this._chunkLength == 0) {
                    this._chunkLength = -2;
                }
                return value;
            }
            case 52: 
            case 53: 
            case 54: 
            case 55: {
                this._isLastChunk = true;
                this._chunkLength = (tag - 52) * 256 + this.read();
                int value = this.parseByte();
                if (this._chunkLength == 0) {
                    this._chunkLength = -2;
                }
                return value;
            }
        }
        throw this.expect("binary", tag);
    }

    public int readBytes(byte[] buffer, int offset, int length) throws IOException {
        int tag;
        int readLength = 0;
        if (this._chunkLength == -2) {
            this._chunkLength = 0;
            return -1;
        }
        if (this._chunkLength == 0) {
            tag = this.read();
            switch (tag) {
                case 78: {
                    return -1;
                }
                case 65: 
                case 66: {
                    this._isLastChunk = tag == 66;
                    this._chunkLength = (this.read() << 8) + this.read();
                    break;
                }
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: {
                    this._isLastChunk = true;
                    this._chunkLength = tag - 32;
                    break;
                }
                case 52: 
                case 53: 
                case 54: 
                case 55: {
                    this._isLastChunk = true;
                    this._chunkLength = (tag - 52) * 256 + this.read();
                    break;
                }
                default: {
                    throw this.expect("binary", tag);
                }
            }
        }
        block9: while (length > 0) {
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
                this._chunkLength = -2;
                return readLength;
            }
            tag = this.read();
            switch (tag) {
                case 65: 
                case 66: {
                    this._isLastChunk = tag == 66;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block9;
                }
            }
            throw this.expect("binary", tag);
        }
        if (readLength == 0) {
            return -1;
        }
        if (this._chunkLength > 0 || !this._isLastChunk) {
            return readLength;
        }
        this._chunkLength = -2;
        return readLength;
    }

    private HashMap readFault() throws IOException {
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        int code = this.read();
        while (code > 0 && code != 90) {
            --this._offset;
            Object key = this.readObject();
            Object value = this.readObject();
            if (key != null && value != null) {
                map.put(key, value);
            }
            code = this.read();
        }
        if (code != 90) {
            throw this.expect("fault", code);
        }
        return map;
    }

    @Override
    public Object readObject(Class cl) throws IOException {
        if (cl == null || cl == Object.class) {
            return this.readObject();
        }
        int tag = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 72: {
                Deserializer reader = this.findSerializerFactory().getDeserializer(cl);
                return reader.readMap(this);
            }
            case 77: {
                String type = this.readType();
                if ("".equals(type)) {
                    Deserializer reader = this.findSerializerFactory().getDeserializer(cl);
                    return reader.readMap(this);
                }
                Deserializer reader = this.findSerializerFactory().getObjectDeserializer(type, cl);
                return reader.readMap(this);
            }
            case 67: {
                this.readObjectDefinition(cl);
                return this.readObject(cl);
            }
            case 96: 
            case 97: 
            case 98: 
            case 99: 
            case 100: 
            case 101: 
            case 102: 
            case 103: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: {
                int ref = tag - 96;
                int size = this._classDefs.size();
                if (ref < 0 || size <= ref) {
                    throw new HessianProtocolException("'" + ref + "' is an unknown class definition");
                }
                ObjectDefinition def = this._classDefs.get(ref);
                return this.readObjectInstance(cl, def);
            }
            case 79: {
                int ref = this.readInt();
                int size = this._classDefs.size();
                if (ref < 0 || size <= ref) {
                    throw new HessianProtocolException("'" + ref + "' is an unknown class definition");
                }
                ObjectDefinition def = this._classDefs.get(ref);
                return this.readObjectInstance(cl, def);
            }
            case 85: {
                String type = this.readType();
                Deserializer reader = this.findSerializerFactory().getListDeserializer(type, cl);
                Object v = reader.readList(this, -1);
                return v;
            }
            case 86: {
                String type = this.readType();
                int length = this.readInt();
                Deserializer reader = this.findSerializerFactory().getListDeserializer(type, cl);
                Object v = reader.readLengthList(this, length);
                return v;
            }
            case 112: 
            case 113: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: {
                int length = tag - 112;
                String type = this.readType();
                Deserializer reader = this.findSerializerFactory().getListDeserializer(type, cl);
                Object v = reader.readLengthList(this, length);
                return v;
            }
            case 87: {
                Deserializer reader = this.findSerializerFactory().getListDeserializer(null, cl);
                Object v = reader.readList(this, -1);
                return v;
            }
            case 88: {
                int length = this.readInt();
                Deserializer reader = this.findSerializerFactory().getListDeserializer(null, cl);
                Object v = reader.readLengthList(this, length);
                return v;
            }
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 127: {
                int length = tag - 120;
                Deserializer reader = this.findSerializerFactory().getListDeserializer(null, cl);
                Object v = reader.readLengthList(this, length);
                return v;
            }
            case 81: {
                int ref = this.readInt();
                return this._refs.get(ref);
            }
        }
        if (tag >= 0) {
            --this._offset;
        }
        Object value = this.findSerializerFactory().getDeserializer(cl).readObject(this);
        return value;
    }

    @Override
    public Object readObject() throws IOException {
        int tag = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
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
            case 128: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 133: 
            case 134: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 177: 
            case 178: 
            case 179: 
            case 180: 
            case 181: 
            case 182: 
            case 183: 
            case 184: 
            case 185: 
            case 186: 
            case 187: 
            case 188: 
            case 189: 
            case 190: 
            case 191: {
                return tag - 144;
            }
            case 192: 
            case 193: 
            case 194: 
            case 195: 
            case 196: 
            case 197: 
            case 198: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: 
            case 206: 
            case 207: {
                return (tag - 200 << 8) + this.read();
            }
            case 208: 
            case 209: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: {
                return (tag - 212 << 16) + 256 * this.read() + this.read();
            }
            case 73: {
                return this.parseInt();
            }
            case 216: 
            case 217: 
            case 218: 
            case 219: 
            case 220: 
            case 221: 
            case 222: 
            case 223: 
            case 224: 
            case 225: 
            case 226: 
            case 227: 
            case 228: 
            case 229: 
            case 230: 
            case 231: 
            case 232: 
            case 233: 
            case 234: 
            case 235: 
            case 236: 
            case 237: 
            case 238: 
            case 239: {
                return (long)(tag - 224);
            }
            case 240: 
            case 241: 
            case 242: 
            case 243: 
            case 244: 
            case 245: 
            case 246: 
            case 247: 
            case 248: 
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                return (long)((tag - 248 << 8) + this.read());
            }
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: {
                return (long)((tag - 60 << 16) + 256 * this.read() + this.read());
            }
            case 89: {
                return (long)this.parseInt();
            }
            case 76: {
                return this.parseLong();
            }
            case 91: {
                return 0.0;
            }
            case 92: {
                return 1.0;
            }
            case 93: {
                return (double)((byte)this.read());
            }
            case 94: {
                return (double)((short)(256 * this.read() + this.read()));
            }
            case 95: {
                int mills = this.parseInt();
                return 0.001 * (double)mills;
            }
            case 68: {
                return this.parseDouble();
            }
            case 74: {
                return new Date(this.parseLong());
            }
            case 75: {
                return new Date((long)this.parseInt() * 60000L);
            }
            case 82: 
            case 83: {
                this._isLastChunk = tag == 83;
                this._chunkLength = (this.read() << 8) + this.read();
                this._sbuf.setLength(0);
                this.parseString(this._sbuf);
                return this._sbuf.toString();
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: {
                this._isLastChunk = true;
                this._chunkLength = tag - 0;
                this._sbuf.setLength(0);
                this.parseString(this._sbuf);
                return this._sbuf.toString();
            }
            case 48: 
            case 49: 
            case 50: 
            case 51: {
                this._isLastChunk = true;
                this._chunkLength = (tag - 48) * 256 + this.read();
                this._sbuf.setLength(0);
                this.parseString(this._sbuf);
                return this._sbuf.toString();
            }
            case 65: 
            case 66: {
                int data;
                this._isLastChunk = tag == 66;
                this._chunkLength = (this.read() << 8) + this.read();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((data = this.parseByte()) >= 0) {
                    bos.write(data);
                }
                return bos.toByteArray();
            }
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 46: 
            case 47: {
                this._isLastChunk = true;
                int len = tag - 32;
                this._chunkLength = 0;
                byte[] data = new byte[len];
                for (int i = 0; i < len; ++i) {
                    data[i] = (byte)this.read();
                }
                return data;
            }
            case 52: 
            case 53: 
            case 54: 
            case 55: {
                this._isLastChunk = true;
                int len = (tag - 52) * 256 + this.read();
                this._chunkLength = 0;
                byte[] buffer = new byte[len];
                for (int i = 0; i < len; ++i) {
                    buffer[i] = (byte)this.read();
                }
                return buffer;
            }
            case 85: {
                String type = this.readType();
                return this.findSerializerFactory().readList(this, -1, type);
            }
            case 87: {
                return this.findSerializerFactory().readList(this, -1, null);
            }
            case 86: {
                String type = this.readType();
                int length = this.readInt();
                Deserializer reader = this.findSerializerFactory().getListDeserializer(type, null);
                return reader.readLengthList(this, length);
            }
            case 88: {
                int length = this.readInt();
                Deserializer reader = this.findSerializerFactory().getListDeserializer(null, null);
                return reader.readLengthList(this, length);
            }
            case 112: 
            case 113: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: {
                String type = this.readType();
                int length = tag - 112;
                Deserializer reader = this.findSerializerFactory().getListDeserializer(type, null);
                return reader.readLengthList(this, length);
            }
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 127: {
                int length = tag - 120;
                Deserializer reader = this.findSerializerFactory().getListDeserializer(null, null);
                return reader.readLengthList(this, length);
            }
            case 72: {
                return this.findSerializerFactory().readMap(this, null);
            }
            case 77: {
                String type = this.readType();
                return this.findSerializerFactory().readMap(this, type);
            }
            case 67: {
                this.readObjectDefinition(null);
                return this.readObject();
            }
            case 96: 
            case 97: 
            case 98: 
            case 99: 
            case 100: 
            case 101: 
            case 102: 
            case 103: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: {
                int ref = tag - 96;
                if (this._classDefs.size() <= ref) {
                    throw this.error("No classes defined at reference '" + Integer.toHexString(tag) + "'");
                }
                ObjectDefinition def = this._classDefs.get(ref);
                return this.readObjectInstance(null, def);
            }
            case 79: {
                int ref = this.readInt();
                if (this._classDefs.size() <= ref) {
                    throw this.error("Illegal object reference #" + ref);
                }
                ObjectDefinition def = this._classDefs.get(ref);
                return this.readObjectInstance(null, def);
            }
            case 81: {
                int ref = this.readInt();
                return this._refs.get(ref);
            }
        }
        if (tag < 0) {
            throw new EOFException("readObject: unexpected end of file");
        }
        throw this.error("readObject: unknown code " + this.codeName(tag));
    }

    private void readObjectDefinition(Class<?> cl) throws IOException {
        String type = this.readString();
        int len = this.readInt();
        SerializerFactory factory = this.findSerializerFactory();
        Deserializer reader = factory.getObjectDeserializer(type, null);
        Object[] fields = reader.createFields(len);
        String[] fieldNames = new String[len];
        for (int i = 0; i < len; ++i) {
            String name = this.readString();
            fields[i] = reader.createField(name);
            fieldNames[i] = name;
        }
        ObjectDefinition def = new ObjectDefinition(type, reader, fields, fieldNames);
        this._classDefs.add(def);
    }

    private Object readObjectInstance(Class<?> cl, ObjectDefinition def) throws IOException {
        String type = def.getType();
        Deserializer reader = def.getReader();
        Object[] fields = def.getFields();
        SerializerFactory factory = this.findSerializerFactory();
        if (cl != reader.getType() && cl != null) {
            reader = factory.getObjectDeserializer(type, cl);
            return reader.readObject((AbstractHessianInput)this, def.getFieldNames());
        }
        return reader.readObject((AbstractHessianInput)this, fields);
    }

    @Override
    public Object readRemote() throws IOException {
        String type = this.readType();
        String url = this.readString();
        return this.resolveRemote(type, url);
    }

    @Override
    public Object readRef() throws IOException {
        int value = this.parseInt();
        return this._refs.get(value);
    }

    @Override
    public int readListStart() throws IOException {
        return this.read();
    }

    @Override
    public int readMapStart() throws IOException {
        return this.read();
    }

    @Override
    public boolean isEnd() throws IOException {
        int code;
        if (this._offset < this._length) {
            code = this._buffer[this._offset] & 0xFF;
        } else {
            code = this.read();
            if (code >= 0) {
                --this._offset;
            }
        }
        return code < 0 || code == 90;
    }

    @Override
    public void readEnd() throws IOException {
        int code;
        int n = code = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
        if (code == 90) {
            return;
        }
        if (code < 0) {
            throw this.error("unexpected end of file");
        }
        throw this.error("unknown code:" + this.codeName(code));
    }

    @Override
    public void readMapEnd() throws IOException {
        int code;
        int n = code = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
        if (code != 90) {
            throw this.error("expected end of map ('Z') at '" + this.codeName(code) + "'");
        }
    }

    @Override
    public void readListEnd() throws IOException {
        int code;
        int n = code = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
        if (code != 90) {
            throw this.error("expected end of list ('Z') at '" + this.codeName(code) + "'");
        }
    }

    @Override
    public int addRef(Object ref) {
        if (this._refs == null) {
            this._refs = new ArrayList();
        }
        this._refs.add(ref);
        return this._refs.size() - 1;
    }

    @Override
    public void setRef(int i, Object ref) {
        this._refs.set(i, ref);
    }

    @Override
    public void resetReferences() {
        this._refs.clear();
    }

    public void reset() {
        this.resetReferences();
        this._classDefs.clear();
        this._types.clear();
    }

    public void resetBuffer() {
        int offset = this._offset;
        this._offset = 0;
        int length = this._length;
        this._length = 0;
        if (length > 0 && offset != length) {
            throw new IllegalStateException("offset=" + offset + " length=" + length);
        }
    }

    public Object readStreamingObject() throws IOException {
        if (this._refs != null) {
            this._refs.clear();
        }
        return this.readObject();
    }

    public Object resolveRemote(String type, String url) throws IOException {
        HessianRemoteResolver resolver = this.getRemoteResolver();
        if (resolver != null) {
            return resolver.lookup(type, url);
        }
        return new HessianRemote(type, url);
    }

    @Override
    public String readType() throws IOException {
        int code = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
        --this._offset;
        switch (code) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 82: 
            case 83: {
                String type = this.readString();
                if (this._types == null) {
                    this._types = new ArrayList();
                }
                this._types.add(type);
                return type;
            }
        }
        int ref = this.readInt();
        if (this._types.size() <= ref) {
            throw new IndexOutOfBoundsException("type ref #" + ref + " is greater than the number of valid types (" + this._types.size() + ")");
        }
        return this._types.get(ref);
    }

    @Override
    public int readLength() throws IOException {
        throw new UnsupportedOperationException();
    }

    private int parseInt() throws IOException {
        int offset = this._offset;
        if (offset + 3 < this._length) {
            byte[] buffer = this._buffer;
            int b32 = buffer[offset + 0] & 0xFF;
            int b24 = buffer[offset + 1] & 0xFF;
            int b16 = buffer[offset + 2] & 0xFF;
            int b8 = buffer[offset + 3] & 0xFF;
            this._offset = offset + 4;
            return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
        }
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
        long bits = this.parseLong();
        return Double.longBitsToDouble(bits);
    }

    Node parseXML() throws IOException {
        throw new UnsupportedOperationException();
    }

    private void parseString(StringBuilder sbuf) throws IOException {
        block0: while (this._chunkLength > 0 || this.parseChunkLength()) {
            int length = this._chunkLength;
            this._chunkLength = 0;
            while (true) {
                if (length-- <= 0) continue block0;
                sbuf.append((char)this.parseUTF8Char());
            }
            break;
        }
        return;
    }

    private int parseChar() throws IOException {
        while (this._chunkLength <= 0) {
            if (this.parseChunkLength()) continue;
            return -1;
        }
        --this._chunkLength;
        return this.parseUTF8Char();
    }

    private boolean parseChunkLength() throws IOException {
        if (this._isLastChunk) {
            return false;
        }
        int code = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
        switch (code) {
            case 82: {
                this._isLastChunk = false;
                this._chunkLength = (this.read() << 8) + this.read();
                break;
            }
            case 83: {
                this._isLastChunk = true;
                this._chunkLength = (this.read() << 8) + this.read();
                break;
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: {
                this._isLastChunk = true;
                this._chunkLength = code - 0;
                break;
            }
            case 48: 
            case 49: 
            case 50: 
            case 51: {
                this._isLastChunk = true;
                this._chunkLength = (code - 48) * 256 + this.read();
                break;
            }
            default: {
                throw this.expect("string", code);
            }
        }
        return true;
    }

    private int parseUTF8Char() throws IOException {
        int ch;
        int n = ch = this._offset < this._length ? this._buffer[this._offset++] & 0xFF : this.read();
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
        block6: while (this._chunkLength <= 0) {
            if (this._isLastChunk) {
                return -1;
            }
            int code = this.read();
            switch (code) {
                case 65: {
                    this._isLastChunk = false;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block6;
                }
                case 66: {
                    this._isLastChunk = true;
                    this._chunkLength = (this.read() << 8) + this.read();
                    continue block6;
                }
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: {
                    this._isLastChunk = true;
                    this._chunkLength = code - 32;
                    continue block6;
                }
                case 52: 
                case 53: 
                case 54: 
                case 55: {
                    this._isLastChunk = true;
                    this._chunkLength = (code - 52) * 256 + this.read();
                    continue block6;
                }
            }
            throw this.expect("byte[]", code);
        }
        --this._chunkLength;
        return this.read();
    }

    @Override
    public InputStream readInputStream() throws IOException {
        int tag = this.read();
        switch (tag) {
            case 78: {
                return null;
            }
            case 65: 
            case 66: {
                this._isLastChunk = tag == 66;
                this._chunkLength = (this.read() << 8) + this.read();
                break;
            }
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 46: 
            case 47: {
                this._isLastChunk = true;
                this._chunkLength = tag - 32;
                break;
            }
            case 52: 
            case 53: 
            case 54: 
            case 55: {
                this._isLastChunk = true;
                this._chunkLength = (tag - 52) * 256 + this.read();
                break;
            }
            default: {
                throw this.expect("binary", tag);
            }
        }
        return new ReadInputStream();
    }

    int read(byte[] buffer, int offset, int length) throws IOException {
        int readLength = 0;
        while (length > 0) {
            block7: while (this._chunkLength <= 0) {
                if (this._isLastChunk) {
                    return readLength == 0 ? -1 : readLength;
                }
                int code = this.read();
                switch (code) {
                    case 65: {
                        this._isLastChunk = false;
                        this._chunkLength = (this.read() << 8) + this.read();
                        continue block7;
                    }
                    case 66: {
                        this._isLastChunk = true;
                        this._chunkLength = (this.read() << 8) + this.read();
                        continue block7;
                    }
                    case 32: 
                    case 33: 
                    case 34: 
                    case 35: 
                    case 36: 
                    case 37: 
                    case 38: 
                    case 39: 
                    case 40: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 45: 
                    case 46: 
                    case 47: {
                        this._isLastChunk = true;
                        this._chunkLength = code - 32;
                        continue block7;
                    }
                    case 52: 
                    case 53: 
                    case 54: 
                    case 55: {
                        this._isLastChunk = true;
                        this._chunkLength = (code - 52) * 256 + this.read();
                        continue block7;
                    }
                }
                throw this.expect("byte[]", code);
            }
            int sublen = this._chunkLength;
            if (length < sublen) {
                sublen = length;
            }
            if (this._length <= this._offset && !this.readBuffer()) {
                return -1;
            }
            if (this._length - this._offset < sublen) {
                sublen = this._length - this._offset;
            }
            System.arraycopy(this._buffer, this._offset, buffer, offset, sublen);
            this._offset += sublen;
            offset += sublen;
            readLength += sublen;
            length -= sublen;
            this._chunkLength -= sublen;
        }
        return readLength;
    }

    public final int read() throws IOException {
        if (this._length <= this._offset && !this.readBuffer()) {
            return -1;
        }
        return this._buffer[this._offset++] & 0xFF;
    }

    protected void unread() {
        if (this._offset <= 0) {
            throw new IllegalStateException();
        }
        --this._offset;
    }

    private final boolean readBuffer() throws IOException {
        byte[] buffer = this._buffer;
        int offset = this._offset;
        int length = this._length;
        if (offset < length) {
            System.arraycopy(buffer, offset, buffer, 0, length - offset);
            offset = length - offset;
        } else {
            offset = 0;
        }
        int len = this._is.read(buffer, offset, 1024 - offset);
        if (len <= 0) {
            this._length = offset;
            this._offset = 0;
            return offset > 0;
        }
        this._length = offset + len;
        this._offset = 0;
        return true;
    }

    @Override
    public Reader getReader() {
        return null;
    }

    protected IOException expect(String expect, int ch) throws IOException {
        if (ch < 0) {
            return this.error("expected " + expect + " at end of file");
        }
        --this._offset;
        try {
            int offset = this._offset;
            String context = this.buildDebugContext(this._buffer, 0, this._length, offset);
            Object obj = this.readObject();
            if (obj != null) {
                return this.error("expected " + expect + " at 0x" + Integer.toHexString(ch & 0xFF) + " " + obj.getClass().getName() + " (" + obj + ")" + "\n  " + context + "");
            }
            return this.error("expected " + expect + " at 0x" + Integer.toHexString(ch & 0xFF) + " null");
        }
        catch (Exception e) {
            log.log(Level.FINE, e.toString(), e);
            return this.error("expected " + expect + " at 0x" + Integer.toHexString(ch & 0xFF));
        }
    }

    private String buildDebugContext(byte[] buffer, int offset, int length, int errorOffset) {
        byte ch;
        int i;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (i = 0; i < errorOffset; ++i) {
            ch = buffer[offset + i];
            this.addDebugChar(sb, ch);
        }
        sb.append("] ");
        this.addDebugChar(sb, buffer[offset + errorOffset]);
        sb.append(" [");
        for (i = errorOffset + 1; i < length; ++i) {
            ch = buffer[offset + i];
            this.addDebugChar(sb, ch);
        }
        sb.append("]");
        return sb.toString();
    }

    private void addDebugChar(StringBuilder sb, int ch) {
        if (ch >= 32 && ch < 127) {
            sb.append((char)ch);
        } else if (ch == 10) {
            sb.append((char)ch);
        } else {
            sb.append(String.format("\\x%02x", ch & 0xFF));
        }
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

    public void free() {
        this.reset();
    }

    @Override
    public void close() throws IOException {
        InputStream is = this._is;
        this._is = null;
        if (_isCloseStreamOnClose && is != null) {
            is.close();
        }
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

    static final class ObjectDefinition {
        private final String _type;
        private final Deserializer _reader;
        private final Object[] _fields;
        private final String[] _fieldNames;

        ObjectDefinition(String type, Deserializer reader, Object[] fields, String[] fieldNames) {
            this._type = type;
            this._reader = reader;
            this._fields = fields;
            this._fieldNames = fieldNames;
        }

        String getType() {
            return this._type;
        }

        Deserializer getReader() {
            return this._reader;
        }

        Object[] getFields() {
            return this._fields;
        }

        String[] getFieldNames() {
            return this._fieldNames;
        }
    }

    class ReadInputStream
    extends InputStream {
        boolean _isClosed = false;

        ReadInputStream() {
        }

        public int read() throws IOException {
            if (this._isClosed) {
                return -1;
            }
            int ch = Hessian2Input.this.parseByte();
            if (ch < 0) {
                this._isClosed = true;
            }
            return ch;
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            if (this._isClosed) {
                return -1;
            }
            int len = Hessian2Input.this.read(buffer, offset, length);
            if (len < 0) {
                this._isClosed = true;
            }
            return len;
        }

        public void close() throws IOException {
            while (this.read() >= 0) {
            }
        }
    }
}

