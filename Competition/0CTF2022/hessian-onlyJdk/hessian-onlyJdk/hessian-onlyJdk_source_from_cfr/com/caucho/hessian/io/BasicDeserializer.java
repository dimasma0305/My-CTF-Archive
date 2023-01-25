/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class BasicDeserializer
extends AbstractDeserializer {
    public static final int NULL = 0;
    public static final int BOOLEAN = 1;
    public static final int BYTE = 2;
    public static final int SHORT = 3;
    public static final int INTEGER = 4;
    public static final int LONG = 5;
    public static final int FLOAT = 6;
    public static final int DOUBLE = 7;
    public static final int CHARACTER = 8;
    public static final int CHARACTER_OBJECT = 9;
    public static final int STRING = 10;
    public static final int DATE = 12;
    public static final int NUMBER = 13;
    public static final int OBJECT = 14;
    public static final int BOOLEAN_ARRAY = 15;
    public static final int BYTE_ARRAY = 16;
    public static final int SHORT_ARRAY = 17;
    public static final int INTEGER_ARRAY = 18;
    public static final int LONG_ARRAY = 19;
    public static final int FLOAT_ARRAY = 20;
    public static final int DOUBLE_ARRAY = 21;
    public static final int CHARACTER_ARRAY = 22;
    public static final int STRING_ARRAY = 23;
    public static final int OBJECT_ARRAY = 24;
    private int _code;

    public BasicDeserializer(int code) {
        this._code = code;
    }

    public Class getType() {
        switch (this._code) {
            case 0: {
                return Void.TYPE;
            }
            case 1: {
                return Boolean.class;
            }
            case 2: {
                return Byte.class;
            }
            case 3: {
                return Short.class;
            }
            case 4: {
                return Integer.class;
            }
            case 5: {
                return Long.class;
            }
            case 6: {
                return Float.class;
            }
            case 7: {
                return Double.class;
            }
            case 8: {
                return Character.class;
            }
            case 9: {
                return Character.class;
            }
            case 10: {
                return String.class;
            }
            case 12: {
                return Date.class;
            }
            case 13: {
                return Number.class;
            }
            case 14: {
                return Object.class;
            }
            case 15: {
                return boolean[].class;
            }
            case 16: {
                return byte[].class;
            }
            case 17: {
                return short[].class;
            }
            case 18: {
                return int[].class;
            }
            case 19: {
                return long[].class;
            }
            case 20: {
                return float[].class;
            }
            case 21: {
                return double[].class;
            }
            case 22: {
                return char[].class;
            }
            case 23: {
                return String[].class;
            }
            case 24: {
                return Object[].class;
            }
        }
        throw new UnsupportedOperationException();
    }

    public Object readObject(AbstractHessianInput in) throws IOException {
        switch (this._code) {
            case 0: {
                in.readObject();
                return null;
            }
            case 1: {
                return in.readBoolean();
            }
            case 2: {
                return (byte)in.readInt();
            }
            case 3: {
                return (short)in.readInt();
            }
            case 4: {
                return in.readInt();
            }
            case 5: {
                return in.readLong();
            }
            case 6: {
                return Float.valueOf((float)in.readDouble());
            }
            case 7: {
                return in.readDouble();
            }
            case 10: {
                return in.readString();
            }
            case 14: {
                return in.readObject();
            }
            case 8: {
                String s = in.readString();
                if (s == null || s.equals("")) {
                    return Character.valueOf('\u0000');
                }
                return Character.valueOf(s.charAt(0));
            }
            case 9: {
                String s = in.readString();
                if (s == null || s.equals("")) {
                    return null;
                }
                return Character.valueOf(s.charAt(0));
            }
            case 12: {
                return new Date(in.readUTCDate());
            }
            case 13: {
                return in.readObject();
            }
            case 16: {
                return in.readBytes();
            }
            case 22: {
                String s = in.readString();
                if (s == null) {
                    return null;
                }
                int len = s.length();
                char[] chars = new char[len];
                s.getChars(0, len, chars, 0);
                return chars;
            }
            case 15: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 23: {
                int code = in.readListStart();
                switch (code) {
                    case 78: {
                        return null;
                    }
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
                        int length = code - 16;
                        in.readInt();
                        return this.readLengthList(in, length);
                    }
                }
                String type = in.readType();
                int length = in.readLength();
                return this.readList(in, length);
            }
        }
        throw new UnsupportedOperationException();
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        switch (this._code) {
            case 15: {
                if (length >= 0) {
                    boolean[] data = new boolean[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = in.readBoolean();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList<Boolean> list = new ArrayList<Boolean>();
                while (!in.isEnd()) {
                    list.add(in.readBoolean());
                }
                in.readEnd();
                boolean[] data = new boolean[list.size()];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = (Boolean)list.get(i);
                }
                return data;
            }
            case 17: {
                if (length >= 0) {
                    short[] data = new short[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = (short)in.readInt();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList<Short> list = new ArrayList<Short>();
                while (!in.isEnd()) {
                    list.add((short)in.readInt());
                }
                in.readEnd();
                short[] data = new short[list.size()];
                for (int i = 0; i < data.length; ++i) {
                    data[i] = (Short)list.get(i);
                }
                in.addRef(data);
                return data;
            }
            case 18: {
                if (length >= 0) {
                    int[] data = new int[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = in.readInt();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList<Integer> list = new ArrayList<Integer>();
                while (!in.isEnd()) {
                    list.add(in.readInt());
                }
                in.readEnd();
                int[] data = new int[list.size()];
                for (int i = 0; i < data.length; ++i) {
                    data[i] = (Integer)list.get(i);
                }
                in.addRef(data);
                return data;
            }
            case 19: {
                if (length >= 0) {
                    long[] data = new long[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = in.readLong();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList<Long> list = new ArrayList<Long>();
                while (!in.isEnd()) {
                    list.add(in.readLong());
                }
                in.readEnd();
                long[] data = new long[list.size()];
                for (int i = 0; i < data.length; ++i) {
                    data[i] = (Long)list.get(i);
                }
                in.addRef(data);
                return data;
            }
            case 20: {
                if (length >= 0) {
                    float[] data = new float[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = (float)in.readDouble();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList<Float> list = new ArrayList<Float>();
                while (!in.isEnd()) {
                    list.add(new Float(in.readDouble()));
                }
                in.readEnd();
                float[] data = new float[list.size()];
                for (int i = 0; i < data.length; ++i) {
                    data[i] = ((Float)list.get(i)).floatValue();
                }
                in.addRef(data);
                return data;
            }
            case 21: {
                if (length >= 0) {
                    double[] data = new double[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = in.readDouble();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList<Double> list = new ArrayList<Double>();
                while (!in.isEnd()) {
                    list.add(new Double(in.readDouble()));
                }
                in.readEnd();
                double[] data = new double[list.size()];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = (Double)list.get(i);
                }
                return data;
            }
            case 23: {
                if (length >= 0) {
                    String[] data = new String[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = in.readString();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList<String> list = new ArrayList<String>();
                while (!in.isEnd()) {
                    list.add(in.readString());
                }
                in.readEnd();
                String[] data = new String[list.size()];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = (String)list.get(i);
                }
                return data;
            }
            case 24: {
                if (length >= 0) {
                    Object[] data = new Object[length];
                    in.addRef(data);
                    for (int i = 0; i < data.length; ++i) {
                        data[i] = in.readObject();
                    }
                    in.readEnd();
                    return data;
                }
                ArrayList<Object> list = new ArrayList<Object>();
                in.addRef(list);
                while (!in.isEnd()) {
                    list.add(in.readObject());
                }
                in.readEnd();
                Object[] data = new Object[list.size()];
                for (int i = 0; i < data.length; ++i) {
                    data[i] = list.get(i);
                }
                return data;
            }
        }
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        switch (this._code) {
            case 15: {
                boolean[] data = new boolean[length];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = in.readBoolean();
                }
                return data;
            }
            case 17: {
                short[] data = new short[length];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = (short)in.readInt();
                }
                return data;
            }
            case 18: {
                int[] data = new int[length];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = in.readInt();
                }
                return data;
            }
            case 19: {
                long[] data = new long[length];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = in.readLong();
                }
                return data;
            }
            case 20: {
                float[] data = new float[length];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = (float)in.readDouble();
                }
                return data;
            }
            case 21: {
                double[] data = new double[length];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = in.readDouble();
                }
                return data;
            }
            case 23: {
                String[] data = new String[length];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = in.readString();
                }
                return data;
            }
            case 24: {
                Object[] data = new Object[length];
                in.addRef(data);
                for (int i = 0; i < data.length; ++i) {
                    data[i] = in.readObject();
                }
                return data;
            }
        }
        throw new UnsupportedOperationException(String.valueOf(this));
    }
}

