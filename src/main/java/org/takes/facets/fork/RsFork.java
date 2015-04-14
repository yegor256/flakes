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
package org.takes.facets.fork;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.rs.RsWrap;

/**
 * Response based on forks.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.6
 */
@EqualsAndHashCode(callSuper = true)
public final class RsFork extends RsWrap {

    /**
     * Ctor.
     * @param req Request
     * @param list List of forks
     */
    public RsFork(final Request req, final Fork... list) {
        this(req, Arrays.asList(list));
    }

    /**
     * Ctor.
     * @param req Request
     * @param list List of forks
     */
    public RsFork(final Request req, final Iterable<Fork> list) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return RsFork.pick(req, list).head();
                }
                @Override
                public InputStream body() throws IOException {
                    return RsFork.pick(req, list).body();
                }
            }
        );
    }

    /**
     * Pick the right one.
     * @param req Request
     * @param forks List of forks
     * @return Response
     * @throws IOException If fails
     */
    private static Response pick(final Request req,
        final Iterable<Fork> forks) throws IOException {
        for (final Fork fork : forks) {
            final Iterator<Response> rsps = fork.route(req);
            if (rsps.hasNext()) {
                return rsps.next();
            }
        }
        throw new HttpException(HttpURLConnection.HTTP_NOT_FOUND);
    }

}
