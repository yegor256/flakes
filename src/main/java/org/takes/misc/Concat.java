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

/**
 * Concat iterable.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
public final class Concat<T> implements Iterable<T> {

    /**
     * Internal reference to hold the first elements from constructor.
     */
    private final transient Iterable<T> left;

    /**
     * Internal reference to hold the second elements from constructor.
     */
    private final transient Iterable<T> right;

    /**
     * To produce an iterable collection combining a and b, with order of the
     * elements in a first.
     * @param aitb First iterable to concat
     * @param bitb Second iterable to conat
     */
    public Concat(final Iterable<T> aitb, final Iterable<T> bitb) {
        this.left = aitb;
        this.right = bitb;
    }

    @Override
    public Iterator<T> iterator() {
        return new ConcatIterator<T>(
            this.left.iterator(),
            this.right.iterator()
        );
    }

    /**
     * The concat iterator to traverse the input iterables as if they are
     * from one list.
     */
    private static class ConcatIterator<E> implements Iterator<E> {

        /**
         * Internal reference for holding the first iterator form constructor.
         */
        private final transient Iterator<E> left;

        /**
         * Internal reference for holding the second iterator form constructor.
         */
        private final transient Iterator<E> right;

        /**
         * Ctor. ConcatIterator traverses the element.
         * @param aitr The first iterable to traverse
         * @param bitr The second iterable to traverse
         */
        public ConcatIterator(final Iterator<E> aitr, final Iterator<E> bitr) {
            this.left = aitr;
            this.right = bitr;
        }

        @Override
        public boolean hasNext() {
            return this.left.hasNext() || this.right.hasNext();
        }

        @Override
        public E next() {
            final E object;
            if (this.left.hasNext()) {
                object = this.left.next();
            } else {
                object = this.right.next();
            }
            return object;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                "This iterable is immutable and cannot remove anything"
            );
        }
    }
}
