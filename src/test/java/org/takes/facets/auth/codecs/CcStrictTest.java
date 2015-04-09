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
package org.takes.facets.auth.codecs;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcStrict}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11.2
 */
public final class CcStrictTest {
    /**
     * CcStrict can block empty urn.
     * @throws IOException If some problem inside
     */
    @Test(expected = DecodingException.class)
    public void blocksEmptyUrn() throws IOException {
        new CcStrict(new CcPlain()).encode(new Identity.Simple(""));
    }

    /**
     * CcStrict can block invalid urn.
     * @throws IOException If some problem inside
     */
    @Test(expected = DecodingException.class)
    public void blocksInvalidUrn() throws IOException {
        new CcStrict(new CcPlain()).decode("u%3Atest%3A9".getBytes());
    }

    /**
     * CcStrict can pass valid Identities.
     * @throws IOException If some problem inside
     */
    @Test
    public void passesValid() throws IOException {
        MatcherAssert.assertThat(
            new String(
                new CcStrict(new CcPlain()).encode(
                    new Identity.Simple("urn:test:1")
                )
            ), Matchers.equalTo("urn%3Atest%3A1")
        );
        MatcherAssert.assertThat(
            new String(
                new CcStrict(new CcPlain()).encode(
                    new Identity.Simple("urn:test-domain-org:valid:1")
                )
            ), Matchers.equalTo("urn%3Atest-domain-org%3Avalid%3A1")
        );
    }
}
