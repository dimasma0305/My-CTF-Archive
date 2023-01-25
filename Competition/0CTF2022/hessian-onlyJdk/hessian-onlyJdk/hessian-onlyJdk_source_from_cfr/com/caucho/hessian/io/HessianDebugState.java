/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.Hessian2Constants;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class HessianDebugState
implements Hessian2Constants {
    private static final Logger log = Logger.getLogger(HessianDebugState.class.getName());
    private PrintWriter _dbg;
    private State _state;
    private ArrayList<State> _stateStack = new ArrayList();
    private ArrayList<ObjectDef> _objectDefList = new ArrayList();
    private ArrayList<String> _typeDefList = new ArrayList();
    private int _refId;
    private boolean _isNewline = true;
    private boolean _isObject = false;
    private int _column;
    private int _depth = 0;

    public HessianDebugState(PrintWriter dbg) {
        this._dbg = dbg;
        this._state = new InitialState();
    }

    public void startTop2() {
        this._state = new Top2State();
    }

    public void startData1() {
        this._state = new InitialState1();
    }

    public void startStreaming() {
        this._state = new StreamingState(new InitialState(), false);
    }

    public void next(int ch) throws IOException {
        this._state = this._state.next(ch);
    }

    void pushStack(State state) {
        this._stateStack.add(state);
    }

    State popStack() {
        return this._stateStack.remove(this._stateStack.size() - 1);
    }

    public void setDepth(int depth) {
        this._depth = depth;
    }

    public int getDepth() {
        return this._depth;
    }

    void println() {
        if (!this._isNewline) {
            this._dbg.println();
            this._dbg.flush();
        }
        this._isNewline = true;
        this._column = 0;
    }

    static boolean isString(int ch) {
        switch (ch) {
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
                return true;
            }
        }
        return false;
    }

    static boolean isInteger(int ch) {
        switch (ch) {
            case 73: 
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
            case 191: 
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
            case 207: 
            case 208: 
            case 209: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: {
                return true;
            }
        }
        return false;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ObjectDef {
        private String _type;
        private ArrayList<String> _fields;

        ObjectDef(String type, ArrayList<String> fields) {
            this._type = type;
            this._fields = fields;
        }

        String getType() {
            return this._type;
        }

        ArrayList<String> getFields() {
            return this._fields;
        }
    }

    class StreamingState
    extends State {
        private long _length;
        private int _metaLength;
        private boolean _isLast;
        private boolean _isFirst;
        private boolean _isLengthState;
        private State _childState;

        StreamingState(State next, boolean isLast) {
            super(next);
            this._isFirst = true;
            this._isLast = isLast;
            this._childState = new InitialState();
        }

        State next(int ch) {
            if (this._metaLength > 0) {
                this._length = 256L * this._length + (long)ch;
                --this._metaLength;
                if (this._metaLength == 0 && this._isFirst) {
                    if (this._isLast) {
                        this.println(-1, "--- packet-start(" + this._length + ")");
                    } else {
                        this.println(-1, "--- packet-start(fragment)");
                    }
                    this._isFirst = false;
                }
                return this;
            }
            if (this._length > 0L) {
                --this._length;
                this._childState = this._childState.next(ch);
                return this;
            }
            if (!this._isLengthState) {
                this._isLengthState = true;
                if (this._isLast) {
                    this.println(-1, "");
                    this.println(-1, "--- packet-end");
                    HessianDebugState.this._refId = 0;
                    this._isFirst = true;
                }
                this._isLast = (ch & 0x80) == 0;
                this._isLengthState = true;
            } else {
                this._isLengthState = false;
                this._length = ch & 0x7F;
                if (this._length == 126L) {
                    this._length = 0L;
                    this._metaLength = 2;
                } else if (this._length == 127L) {
                    this._length = 0L;
                    this._metaLength = 8;
                } else if (this._isFirst) {
                    if (this._isLast) {
                        this.println(-1, "--- packet-start(" + this._length + ")");
                    } else {
                        this.println(-1, "--- packet-start(fragment)");
                    }
                    this._isFirst = false;
                }
            }
            return this;
        }
    }

    class RemoteState
    extends State {
        private static final int TYPE = 0;
        private static final int VALUE = 1;
        private static final int END = 2;
        private int _state;
        private int _major;
        private int _minor;

        RemoteState(State next) {
            super(next);
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    this.println(-1, "remote");
                    if (ch == 116) {
                        this._state = 1;
                        return new StringState((State)this, 't', false);
                    }
                    this._state = 2;
                    return this.nextObject(ch);
                }
                case 1: {
                    this._state = 2;
                    return this._next.nextObject(ch);
                }
                case 2: {
                    return this._next.next(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class IndirectState
    extends State {
        IndirectState(State next) {
            super(next);
        }

        boolean isShift(Object object) {
            return this._next.isShift(object);
        }

        State shift(Object object) {
            return this._next.shift(object);
        }

        State next(int ch) {
            return this.nextObject(ch);
        }
    }

    class Fault2State
    extends State {
        Fault2State(State next) {
            super(next);
            this.println(-2, "Fault");
        }

        int depth() {
            return this._next.depth() + 2;
        }

        State next(int ch) {
            return this.nextObject(ch);
        }
    }

    class Reply2State
    extends State {
        Reply2State(State next) {
            super(next);
            this.println(-2, "Reply");
        }

        int depth() {
            return this._next.depth() + 2;
        }

        State next(int ch) {
            if (ch < 0) {
                this.println();
                return this._next;
            }
            return this.nextObject(ch);
        }
    }

    class ReplyState1
    extends State1 {
        private static final int MAJOR = 0;
        private static final int MINOR = 1;
        private static final int HEADER = 2;
        private static final int VALUE = 3;
        private static final int END = 4;
        private int _state;
        private int _major;
        private int _minor;

        ReplyState1(State next) {
            this._next = next;
        }

        int depth() {
            return this._next.depth() + 2;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    if (ch == 116 || ch == 83) {
                        return new RemoteState(this).next(ch);
                    }
                    this._major = ch;
                    this._state = 1;
                    return this;
                }
                case 1: {
                    this._minor = ch;
                    this._state = 2;
                    this.println(-2, "reply " + this._major + "." + this._minor);
                    return this;
                }
                case 2: {
                    if (ch == 72) {
                        this._state = 3;
                        return new StringState((State)this, 'H', true);
                    }
                    if (ch == 102) {
                        this.print("fault ");
                        HessianDebugState.this._isObject = false;
                        this._state = 4;
                        return new MapState(this, 0);
                    }
                    this._state = 4;
                    return this.nextObject(ch);
                }
                case 3: {
                    this._state = 2;
                    return this.nextObject(ch);
                }
                case 4: {
                    this.println();
                    if (ch == 122) {
                        return this._next;
                    }
                    return this._next.next(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class Call2State
    extends State {
        private static final int METHOD = 0;
        private static final int COUNT = 1;
        private static final int ARG = 2;
        private int _state;
        private int _i;
        private int _count;

        Call2State(State next) {
            super(next);
            this._state = 0;
        }

        int depth() {
            return this._next.depth() + 5;
        }

        boolean isShift(Object value) {
            return this._state != 2;
        }

        State shift(Object object) {
            if (this._state == 0) {
                this.println(-5, "Call " + object);
                this._state = 1;
                return this;
            }
            if (this._state == 1) {
                Integer count = (Integer)object;
                this._count = count;
                this._state = 2;
                if (this._count == 0) {
                    return this._next;
                }
                return this;
            }
            return this;
        }

        State next(int ch) {
            switch (this._state) {
                case 1: {
                    return this.nextObject(ch);
                }
                case 0: {
                    return this.nextObject(ch);
                }
                case 2: {
                    if (this._count <= this._i) {
                        this.println();
                        return this._next.next(ch);
                    }
                    this.println();
                    this.print(-3, this._i++ + ": ");
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class CallState1
    extends State1 {
        private static final int MAJOR = 0;
        private static final int MINOR = 1;
        private static final int HEADER = 2;
        private static final int METHOD = 3;
        private static final int VALUE = 4;
        private static final int ARG = 5;
        private int _state;
        private int _major;
        private int _minor;

        CallState1(State next) {
            super(next);
        }

        int depth() {
            return this._next.depth() + 2;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    this._major = ch;
                    this._state = 1;
                    return this;
                }
                case 1: {
                    this._minor = ch;
                    this._state = 2;
                    this.println(-2, "call " + this._major + "." + this._minor);
                    return this;
                }
                case 2: {
                    if (ch == 72) {
                        this.println();
                        this.print("header ");
                        HessianDebugState.this._isObject = false;
                        this._state = 4;
                        return new StringState((State)this, 'H', true);
                    }
                    if (ch == 109) {
                        this.println();
                        this.print("method ");
                        HessianDebugState.this._isObject = false;
                        this._state = 5;
                        return new StringState((State)this, 'm', true);
                    }
                    this.println((char)ch + ": unexpected char");
                    return HessianDebugState.this.popStack();
                }
                case 4: {
                    this.print(" => ");
                    HessianDebugState.this._isObject = false;
                    this._state = 2;
                    return this.nextObject(ch);
                }
                case 5: {
                    if (ch == 122) {
                        this.println();
                        return this._next;
                    }
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class Hessian2State
    extends State {
        private static final int MAJOR = 0;
        private static final int MINOR = 1;
        private int _state;
        private int _major;
        private int _minor;

        Hessian2State(State next) {
            super(next);
        }

        int depth() {
            return this._next.depth() + 2;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    this._major = ch;
                    this._state = 1;
                    return this;
                }
                case 1: {
                    this._minor = ch;
                    this.println(-2, "Hessian " + this._major + "." + this._minor);
                    return this._next;
                }
            }
            throw new IllegalStateException();
        }
    }

    class CompactListState
    extends State {
        private static final int TYPE = 0;
        private static final int LENGTH = 1;
        private static final int VALUE = 2;
        private int _refId;
        private boolean _isTyped;
        private boolean _isLength;
        private int _state;
        private int _length;
        private int _count;
        private int _valueDepth;

        CompactListState(State next, int refId, boolean isTyped) {
            super(next);
            this._isTyped = isTyped;
            this._refId = refId;
            this._state = isTyped ? 0 : 1;
        }

        CompactListState(State next, int refId, boolean isTyped, int length) {
            super(next);
            this._isTyped = isTyped;
            this._refId = refId;
            this._length = length;
            this._isLength = true;
            if (isTyped) {
                this._state = 0;
            } else {
                this.printObject("list (#" + this._refId + ")");
                this._state = 2;
            }
        }

        boolean isShift(Object value) {
            return this._state == 0 || this._state == 1;
        }

        State shift(Object object) {
            if (this._state == 0) {
                Object type = object;
                if (object instanceof Integer) {
                    int index = (Integer)object;
                    type = index >= 0 && index < HessianDebugState.this._typeDefList.size() ? HessianDebugState.this._typeDefList.get(index) : "type-unknown(" + index + ")";
                } else if (object instanceof String) {
                    HessianDebugState.this._typeDefList.add((String)object);
                }
                this.printObject("list " + type + " (#" + this._refId + ")");
                if (this._isLength) {
                    this._state = 2;
                    if (this._length == 0) {
                        return this._next;
                    }
                } else {
                    this._state = 1;
                }
                return this;
            }
            if (this._state == 1) {
                this._length = (Integer)object;
                if (!this._isTyped) {
                    this.printObject("list (#" + this._refId + ")");
                }
                this._state = 2;
                if (this._length == 0) {
                    return this._next;
                }
                return this;
            }
            return this;
        }

        int depth() {
            if (this._state <= 1) {
                return this._next.depth();
            }
            if (this._state == 2) {
                return this._valueDepth;
            }
            return this._next.depth() + 2;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    return this.nextObject(ch);
                }
                case 1: {
                    return this.nextObject(ch);
                }
                case 2: {
                    if (this._length <= this._count) {
                        return this._next.next(ch);
                    }
                    this._valueDepth = this._next.depth() + 2;
                    this.println();
                    this.printObject(this._count++ + ": ");
                    this._valueDepth = HessianDebugState.this._column;
                    HessianDebugState.this._isObject = false;
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class ListState
    extends State {
        private static final int TYPE = 0;
        private static final int LENGTH = 1;
        private static final int VALUE = 2;
        private int _refId;
        private int _state;
        private int _count;
        private int _valueDepth;

        ListState(State next, int refId, boolean isType) {
            super(next);
            this._refId = refId;
            if (isType) {
                this._state = 0;
            } else {
                this.printObject("list (#" + this._refId + ")");
                this._state = 2;
            }
        }

        boolean isShift(Object value) {
            return this._state == 0 || this._state == 1;
        }

        State shift(Object object) {
            if (this._state == 0) {
                Object type = object;
                if (type instanceof String) {
                    HessianDebugState.this._typeDefList.add((String)type);
                } else if (object instanceof Integer) {
                    int index = (Integer)object;
                    type = index >= 0 && index < HessianDebugState.this._typeDefList.size() ? HessianDebugState.this._typeDefList.get(index) : "type-unknown(" + index + ")";
                }
                this.printObject("list " + type + "(#" + this._refId + ")");
                this._state = 2;
                return this;
            }
            if (this._state == 1) {
                this._state = 2;
                return this;
            }
            return this;
        }

        int depth() {
            if (this._state <= 1) {
                return this._next.depth();
            }
            if (this._state == 2) {
                return this._valueDepth;
            }
            return this._next.depth() + 2;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    return this.nextObject(ch);
                }
                case 2: {
                    if (ch == 90) {
                        if (this._count > 0) {
                            this.println();
                        }
                        return this._next;
                    }
                    this._valueDepth = this._next.depth() + 2;
                    this.println();
                    this.printObject(this._count++ + ": ");
                    this._valueDepth = HessianDebugState.this._column;
                    HessianDebugState.this._isObject = false;
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class ListState1
    extends State1 {
        private static final int TYPE = 0;
        private static final int LENGTH = 1;
        private static final int VALUE = 2;
        private int _refId;
        private int _state;
        private int _count;
        private int _valueDepth;

        ListState1(State next, int refId) {
            super(next);
            this._refId = refId;
            this._state = 0;
        }

        boolean isShift(Object value) {
            return this._state == 0 || this._state == 1;
        }

        State shift(Object object) {
            if (this._state == 0) {
                Object type = object;
                if (type instanceof String) {
                    HessianDebugState.this._typeDefList.add((String)type);
                } else if (object instanceof Integer) {
                    int index = (Integer)object;
                    type = index >= 0 && index < HessianDebugState.this._typeDefList.size() ? HessianDebugState.this._typeDefList.get(index) : "type-unknown(" + index + ")";
                }
                this.printObject("list " + type + "(#" + this._refId + ")");
                this._state = 2;
                return this;
            }
            if (this._state == 1) {
                this._state = 2;
                return this;
            }
            return this;
        }

        int depth() {
            if (this._state <= 1) {
                return this._next.depth();
            }
            if (this._state == 2) {
                return this._valueDepth;
            }
            return this._next.depth() + 2;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    if (ch == 122) {
                        this.printObject("list (#" + this._refId + ")");
                        return this._next;
                    }
                    if (ch == 116) {
                        return new StringState((State)this, 't', true);
                    }
                    this.printObject("list (#" + this._refId + ")");
                    this.printObject("  " + this._count++ + ": ");
                    this._valueDepth = HessianDebugState.this._column;
                    HessianDebugState.this._isObject = false;
                    this._state = 2;
                    return this.nextObject(ch);
                }
                case 2: {
                    if (ch == 122) {
                        if (this._count > 0) {
                            this.println();
                        }
                        return this._next;
                    }
                    this._valueDepth = this._next.depth() + 2;
                    this.println();
                    this.printObject(this._count++ + ": ");
                    this._valueDepth = HessianDebugState.this._column;
                    HessianDebugState.this._isObject = false;
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class ObjectState
    extends State {
        private static final int TYPE = 0;
        private static final int FIELD = 1;
        private int _refId;
        private int _state;
        private ObjectDef _def;
        private int _count;
        private int _fieldDepth;

        ObjectState(State next, int refId) {
            super(next);
            this._refId = refId;
            this._state = 0;
        }

        ObjectState(State next, int refId, int def) {
            super(next);
            this._refId = refId;
            this._state = 1;
            if (def < 0 || HessianDebugState.this._objectDefList.size() <= def) {
                log.warning(this + " " + def + " is an unknown object type");
                this.println(this + " object unknown  (#" + this._refId + ")");
            }
            this._def = (ObjectDef)HessianDebugState.this._objectDefList.get(def);
            if (HessianDebugState.this._isObject) {
                this.println();
            }
            this.println("object " + this._def.getType() + " (#" + this._refId + ")");
        }

        boolean isShift(Object value) {
            return this._state == 0;
        }

        State shift(Object object) {
            if (this._state == 0) {
                int def = (Integer)object;
                this._def = (ObjectDef)HessianDebugState.this._objectDefList.get(def);
                this.println("object " + this._def.getType() + " (#" + this._refId + ")");
                this._state = 1;
                if (this._def.getFields().size() == 0) {
                    return this._next;
                }
            }
            return this;
        }

        int depth() {
            if (this._state <= 0) {
                return this._next.depth();
            }
            return this._fieldDepth;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    return this.nextObject(ch);
                }
                case 1: {
                    if (this._def.getFields().size() <= this._count) {
                        return this._next.next(ch);
                    }
                    this._fieldDepth = this._next.depth() + 2;
                    this.println();
                    this.print(this._def.getFields().get(this._count++) + ": ");
                    this._fieldDepth = HessianDebugState.this._column;
                    HessianDebugState.this._isObject = false;
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class ObjectDefState
    extends State {
        private static final int TYPE = 1;
        private static final int COUNT = 2;
        private static final int FIELD = 3;
        private static final int COMPLETE = 4;
        private int _state;
        private int _count;
        private String _type;
        private ArrayList<String> _fields;

        ObjectDefState(State next) {
            super(next);
            this._fields = new ArrayList();
            this._state = 1;
        }

        boolean isShift(Object value) {
            return true;
        }

        State shift(Object object) {
            if (this._state == 1) {
                this._type = (String)object;
                this.print("/* defun " + this._type + " [");
                HessianDebugState.this._objectDefList.add(new ObjectDef(this._type, this._fields));
                this._state = 2;
            } else if (this._state == 2) {
                this._count = (Integer)object;
                this._state = 3;
            } else if (this._state == 3) {
                String field = (String)object;
                --this._count;
                this._fields.add(field);
                if (this._fields.size() == 1) {
                    this.print(field);
                } else {
                    this.print(", " + field);
                }
            } else {
                throw new UnsupportedOperationException();
            }
            return this;
        }

        int depth() {
            if (this._state <= 1) {
                return this._next.depth();
            }
            return this._next.depth() + 2;
        }

        State next(int ch) {
            switch (this._state) {
                case 1: {
                    return this.nextObject(ch);
                }
                case 2: {
                    return this.nextObject(ch);
                }
                case 3: {
                    if (this._count == 0) {
                        this.println("] */");
                        this._next.printIndent(0);
                        return this._next.nextObject(ch);
                    }
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class MapState1
    extends State1 {
        private static final int TYPE = 0;
        private static final int KEY = 1;
        private static final int VALUE = 2;
        private int _refId;
        private int _state;
        private int _valueDepth;
        private boolean _hasData;

        MapState1(State next, int refId) {
            super(next);
            this._refId = refId;
            this._state = 0;
        }

        MapState1(State next, int refId, boolean isType) {
            super(next);
            this._refId = refId;
            if (isType) {
                this._state = 0;
            } else {
                this.printObject("map (#" + this._refId + ")");
                this._state = 2;
            }
        }

        boolean isShift(Object value) {
            return this._state == 0;
        }

        State shift(Object type) {
            if (this._state == 0) {
                int iValue;
                if (type instanceof String) {
                    HessianDebugState.this._typeDefList.add((String)type);
                } else if (type instanceof Integer && (iValue = ((Integer)type).intValue()) >= 0 && iValue < HessianDebugState.this._typeDefList.size()) {
                    type = HessianDebugState.this._typeDefList.get(iValue);
                }
                this.printObject("map " + type + " (#" + this._refId + ")");
                this._state = 2;
                return this;
            }
            throw new IllegalStateException();
        }

        int depth() {
            if (this._state == 0) {
                return this._next.depth();
            }
            if (this._state == 1) {
                return this._next.depth() + 2;
            }
            return this._valueDepth;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    if (ch == 116) {
                        return new StringState((State)this, 't', true);
                    }
                    if (ch == 122) {
                        this.println("map (#" + this._refId + ")");
                        return this._next;
                    }
                    this.println("map (#" + this._refId + ")");
                    this._hasData = true;
                    this._state = 1;
                    return this.nextObject(ch);
                }
                case 2: {
                    if (ch == 122) {
                        if (this._hasData) {
                            this.println();
                        }
                        return this._next;
                    }
                    if (this._hasData) {
                        this.println();
                    }
                    this._hasData = true;
                    this._state = 1;
                    return this.nextObject(ch);
                }
                case 1: {
                    this.print(" => ");
                    HessianDebugState.this._isObject = false;
                    this._valueDepth = HessianDebugState.this._column;
                    this._state = 2;
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class MapState
    extends State {
        private static final int TYPE = 0;
        private static final int KEY = 1;
        private static final int VALUE = 2;
        private int _refId;
        private int _state;
        private int _valueDepth;
        private boolean _hasData;

        MapState(State next, int refId) {
            super(next);
            this._refId = refId;
            this._state = 0;
        }

        MapState(State next, int refId, boolean isType) {
            super(next);
            this._refId = refId;
            if (isType) {
                this._state = 0;
            } else {
                this.printObject("map (#" + this._refId + ")");
                this._state = 2;
            }
        }

        boolean isShift(Object value) {
            return this._state == 0;
        }

        State shift(Object type) {
            if (this._state == 0) {
                int iValue;
                if (type instanceof String) {
                    HessianDebugState.this._typeDefList.add((String)type);
                } else if (type instanceof Integer && (iValue = ((Integer)type).intValue()) >= 0 && iValue < HessianDebugState.this._typeDefList.size()) {
                    type = HessianDebugState.this._typeDefList.get(iValue);
                }
                this.printObject("map " + type + " (#" + this._refId + ")");
                this._state = 2;
                return this;
            }
            this.printObject(this + " unknown shift state= " + this._state + " type=" + type);
            return this;
        }

        int depth() {
            if (this._state == 0) {
                return this._next.depth();
            }
            if (this._state == 1) {
                return this._next.depth() + 2;
            }
            return this._valueDepth;
        }

        State next(int ch) {
            switch (this._state) {
                case 0: {
                    return this.nextObject(ch);
                }
                case 2: {
                    if (ch == 90) {
                        if (this._hasData) {
                            this.println();
                        }
                        return this._next;
                    }
                    if (this._hasData) {
                        this.println();
                    }
                    this._hasData = true;
                    this._state = 1;
                    return this.nextObject(ch);
                }
                case 1: {
                    this.print(" => ");
                    HessianDebugState.this._isObject = false;
                    this._valueDepth = HessianDebugState.this._column;
                    this._state = 2;
                    return this.nextObject(ch);
                }
            }
            throw new IllegalStateException();
        }
    }

    class BinaryState
    extends State {
        char _typeCode;
        int _totalLength;
        int _lengthIndex;
        int _length;
        boolean _isLastChunk;

        BinaryState(State next, char typeCode, boolean isLastChunk) {
            super(next);
            this._typeCode = typeCode;
            this._isLastChunk = isLastChunk;
        }

        BinaryState(State next, char typeCode, int length) {
            super(next);
            this._typeCode = typeCode;
            this._isLastChunk = true;
            this._length = length;
            this._lengthIndex = 2;
        }

        BinaryState(State next, char typeCode, int length, boolean isLastChunk) {
            super(next);
            this._typeCode = typeCode;
            this._isLastChunk = isLastChunk;
            this._length = length;
            this._lengthIndex = 1;
        }

        State next(int ch) {
            if (this._lengthIndex < 2) {
                this._length = 256 * this._length + (ch & 0xFF);
                if (++this._lengthIndex == 2 && this._length == 0 && this._isLastChunk) {
                    String value = "binary(" + this._totalLength + ")";
                    if (this._next.isShift(value)) {
                        return this._next.shift(value);
                    }
                    this.printObject(value);
                    return this._next;
                }
                return this;
            }
            if (this._length == 0) {
                if (ch == 98 || ch == 65) {
                    this._isLastChunk = false;
                    this._lengthIndex = 0;
                    return this;
                }
                if (ch == 66) {
                    this._isLastChunk = true;
                    this._lengthIndex = 0;
                    return this;
                }
                if (ch == 32) {
                    String value = "binary(" + this._totalLength + ")";
                    if (this._next.isShift(value)) {
                        return this._next.shift(value);
                    }
                    this.printObject(value);
                    return this._next;
                }
                if (32 <= ch && ch < 48) {
                    this._isLastChunk = true;
                    this._lengthIndex = 2;
                    this._length = (ch & 0xFF) - 32;
                    return this;
                }
                this.println(this + " 0x" + Integer.toHexString(ch) + " " + String.valueOf((char)ch) + ": unexpected character");
                return this._next;
            }
            --this._length;
            ++this._totalLength;
            if (this._length == 0 && this._isLastChunk) {
                String value = "binary(" + this._totalLength + ")";
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                this.printObject(value);
                return this._next;
            }
            return this;
        }
    }

    class StringState
    extends State {
        private static final int TOP = 0;
        private static final int UTF_2_1 = 1;
        private static final int UTF_3_1 = 2;
        private static final int UTF_3_2 = 3;
        char _typeCode;
        StringBuilder _value;
        int _lengthIndex;
        int _length;
        boolean _isLastChunk;
        int _utfState;
        char _ch;

        StringState(State next, char typeCode, boolean isLastChunk) {
            super(next);
            this._value = new StringBuilder();
            this._typeCode = typeCode;
            this._isLastChunk = isLastChunk;
        }

        StringState(State next, char typeCode, int length) {
            super(next);
            this._value = new StringBuilder();
            this._typeCode = typeCode;
            this._isLastChunk = true;
            this._length = length;
            this._lengthIndex = 2;
        }

        StringState(State next, char typeCode, int length, boolean isLastChunk) {
            super(next);
            this._value = new StringBuilder();
            this._typeCode = typeCode;
            this._isLastChunk = isLastChunk;
            this._length = length;
            this._lengthIndex = 1;
        }

        State next(int ch) {
            if (this._lengthIndex < 2) {
                this._length = 256 * this._length + (ch & 0xFF);
                if (++this._lengthIndex == 2 && this._length == 0 && this._isLastChunk) {
                    if (this._next.isShift(this._value.toString())) {
                        return this._next.shift(this._value.toString());
                    }
                    this.printObject("\"" + this._value + "\"");
                    return this._next;
                }
                return this;
            }
            if (this._length == 0) {
                if (ch == 115 || ch == 120) {
                    this._isLastChunk = false;
                    this._lengthIndex = 0;
                    return this;
                }
                if (ch == 83 || ch == 88) {
                    this._isLastChunk = true;
                    this._lengthIndex = 0;
                    return this;
                }
                if (ch == 0) {
                    if (this._next.isShift(this._value.toString())) {
                        return this._next.shift(this._value.toString());
                    }
                    this.printObject("\"" + this._value + "\"");
                    return this._next;
                }
                if (0 <= ch && ch < 32) {
                    this._isLastChunk = true;
                    this._lengthIndex = 2;
                    this._length = ch & 0xFF;
                    return this;
                }
                if (48 <= ch && ch < 52) {
                    this._isLastChunk = true;
                    this._lengthIndex = 1;
                    this._length = ch - 48;
                    return this;
                }
                this.println(this + " " + String.valueOf((char)ch) + ": unexpected character");
                return this._next;
            }
            switch (this._utfState) {
                case 0: {
                    if (ch < 128) {
                        --this._length;
                        this._value.append((char)ch);
                        break;
                    }
                    if (ch < 224) {
                        this._ch = (char)((ch & 0x1F) << 6);
                        this._utfState = 1;
                        break;
                    }
                    this._ch = (char)((ch & 0xF) << 12);
                    this._utfState = 2;
                    break;
                }
                case 1: 
                case 3: {
                    this._ch = (char)(this._ch + (ch & 0x3F));
                    this._value.append(this._ch);
                    --this._length;
                    this._utfState = 0;
                    break;
                }
                case 2: {
                    this._ch = (char)(this._ch + (char)((ch & 0x3F) << 6));
                    this._utfState = 3;
                }
            }
            if (this._length == 0 && this._isLastChunk) {
                if (this._next.isShift(this._value.toString())) {
                    return this._next.shift(this._value.toString());
                }
                this.printObject("\"" + this._value + "\"");
                return this._next;
            }
            return this;
        }
    }

    class MillsState
    extends State {
        int _length;
        int _value;

        MillsState(State next) {
            super(next);
        }

        State next(int ch) {
            this._value = 256 * this._value + (ch & 0xFF);
            if (++this._length == 4) {
                Double value = 0.001 * (double)this._value;
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                this.printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    class DoubleState
    extends State {
        int _length;
        long _value;

        DoubleState(State next) {
            super(next);
        }

        State next(int ch) {
            this._value = 256L * this._value + (long)(ch & 0xFF);
            if (++this._length == 8) {
                Double value = Double.longBitsToDouble(this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                this.printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    class DateState
    extends State {
        int _length;
        long _value;
        boolean _isMinute;

        DateState(State next) {
            super(next);
        }

        DateState(State next, boolean isMinute) {
            super(next);
            this._length = 4;
            this._isMinute = isMinute;
        }

        State next(int ch) {
            this._value = 256L * this._value + (long)(ch & 0xFF);
            if (++this._length == 8) {
                Date value = this._isMinute ? new Date(this._value * 60000L) : new Date(this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                this.printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    class RefState1
    extends State {
        String _typeCode;

        RefState1(State next) {
            super(next);
        }

        boolean isShift(Object o) {
            return true;
        }

        State shift(Object o) {
            this.println("ref #" + o);
            return this._next;
        }

        State next(int ch) {
            return this.nextObject(ch);
        }
    }

    class RefState
    extends State {
        String _typeCode;
        int _length;
        int _value;

        RefState(State next) {
            super(next);
        }

        RefState(State next, String typeCode) {
            super(next);
            this._typeCode = typeCode;
        }

        RefState(State next, String typeCode, int value, int length) {
            super(next);
            this._typeCode = typeCode;
            this._value = value;
            this._length = length;
        }

        boolean isShift(Object o) {
            return true;
        }

        State shift(Object o) {
            this.println("ref #" + o);
            return this._next;
        }

        State next(int ch) {
            return this.nextObject(ch);
        }
    }

    class DoubleIntegerState
    extends State {
        int _length;
        int _value;
        boolean _isFirst;

        DoubleIntegerState(State next, int length) {
            super(next);
            this._isFirst = true;
            this._length = length;
        }

        State next(int ch) {
            this._value = this._isFirst ? (int)((byte)ch) : 256 * this._value + (ch & 0xFF);
            this._isFirst = false;
            if (++this._length == 4) {
                Double value = new Double(this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                this.printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    class LongState
    extends State {
        String _typeCode;
        int _length;
        long _value;

        LongState(State next, String typeCode) {
            super(next);
            this._typeCode = typeCode;
        }

        LongState(State next, String typeCode, long value, int length) {
            super(next);
            this._typeCode = typeCode;
            this._value = value;
            this._length = length;
        }

        State next(int ch) {
            this._value = 256L * this._value + (long)(ch & 0xFF);
            if (++this._length == 8) {
                Long value = new Long(this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                this.printObject(value.toString() + "L");
                return this._next;
            }
            return this;
        }
    }

    class IntegerState
    extends State {
        String _typeCode;
        int _length;
        int _value;

        IntegerState(State next, String typeCode) {
            super(next);
            this._typeCode = typeCode;
        }

        IntegerState(State next, String typeCode, int value, int length) {
            super(next);
            this._typeCode = typeCode;
            this._value = value;
            this._length = length;
        }

        State next(int ch) {
            this._value = 256 * this._value + (ch & 0xFF);
            if (++this._length == 4) {
                Integer value = new Integer(this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                this.printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    class Top2State
    extends State {
        Top2State() {
        }

        State next(int ch) {
            this.println();
            if (ch == 82) {
                return new Reply2State(this);
            }
            if (ch == 70) {
                return new Fault2State(this);
            }
            if (ch == 67) {
                return new Call2State(this);
            }
            if (ch == 72) {
                return new Hessian2State(this);
            }
            if (ch == 114) {
                return new ReplyState1(this);
            }
            if (ch == 99) {
                return new CallState1(this);
            }
            return this.nextObject(ch);
        }
    }

    class Top1State
    extends State1 {
        Top1State() {
        }

        State next(int ch) {
            this.println();
            if (ch == 114) {
                return new ReplyState1(this);
            }
            if (ch == 99) {
                return new CallState1(this);
            }
            return this.nextObject(ch);
        }
    }

    class InitialState1
    extends State1 {
        InitialState1() {
        }

        State next(int ch) {
            return this.nextObject(ch);
        }
    }

    class InitialState
    extends State {
        InitialState() {
        }

        State next(int ch) {
            return this.nextObject(ch);
        }
    }

    abstract class State1
    extends State {
        State1() {
        }

        State1(State next) {
            super(next);
        }

        protected State nextObject(int ch) {
            switch (ch) {
                case -1: {
                    this.println();
                    return this;
                }
                case 78: {
                    if (this.isShift(null)) {
                        return this.shift(null);
                    }
                    this.printObject("null");
                    return this;
                }
                case 84: {
                    if (this.isShift(Boolean.TRUE)) {
                        return this.shift(Boolean.TRUE);
                    }
                    this.printObject("true");
                    return this;
                }
                case 70: {
                    if (this.isShift(Boolean.FALSE)) {
                        return this.shift(Boolean.FALSE);
                    }
                    this.printObject("false");
                    return this;
                }
                case 73: {
                    return new IntegerState(this, "int");
                }
                case 76: {
                    return new LongState(this, "long");
                }
                case 68: {
                    return new DoubleState(this);
                }
                case 81: {
                    return new RefState(this);
                }
                case 100: {
                    return new DateState(this);
                }
                case 115: {
                    return new StringState((State)this, 'S', false);
                }
                case 83: {
                    return new StringState((State)this, 'S', true);
                }
                case 65: 
                case 98: {
                    return new BinaryState((State)this, 'B', false);
                }
                case 66: {
                    return new BinaryState((State)this, 'B', true);
                }
                case 77: {
                    return new MapState1(this, HessianDebugState.this._refId++);
                }
                case 86: {
                    return new ListState1(this, HessianDebugState.this._refId++);
                }
                case 82: {
                    return new IntegerState(new RefState1(this), "ref");
                }
            }
            this.printObject("x" + String.format("%02x", ch));
            return this;
        }
    }

    abstract class State {
        State _next;

        State() {
        }

        State(State next) {
            this._next = next;
        }

        abstract State next(int var1);

        boolean isShift(Object value) {
            return false;
        }

        State shift(Object value) {
            return this;
        }

        int depth() {
            if (this._next != null) {
                return this._next.depth();
            }
            return HessianDebugState.this.getDepth();
        }

        void printIndent(int depth) {
            if (HessianDebugState.this._isNewline) {
                for (int i = HessianDebugState.this._column; i < this.depth() + depth; ++i) {
                    HessianDebugState.this._dbg.print(" ");
                    HessianDebugState.this._column++;
                }
            }
        }

        void print(String string) {
            this.print(0, string);
        }

        void print(int depth, String string) {
            this.printIndent(depth);
            HessianDebugState.this._dbg.print(string);
            HessianDebugState.this._isNewline = false;
            HessianDebugState.this._isObject = false;
            int p = string.lastIndexOf(10);
            if (p > 0) {
                HessianDebugState.this._column = string.length() - p - 1;
            } else {
                HessianDebugState.this._column += string.length();
            }
        }

        void println(String string) {
            this.println(0, string);
        }

        void println(int depth, String string) {
            this.printIndent(depth);
            HessianDebugState.this._dbg.println(string);
            HessianDebugState.this._dbg.flush();
            HessianDebugState.this._isNewline = true;
            HessianDebugState.this._isObject = false;
            HessianDebugState.this._column = 0;
        }

        void println() {
            if (!HessianDebugState.this._isNewline) {
                HessianDebugState.this._dbg.println();
                HessianDebugState.this._dbg.flush();
            }
            HessianDebugState.this._isNewline = true;
            HessianDebugState.this._isObject = false;
            HessianDebugState.this._column = 0;
        }

        void printObject(String string) {
            if (HessianDebugState.this._isObject) {
                this.println();
            }
            this.printIndent(0);
            HessianDebugState.this._dbg.print(string);
            HessianDebugState.this._dbg.flush();
            HessianDebugState.this._column += string.length();
            HessianDebugState.this._isNewline = false;
            HessianDebugState.this._isObject = true;
        }

        protected State nextObject(int ch) {
            switch (ch) {
                case -1: {
                    this.println();
                    return this;
                }
                case 78: {
                    if (this.isShift(null)) {
                        return this.shift(null);
                    }
                    this.printObject("null");
                    return this;
                }
                case 84: {
                    if (this.isShift(Boolean.TRUE)) {
                        return this.shift(Boolean.TRUE);
                    }
                    this.printObject("true");
                    return this;
                }
                case 70: {
                    if (this.isShift(Boolean.FALSE)) {
                        return this.shift(Boolean.FALSE);
                    }
                    this.printObject("false");
                    return this;
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
                    Integer value = new Integer(ch - 144);
                    if (this.isShift(value)) {
                        return this.shift(value);
                    }
                    this.printObject(value.toString());
                    return this;
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
                    return new IntegerState(this, "int", ch - 200, 3);
                }
                case 208: 
                case 209: 
                case 210: 
                case 211: 
                case 212: 
                case 213: 
                case 214: 
                case 215: {
                    return new IntegerState(this, "int", ch - 212, 2);
                }
                case 73: {
                    return new IntegerState(this, "int");
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
                    Long value = new Long(ch - 224);
                    if (this.isShift(value)) {
                        return this.shift(value);
                    }
                    this.printObject(value.toString() + "L");
                    return this;
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
                    return new LongState(this, "long", ch - 248, 7);
                }
                case 56: 
                case 57: 
                case 58: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: {
                    return new LongState(this, "long", ch - 60, 6);
                }
                case 89: {
                    return new LongState(this, "long", 0L, 4);
                }
                case 76: {
                    return new LongState(this, "long");
                }
                case 91: 
                case 92: {
                    Double value = new Double(ch - 91);
                    if (this.isShift(value)) {
                        return this.shift(value);
                    }
                    this.printObject(value.toString());
                    return this;
                }
                case 93: {
                    return new DoubleIntegerState(this, 3);
                }
                case 94: {
                    return new DoubleIntegerState(this, 2);
                }
                case 95: {
                    return new MillsState(this);
                }
                case 68: {
                    return new DoubleState(this);
                }
                case 81: {
                    return new RefState(this);
                }
                case 74: {
                    return new DateState(this);
                }
                case 75: {
                    return new DateState(this, true);
                }
                case 0: {
                    String value = "\"\"";
                    if (this.isShift(value)) {
                        return this.shift(value);
                    }
                    this.printObject(value.toString());
                    return this;
                }
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
                    return new StringState(this, 'S', ch);
                }
                case 48: 
                case 49: 
                case 50: 
                case 51: {
                    return new StringState(this, 'S', ch - 48, true);
                }
                case 82: {
                    return new StringState(this, 'S', false);
                }
                case 83: {
                    return new StringState(this, 'S', true);
                }
                case 32: {
                    String value = "binary(0)";
                    if (this.isShift(value)) {
                        return this.shift(value);
                    }
                    this.printObject(value.toString());
                    return this;
                }
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
                    return new BinaryState(this, 'B', ch - 32);
                }
                case 52: 
                case 53: 
                case 54: 
                case 55: {
                    return new BinaryState(this, 'B', ch - 52, true);
                }
                case 65: {
                    return new BinaryState(this, 'B', false);
                }
                case 66: {
                    return new BinaryState(this, 'B', true);
                }
                case 77: {
                    return new MapState(this, HessianDebugState.this._refId++);
                }
                case 72: {
                    return new MapState(this, HessianDebugState.this._refId++, false);
                }
                case 85: {
                    return new ListState(this, HessianDebugState.this._refId++, true);
                }
                case 87: {
                    return new ListState(this, HessianDebugState.this._refId++, false);
                }
                case 86: {
                    return new CompactListState(this, HessianDebugState.this._refId++, true);
                }
                case 88: {
                    return new CompactListState(this, HessianDebugState.this._refId++, false);
                }
                case 112: 
                case 113: 
                case 114: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: {
                    return new CompactListState(this, HessianDebugState.this._refId++, true, ch - 112);
                }
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 127: {
                    return new CompactListState(this, HessianDebugState.this._refId++, false, ch - 120);
                }
                case 67: {
                    return new ObjectDefState(this);
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
                    return new ObjectState(this, HessianDebugState.this._refId++, ch - 96);
                }
                case 79: {
                    return new ObjectState(this, HessianDebugState.this._refId++);
                }
            }
            return this;
        }
    }
}

