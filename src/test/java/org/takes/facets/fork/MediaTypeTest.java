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

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MediaType}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.6
 */
public final class MediaTypeTest {

    /**
     * MediaType can match two types.
     * @throws IOException If some problem inside
     */
    @Test
    public void matchesTwoTypes() throws IOException {
        MatcherAssert.assertThat(
            new MediaType("*/*").matches(new MediaType("application/pdf")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaType("application/xml").matches(new MediaType("*/* ")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaType("text/html").matches(new MediaType("text/*")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaType("image/*").matches(new MediaType("image/png")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MediaType("application/json").matches(
                new MediaType("text")
            ),
            Matchers.is(false)
        );
    }

    /**
     * MediaType can match two types.
     * @throws IOException If some problem inside
     */
    @Test
    public void comparesTwoTypes() throws IOException {
        MatcherAssert.assertThat(
            new MediaType("text/b").compareTo(new MediaType("text/a")),
            Matchers.not(Matchers.equalTo(0))
        );
    }

    /**
     * MediaType can parse invalid types.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesInvalidTypes() throws IOException {
        new MediaType("hello, how are you?");
        new MediaType("////");
        new MediaType("/;/;q=0.9");
        new MediaType("\n\n\t\r\u20ac00");
    }

}
