/*
 * Copyright (c) 2000, 2008, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.nio;

import java.util.Objects;
import libcore.io.Memory;

class ByteBufferAsIntBuffer extends IntBuffer {        // package-private

    protected final ByteBuffer bb;
    protected final int offset;
    private final ByteOrder order;

    ByteBufferAsIntBuffer(ByteBuffer bb,
                          int mark, int pos, int lim, int cap,
                          int off, ByteOrder order) {
        super(mark, pos, lim, cap);
        this.bb = bb.duplicate();
        this.isReadOnly = bb.isReadOnly;
        // There are only two possibilities for the type of ByteBuffer "bb", viz, DirectByteBuffer and
        // HeapByteBuffer. We only have to initialize the field when bb is an instance of
        // DirectByteBuffer.
        // The address field is used by NIOAccess#getBasePointer and GetDirectBufferAddress method
        // in art which return the address of the first usable byte of the underlying memory, i.e,
        // the position of parent buffer. Therefore, value of "off" will be equal to parent buffer's
        // position when the method is called from either HeapByteBuffer or DirectByteBuffer.
        if (bb instanceof DirectByteBuffer) {
            this.address = bb.address + off;
        }
        this.bb.order(order);
        this.order = order;
        offset = off;
    }

    @Override
    Object base() {
        return bb.hb;
    }

    public IntBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        int off = (pos << 2) + offset;
        assert (off >= 0);
        return new ByteBufferAsIntBuffer(bb, -1, 0, rem, rem, off, order);
    }

    @Override
    public IntBuffer slice(int index, int length) {
        Objects.checkFromIndexSize(index, length, limit());
        return new ByteBufferAsIntBuffer(bb,
                                                    -1,
                                                    0,
                                                    length,
                                                    length,
                                                    offset, order);
    }

    public IntBuffer duplicate() {
        return new ByteBufferAsIntBuffer(bb,
                markValue(),
                position(),
                limit(),
                capacity(),
                offset,
                order);
    }

    public IntBuffer asReadOnlyBuffer() {
        return new ByteBufferAsIntBuffer(bb.asReadOnlyBuffer(),
                markValue(),
                position(),
                limit(),
                capacity(),
                offset,
                order);
    }

    protected int ix(int i) {
        return (i << 2) + offset;
    }

    public int get() {
        return get(nextGetIndex());
    }

    public int get(int i) {
        return bb.getIntUnchecked(ix(checkIndex(i)));
    }

    public IntBuffer get(int[] dst, int offset, int length) {
        checkBounds(offset, length, dst.length);
        if (length > remaining())
            throw new BufferUnderflowException();
        bb.getUnchecked(ix(position), dst, offset, length);
        position += length;
        return this;
    }

    public IntBuffer put(int x) {
        put(nextPutIndex(), x);
        return this;
    }

    public IntBuffer put(int i, int x) {
        if (isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        bb.putIntUnchecked(ix(checkIndex(i)), x);
        return this;
    }

    public IntBuffer put(int[] src, int offset, int length) {
        checkBounds(offset, length, src.length);
        if (length > remaining())
            throw new BufferOverflowException();
        bb.putUnchecked(ix(position), src, offset, length);
        position += length;
        return this;
    }

    public IntBuffer compact() {
        if (isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        if (!(bb instanceof DirectByteBuffer)) {
            System.arraycopy(bb.array(), ix(pos), bb.array(), ix(0), rem << 2);
        } else {
            Memory.memmove(this, ix(0), this, ix(pos), rem << 2);
        }
        position(rem);
        limit(capacity());
        discardMark();
        return this;
    }

    public boolean isDirect() {
        return bb.isDirect();
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public ByteOrder order() {
        return order;
    }
}
