/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
import java.net.HttpURLConnection;
import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.misc.Opt;
import org.takes.rq.RqRequestLine;
import org.takes.rq.UriHandler;
import org.takes.rs.RsRedirect;

/**
 * Take that redirects, passing all query arguments and the fragment through.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.9
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkSmartRedirect extends TkWrap {

    /**
     * Ctor.
     */
    public TkSmartRedirect() {
        this("/");
    }

    /**
     * Ctor.
     * @param location Location to redirect to
     */
    public TkSmartRedirect(final String location) {
        this(location, HttpURLConnection.HTTP_SEE_OTHER);
    }

    /**
     * Ctor.
     * @param location Location to redirect to
     * @param code Redirection status code
     */
    public TkSmartRedirect(final String location, final int code) {
        super(
            req -> new RsRedirect(
                new RedirectParams(
                    req, location
                ).location(),
                code
            )
        );
    }

    /**
     * Extract params from original query.
     * @since 1.9
     */
    private static final class RedirectParams {
        /**
         * Original request.
         */
        private final Request req;

        /**
         * Original location.
         */
        private final String origin;

        /**
         * Ctor.
         * @param req Original request.
         * @param origin Original location.
         */
        RedirectParams(final Request req, final String origin) {
            this.req = req;
            this.origin = origin;
        }

        /**
         * Get location with composed params.
         * @return New location.
         * @throws IOException in case of error.
         */
        public String location() throws IOException {
            final StringBuilder loc = new StringBuilder(this.origin);
            final URI target = URI.create(this.origin);
            final URI uri = URI.create(new RqRequestLine.Base(this.req).uri());
            final Opt<String> uriQuery = UriHandler.getQuery(uri);
            if (uriQuery.has()) {
                if (!UriHandler.getQuery(target).has()) {
                    loc.append('?');
                } else {
                    loc.append('&');
                }
                loc.append(uriQuery.get());
            }
            if (uri.getFragment() != null) {
                loc.append('#');
                loc.append(uri.getFragment());
            }
            return loc.toString();
        }
    }
}
