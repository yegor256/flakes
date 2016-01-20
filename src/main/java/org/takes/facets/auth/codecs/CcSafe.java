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
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;

/**
 * Safe codec, never throws decoding exception.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.5
 */
@EqualsAndHashCode(of = "origin")
public final class CcSafe implements Codec {

    /**
     * Original codec.
     */
    private final transient Codec origin;

    /**
     * Ctor.
     * @param codec Original codec
     */
    public CcSafe(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        return this.origin.encode(identity);
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        Identity identity;
        try {
            identity = this.origin.decode(bytes);
        } catch (final DecodingException ex) {
            identity = Identity.ANONYMOUS;
        }
        return identity;
    }

}
