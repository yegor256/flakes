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
package org.takes.facets.fork;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.util.Arrays;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.HttpException;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsJSON;
import org.takes.rs.RsPrint;
import org.takes.tk.TkFixed;

/**
 * Test case for {@link TkConsumes}.
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class TkConsumesTest {

    /**
     * TkConsumes can accept request with certain Content-Type.
     * @throws IOException If some problem inside
     */
    @Test
    public void acceptsCorrectContentTypeRequest() throws IOException {
        final Take consumes = new TkConsumes(
            new TkFixed(new RsJSON(new RsEmpty())),
            "application/json"
        );
        final Response response = consumes.act(
            new RqFake(
                Arrays.asList(
                    "GET /?TkConsumes",
                    "Content-Type: application/json"
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            new RsPrint(response).print(),
            Matchers.startsWith(
                Joiner.on("\r\n").join(
                    "HTTP/1.1 200 OK",
                    "Content-Type: application/json"
                )
            )
        );
    }

    /**
     * TkConsumes can fail on unsupported Content-Type header.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void failsOnUnsupportedAcceptHeader() throws IOException {
        final Take consumes = new TkConsumes(
            new TkFixed(new RsJSON(new RsEmpty())),
            "application/json"
        );
        consumes.act(
            new RqFake(
                Arrays.asList(
                    "GET /?TkConsumes error",
                    "Content-Type: application/xml"
                ),
                ""
            )
        ).head();
    }

    /**
     * Checks TkConsumes equals method.
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        EqualsVerifier.forClass(TkConsumes.class)
            .withRedefinedSuperclass()
            .suppress(Warning.TRANSIENT_FIELDS)
            .verify();
    }
}
