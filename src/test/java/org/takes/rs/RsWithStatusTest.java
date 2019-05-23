/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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

import java.net.HttpURLConnection;
import org.cactoos.Scalar;
import org.cactoos.text.JoinedText;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasSize;
import org.llorllale.cactoos.matchers.TextIs;
import org.takes.Response;

/**
 * Test case for {@link RsWithStatus}.
 *
 * <p>The class is immutable and thread-safe.
 * @since 0.16.9
 * @checkstyle ClassDataAbstractionCouplingCheck (100 lines)
 */
public final class RsWithStatusTest {

    /**
     * RsWithStatus can add status.
     */
    @Test
    public void addsStatus() {
        new Assertion<>(
            "Response must contain not found status",
            () -> new RsPrint(
                new RsWithStatus(
                    new RsWithHeader("Host", "example.com"),
                    HttpURLConnection.HTTP_NOT_FOUND
                )
            ).print(),
            new TextIs(
                new JoinedText(
                    "\r\n",
                    "HTTP/1.1 404 Not Found",
                    "Host: example.com",
                    "",
                    ""
                )
            )
        ).affirm();
    }

    /**
     * RsWithStatus can add status multiple times.
     * @throws Exception If some problem inside
     */
    @SuppressWarnings(
        {
            "PMD.JUnitTestContainsTooManyAsserts",
            "PMD.ProhibitPlainJunitAssertionsRule"
        }
    )
    @Test
    public void addsStatusMultipleTimes() throws Exception {
        final Response response = new RsWithStatus(
            new RsWithStatus(
                new RsEmpty(),
                HttpURLConnection.HTTP_NOT_FOUND
            ),
            HttpURLConnection.HTTP_SEE_OTHER
        );
        final Scalar<Assertion<Iterable<?>>> factory = () -> new Assertion<>(
            "Head with one line",
            response.head(),
            new HasSize(1)
        );
        factory.value().affirm();
        factory.value().affirm();
    }
}
