/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015, 2016 Yegor Bugayenko
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;

/**
 * Sprintf in a class.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 */
public final class Sprintf implements CharSequence {

    /**
     * Pattern.
     */
    private final transient String pattern;

    /**
     * Arguments.
     */
    private final transient Collection<Object> args;

    /**
     * Ctor.
     * @param ptn Pattern
     * @param arguments Arguments
     */
    public Sprintf(final String ptn, final Object... arguments) {
        this(ptn, Arrays.asList(arguments));
    }

    /**
     * Ctor.
     * @param ptn Pattern
     * @param arguments Arguments
     */
    public Sprintf(final String ptn, final Collection<Object> arguments) {
        this.pattern = ptn;
        this.args = Collections.unmodifiableCollection(arguments);
    }

    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder(0);
        new Formatter(out).format(
            this.pattern, this.args.toArray(new Object[this.args.size()])
        );
        return out.toString();
    }

    @Override
    public int length() {
        return this.toString().length();
    }

    @Override
    public char charAt(final int index) {
        return this.toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.toString().subSequence(start, end);
    }
}
