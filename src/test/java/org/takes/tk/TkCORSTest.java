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
package org.takes.tk;

import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkCORS}.
 * @author Endrigo Antonini (teamed@endrigo.com.br)
 * @version $Id$
 * @since 0.20
 */
public final class TkCORSTest {

    /**
     * TkCORS can handle connections without origin in the request.
     * @throws Exception If some problem inside
     */
    @Test
    public void handleConnectionsWithoutOriginInTheRequest() throws Exception {
        MatcherAssert.assertThat(
            "It was expected to receive a 403 error.",
            new TkCORS(
                new TkFixed(new RsText()),
                "http://www.netbout.io",
                "http://www.example.com"
            ).act(new RqFake()),
            new HmRsStatus(HttpURLConnection.HTTP_FORBIDDEN)
        );
    }

    /**
     * TkCORS can handle connections with correct domain on origin.
     * @throws Exception If some problem inside
     */
    @Test
    public void handleConnectionsWithCorrectDomainOnOrigin() throws Exception {
        MatcherAssert.assertThat(
            "Invalid HTTP status for a request with correct domain.",
            new TkCORS(
                new TkFixed(new RsText()),
                "http://teamed.io",
                "http://example.com"
            ).act(
                new RqWithHeaders(
                    new RqFake(),
                    "Origin: http://teamed.io"
                )
            ),
            new HmRsStatus(HttpURLConnection.HTTP_OK)
        );
    }

    /**
     * TkCors can't handle connections with wrong domain on origin.
     * @throws Exception If some problem inside
     */
    @Test
    public void cantHandleConnectionsWithWrongDomainOnOrigin()
        throws Exception {
        MatcherAssert.assertThat(
            "Wrong value on header.",
            new RsPrint(
                new TkCORS(
                    new TkFixed(new RsText()),
                    "http://www.teamed.io",
                    "http://sample.com"
                ).act(
                    new RqWithHeaders(
                        new RqFake(),
                        "Origin: http://wrong.teamed.io"
                    )
                )
            ).printHead(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 403"),
                Matchers.containsString(
                    "Access-Control-Allow-Credentials: false"
                )
            )
        );
    }
}
