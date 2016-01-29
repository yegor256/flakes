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
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHeaders;
import org.takes.rs.RsWithHeaders;
import org.takes.rs.RsWithStatus;

/**
 * CORS take.
 *
 * <p>This take checks if the request (Origin) is allowed to perform
 * the desired action against the list of the given domains.</p>
 *
 * <p>The specification of CORS can be found on the W3C web site on the
 * following <a href="http://www.w3.org/TR/cors/">link</a> or even on the <a
 * href="https://tools.ietf.org/html/rfc6454">RFC-6454</a> specification.
 *
 * @author Endrigo Antonini (teamed@endrigo.com.br)
 * @version $Id$
 * @since 0.20
 */
@ToString(of = { "origin", "allowed" })
@EqualsAndHashCode(of = { "origin", "allowed" })
public final class TkCORS implements Take {

    /**
     * Original take.
     */
    private final transient Take origin;

    /**
     * List of allowed domains.
     */
    private final transient Set<String> allowed;

    /**
     * Ctor.
     * @param take Original
     * @param domains Allow domains
     */
    public TkCORS(final Take take, final String... domains) {
        this.origin = take;
        this.allowed = new HashSet<String>(Arrays.asList(domains));
    }

    @Override
    public Response act(final Request req) throws IOException {
        final Response response;
        final String domain = new RqHeaders.Smart(
            new RqHeaders.Base(req)
        ).single("origin", "");
        if (this.allowed.contains(domain)) {
            response = new RsWithHeaders(
                this.origin.act(req),
                "Access-Control-Allow-Credentials: true",
                // @checkstyle LineLengthCheck (1 line)
                "Access-Control-Allow-Methods: OPTIONS, GET, PUT, POST, DELETE, HEAD",
                String.format(
                    "Access-Control-Allow-Origin: %s",
                    domain
                )
            );
        } else {
            response = new RsWithHeaders(
                new RsWithStatus(
                    HttpURLConnection.HTTP_FORBIDDEN
                ),
                "Access-Control-Allow-Credentials: false"
            );
        }
        return response;
    }
}
