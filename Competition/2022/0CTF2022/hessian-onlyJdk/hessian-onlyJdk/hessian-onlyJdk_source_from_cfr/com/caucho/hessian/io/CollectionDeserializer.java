/*
 * Decompiled with CFR 0.150.
 */
package com.caucho.hessian.io;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractListDeserializer;
import com.caucho.hessian.io.IOExceptionWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CollectionDeserializer
extends AbstractListDeserializer {
    private Class _type;

    public CollectionDeserializer(Class type) {
        this._type = type;
    }

    public Class getType() {
        return this._type;
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        Collection list = this.createList();
        in.addRef(list);
        while (!in.isEnd()) {
            list.add(in.readObject());
        }
        in.readEnd();
        return list;
    }

    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        Collection list = this.createList();
        in.addRef(list);
        while (length > 0) {
            list.add(in.readObject());
            --length;
        }
        return list;
    }

    private Collection createList() throws IOException {
        Collection list = null;
        if (this._type == null) {
            list = new ArrayList();
        } else if (!this._type.isInterface()) {
            try {
                list = (Collection)this._type.newInstance();
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (list == null) {
            if (SortedSet.class.isAssignableFrom(this._type)) {
                list = new TreeSet();
            } else if (Set.class.isAssignableFrom(this._type)) {
                list = new HashSet();
            } else if (List.class.isAssignableFrom(this._type)) {
                list = new ArrayList();
            } else if (Collection.class.isAssignableFrom(this._type)) {
                list = new ArrayList();
            } else {
                try {
                    list = (Collection)this._type.newInstance();
                }
                catch (Exception e) {
                    throw new IOExceptionWrapper(e);
                }
            }
        }
        return list;
    }
}

