/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.Hessian2StreamingInput;
import com.caucho.hessian.io.Hessian2StreamingOutput;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.util.HessianFreeList;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HessianFactory {
    public static final Logger log = Logger.getLogger(HessianFactory.class.getName());
    private SerializerFactory _serializerFactory;
    private SerializerFactory _defaultSerializerFactory;
    private final HessianFreeList<Hessian2Output> _freeHessian2Output = new HessianFreeList(32);
    private final HessianFreeList<HessianOutput> _freeHessianOutput = new HessianFreeList(32);
    private final HessianFreeList<Hessian2Input> _freeHessian2Input = new HessianFreeList(32);
    private final HessianFreeList<HessianInput> _freeHessianInput = new HessianFreeList(32);

    public HessianFactory() {
        this._serializerFactory = this._defaultSerializerFactory = SerializerFactory.createDefault();
    }

    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    public SerializerFactory getSerializerFactory() {
        if (this._serializerFactory == this._defaultSerializerFactory) {
            this._serializerFactory = new SerializerFactory();
        }
        return this._serializerFactory;
    }

    public Hessian2Input createHessian2Input(InputStream is) {
        Hessian2Input in = this._freeHessian2Input.allocate();
        if (in == null) {
            in = new Hessian2Input(is);
            in.setSerializerFactory(this.getSerializerFactory());
        } else {
            in.init(is);
        }
        return in;
    }

    public void freeHessian2Input(Hessian2Input in) {
        if (in == null) {
            return;
        }
        in.free();
        this._freeHessian2Input.free(in);
    }

    public Hessian2StreamingInput createHessian2StreamingInput(InputStream is) {
        Hessian2StreamingInput in = new Hessian2StreamingInput(is);
        in.setSerializerFactory(this.getSerializerFactory());
        return in;
    }

    public void freeHessian2StreamingInput(Hessian2StreamingInput in) {
    }

    public HessianInput createHessianInput(InputStream is) {
        return new HessianInput(is);
    }

    public Hessian2Output createHessian2Output(OutputStream os) {
        Hessian2Output out = this.createHessian2Output();
        out.init(os);
        return out;
    }

    public Hessian2Output createHessian2Output() {
        Hessian2Output out = this._freeHessian2Output.allocate();
        if (out == null) {
            out = new Hessian2Output();
            out.setSerializerFactory(this.getSerializerFactory());
        }
        return out;
    }

    public void freeHessian2Output(Hessian2Output out) {
        if (out == null) {
            return;
        }
        out.free();
        this._freeHessian2Output.free(out);
    }

    public Hessian2StreamingOutput createHessian2StreamingOutput(OutputStream os) {
        Hessian2Output out = this.createHessian2Output(os);
        return new Hessian2StreamingOutput(out);
    }

    public void freeHessian2StreamingOutput(Hessian2StreamingOutput out) {
        if (out == null) {
            return;
        }
        this.freeHessian2Output(out.getHessian2Output());
    }

    public HessianOutput createHessianOutput(OutputStream os) {
        return new HessianOutput(os);
    }

    public OutputStream createHessian2DebugOutput(OutputStream os, Logger log, Level level) {
        HessianDebugOutputStream out = new HessianDebugOutputStream(os, log, level);
        out.startTop2();
        return out;
    }
}

