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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response compressed with GZIP, according to RFC 1952.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 */
@EqualsAndHashCode(callSuper = true)
public final class RsGzip extends RsWrap {

    /**
     * Ctor.
     * @param res Original response
     */
    public RsGzip(final Response res) {
        super(
            new RsWithHeader(
                new Response() {
                    @Override
                    public Iterable<String> head() throws IOException {
                        return res.head();
                    }
                    @Override
                    public InputStream body() throws IOException {
                        return RsGzip.gzip(res.body());
                    }
                },
                "Content-Encoding: gzip"
        )
        );
    }

    /**
     * Gzip input stream.
     * @param input Input stream
     * @return New input stream
     * @throws IOException If fails
     */
    private static InputStream gzip(final InputStream input)
        throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStream gzip = new GZIPOutputStream(baos);
        try {
            while (true) {
                final int data = input.read();
                if (data < 0) {
                    break;
                }
                gzip.write(data);
            }
        } finally {
            gzip.close();
            input.close();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

}
