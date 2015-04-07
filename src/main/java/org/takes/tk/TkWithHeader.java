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
package org.takes.tk;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithHeader;

/**
 * Take with an extra header.
 *
 * <p>This take wraps all responses of another take, adding
 * an extra header to them, through {@link org.takes.rs.RsWithHeader}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.11
 */
@EqualsAndHashCode(callSuper = true)
public final class TkWithHeader extends TkWrap {

    /**
     * Ctor.
     * @param take Original
     * @param key Header
     * @param value Value
     */
    public TkWithHeader(final Take take, final String key, final String value) {
        this(take, String.format("%s: %s", key, value));
    }

    /**
     * Ctor.
     * @param take Original
     * @param header Header
     */
    public TkWithHeader(final Take take, final String header) {
        super(
            new Take() {
                @Override
                public Response act(final Request req) throws IOException {
                    return new RsWithHeader(take.act(req), header);
                }
            }
        );
    }

}
