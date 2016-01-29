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

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * InputStream decorator that knows if it was closed or not.
 *
 * @author I. Sokolov (happy.neko@gmail.com)
 * @version $Id$
 */
public final class StateAwareInputStream extends InputStream {

    /**
     * Original InputStream.
     */
    private final transient InputStream origin;

    /**
     * Stream was closed flag.
     */
    private final AtomicBoolean closed;

    /**
     * Constructor.
     *
     * @param stream InputStream to decorate
     */
    public StateAwareInputStream(final InputStream stream) {
        super();
        this.closed = new AtomicBoolean(false);
        this.origin = stream;
    }

    @Override
    public int read() throws IOException {
        return this.origin.read();
    }

    @Override
    public int read(final byte[] buf) throws IOException {
        return this.origin.read(buf);
    }

    @Override
    public int read(final byte[] buf, final int off, final int len) throws
        IOException {
        return this.origin.read(buf, off, len);
    }

    @Override
    public long skip(final long num) throws IOException {
        return this.origin.skip(num);
    }

    @Override
    public int available() throws IOException {
        return this.origin.available();
    }

    @Override
    public void close() throws IOException {
        this.origin.close();
        this.closed.set(true);
    }

    @Override
    public void mark(final int readlimit) {
        this.origin.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        this.origin.reset();
    }

    @Override
    public boolean markSupported() {
        return this.origin.markSupported();
    }

    /**
     * Checks whether stream was closed.
     * @return True if stream was closed, otherwise false
     */
    public boolean isClosed() {
        return this.closed.get();
    }
}
