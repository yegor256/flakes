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
 * Test case for {@link CcXOR}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.13.7
 */
public final class CcXORTest {

    /**
     * CcXor can encode and decode.
     * @throws IOException If some problem inside
     */
    @Test
    public void encodesAndDecodes() throws IOException {
        final String urn = "urn:domain:9";
        final Codec codec = new CcXOR(
            new Codec() {
                @Override
                public byte[] encode(final Identity identity) {
                    return identity.urn().getBytes();
                }
                @Override
                public Identity decode(final byte[] bytes) {
                    return new Identity.Simple(new String(bytes));
                }
            },
            "secret"
        );
        MatcherAssert.assertThat(
            codec.decode(codec.encode(new Identity.Simple(urn))).urn(),
            Matchers.equalTo(urn)
        );
    }
}
