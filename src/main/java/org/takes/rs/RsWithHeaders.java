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
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response decorator, with an additional headers.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RsWithHeaders extends RsWrap {

    /**
     * Ctor.
     * @param res Original response
     * @param headers Headers
     */
    public RsWithHeaders(final Response res, final CharSequence... headers) {
        this(res, Arrays.asList(headers));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param headers Headers
     */
    public RsWithHeaders(final Response res,
        final Iterable<? extends CharSequence> headers) {
        super(
            new Response() {
                @Override
                public List<String> head() throws IOException {
                    final List<String> head = new LinkedList<String>();
                    for (final String hdr : res.head()) {
                        head.add(hdr);
                    }
                    for (final CharSequence header : headers) {
                        head.add(header.toString().trim());
                    }
                    return head;
                }
                @Override
                public InputStream body() throws IOException {
                    return res.body();
                }
            }
        );
    }

}
