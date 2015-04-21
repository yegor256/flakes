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
 * Transform elements in an iterable (in type T) into others (in type K).
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
public class Transform<T, K> implements Iterable<K> {

    /**
     * Internal storage.
     */
    private final transient Iterable<T> list;

    /**
     * The action to transform the elements in the iterator.
     */
    private final transient TransformAction<T, K> action;

    /**
     * Transform elements in the supplied iterable by the action supplied.
     * @param itb Iterable to be transformed
     * @param act The actual transformation implementation
     */
    public Transform(final Iterable<T> itb,
        final TransformAction<T, K> act) {
        this.list = itb;
        this.action = act;
    }

    @Override
    public final Iterator<K> iterator() {
        return new TransformIterator<T, K>(this.list.iterator(), this.action);
    }

    /**
     * The iterator to iterator through the original type B and return the
     * transformed element in type A.
     */
    private static class TransformIterator<B, A> implements Iterator<A> {

        /**
         * The iterator to reflect the traverse state.
         */
        private final transient Iterator<B> iterator;

        /**
         * The action to transform the elements in the iterator.
         */
        private final transient TransformAction<B, A> action;

        /**
         * Ctor. ConcatIterator traverses the element.
         * @param itr Iterator of the original iterable
         * @param act Action to transform elements
         */
        public TransformIterator(final Iterator<B> itr,
            final TransformAction<B, A> act) {
            this.action = act;
            this.iterator = itr;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public A next() {
            return this.action.transform(this.iterator.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                "This iterable is immutable and cannot remove anything"
            );
        }
    }
}
