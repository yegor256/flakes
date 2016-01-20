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
package org.takes.facets.auth;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;

/**
 * Test case for {@link PsChain}.
 * @author Aleksey Kurochka (eg04lt3r@gmail.com)
 * @version $Id$
 */
public final class PsChainTest {

    /**
     * PsChain returns proper identity.
     * @throws IOException if some problems inside
     */
    @Test
    public void chainExecutionTest() throws IOException {
        MatcherAssert.assertThat(
            new PsChain(
                new PsLogout(),
                new PsFake(true)
            ).enter(new RqFake()).get(),
            Matchers.is(Identity.ANONYMOUS)
        );
    }

    /**
     * PsChain returns proper response.
     * @throws IOException if some problems inside
     */
    @Test
    public void exitChainTest() throws IOException {
        MatcherAssert.assertThat(
            new PsChain(
                new PsFake(true)
            ).exit(new RsEmpty(), Identity.ANONYMOUS)
                .head().iterator().next(),
            Matchers.containsString("HTTP/1.1 200 O")
        );
    }
}
