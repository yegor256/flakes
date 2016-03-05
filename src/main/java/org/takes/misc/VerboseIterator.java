/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.misc;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Verbose iterator.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @param <T> Type of item
 * @since 0.10
 */
public final class VerboseIterator<T> implements Iterator<T> {

    /**
     * Original iterator.
     */
    private final transient Iterator<T> origin;

    /**
     * Error message when running out of items.
     */
    private final transient CharSequence error;

    /**
     * Ctor.
     * @param iter Original iterator
     * @param msg Error message
     */
    public VerboseIterator(final Iterator<T> iter, final CharSequence msg) {
        this.origin = iter;
        this.error = msg;
    }

    @Override
    public boolean hasNext() {
        return this.origin.hasNext();
    }

    @Override
    public T next() {
        if (!this.origin.hasNext()) {
            throw new NoSuchElementException(this.error.toString());
        }
        return this.origin.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("#remove()");
    }
}
