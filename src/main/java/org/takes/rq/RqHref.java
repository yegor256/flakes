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
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.Href;

/**
 * HTTP URI query parsing.
 *
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 */
public interface RqHref extends Request {
    /**
     * Get HREF.
     * @return HTTP href
     * @throws IOException If fails
     */
    Href href() throws IOException;

    /**
     * Request decorator, for HTTP URI query parsing.
     *
     * <p>The class is immutable and thread-safe.
     * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
     * @version $Id$
     * @since 0.13.1
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqHref {
        /**
         * Ctor.
         * @param req Original request
         */
        public Base(final Request req) {
            super(req);
        }
        @Override
        public Href href() throws IOException {
            final Iterator<String> host = new RqHeaders(this)
                .header("host").iterator();
            if (!host.hasNext()) {
                throw new IOException("Host header is absent");
            }
            return new Href(
                String.format(
                    "http://%s%s",
                    host.next(),
                    // @checkstyle MagicNumber (1 line)
                    this.head().iterator().next().split(" ", 3)[1]
                )
            );
        }
    }

    /**
     * Smart decorator, with extra features.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Yegor Bugayenko (yegor@teamed.io)
     * @since 0.14
     */
    @EqualsAndHashCode(of = "origin")
    final class Smart implements RqHref {
        /**
         * Original.
         */
        private final transient RqHref origin;
        /**
         * Ctor.
         * @param req Original request
         */
        public Smart(final RqHref req) {
            this.origin = req;
        }
        @Override
        public Href href() throws IOException {
            return this.origin.href();
        }
        @Override
        public Iterable<String> head() throws IOException {
            return this.origin.head();
        }
        @Override
        public InputStream body() throws IOException {
            return this.origin.body();
        }
        /**
         * Get self.
         * @return Self page, full URL
         * @throws IOException If fails
         * @since 0.14
         */
        public Href home() throws IOException {
            return new Href(
                String.format(
                    "http://%s/",
                    new RqHeaders(this).header("Host").iterator().next()
                )
            );
        }
        /**
         * Get param or throw HTTP exception.
         * @param name Name of query param
         * @return Value of it
         * @throws IOException If fails
         */
        public String single(final CharSequence name) throws IOException {
            final Iterator<String> params = this.origin.href()
                .param(name).iterator();
            if (!params.hasNext()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "query param \"%s\" is mandatory", name
                    )
                );
            }
            return params.next();
        }
        /**
         * Get param or default.
         * @param name Name of query param
         * @param def Default, if not found
         * @return Value of it
         * @throws IOException If fails
         */
        public String single(final CharSequence name, final CharSequence def)
            throws IOException {
            final String value;
            final Iterator<String> params = this.origin.href()
                .param(name).iterator();
            if (params.hasNext()) {
                value = params.next();
            } else {
                value = def.toString();
            }
            return value;
        }
    }
}

