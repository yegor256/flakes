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
package org.takes.facets.forward;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkForward}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.2
 */
public final class TkForwardTest {

    /**
     * TkForward can catch RsForward.
     * @throws IOException If some problem inside
     */
    @Test
    public void catchesExceptionCorrectly() throws IOException {
        final Take take = new Take() {
            @Override
            public Response act(final Request request) {
                throw new RsForward("/");
            }
        };
        MatcherAssert.assertThat(
            new RsPrint(
                new TkForward(take).act(new RqFake())
            ).print(),
            Matchers.startsWith("HTTP/1.1 303 See Other")
        );
    }

    /**
     * TkForward can catch RsForward.
     * @throws IOException If some problem inside
     */
    @Test
    public void catchesExceptionThrownByTake() throws IOException {
        final Take take = new Take() {
            @Override
            public Response act(final Request request) {
                throw new RsForward("/h");
            }
        };
        MatcherAssert.assertThat(
            new RsPrint(
                new TkForward(take).act(new RqFake())
            ).print(),
            Matchers.startsWith("HTTP/1.1 303")
        );
    }

}
