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
package org.takes.facets.auth.codecs;

import java.io.IOException;
import org.takes.facets.auth.Identity;

/**
 * Codec.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
public interface Codec {

    /**
     * Encode identity into bytes.
     * @param identity The identity
     * @return Text
     * @throws IOException If fails
     */
    byte[] encode(Identity identity) throws IOException;

    /**
     * Decode identity from text (or throw
     * {@link org.takes.facets.auth.codecs.DecodingException}).
     *
     * <p>This method may throw
     * {@link org.takes.facets.auth.codecs.DecodingException}, if it's not
     * possible to decode the incoming byte array. This exception will mean
     * that the user can't be authenticated and {@code Identity.ANONYMOUS}
     * object will be identified.
     *
     * @param bytes Text
     * @return Identity
     * @throws IOException If fails
     */
    Identity decode(byte[] bytes) throws IOException;

}
