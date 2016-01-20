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
package org.takes.facets.auth.codecs;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcCompact}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.5
 */
public final class CcCompactTest {

    /**
     * CcCompact can encode and decode.
     * @throws IOException If some problem inside
     */
    @Test
    public void encodesAndDecodes() throws IOException {
        final String urn = "urn:test:3";
        final Identity identity = new Identity.Simple(
            urn,
            new ImmutableMap.Builder<String, String>()
                .put("name", "Jeff Lebowski")
                .build()
        );
        final byte[] bytes = new CcCompact().encode(identity);
        MatcherAssert.assertThat(
            new CcCompact().decode(bytes).urn(),
            Matchers.equalTo(urn)
        );
    }

    /**
     * CcHex can decode invalid data.
     * @throws IOException If some problem inside
     */
    @Test
    public void decodesInvalidData() throws IOException {
        MatcherAssert.assertThat(
            new CcSafe(new CcCompact()).decode(
                " % tjw".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
        MatcherAssert.assertThat(
            new CcSafe(new CcCompact()).decode(
                "75726E253".getBytes()
            ),
            Matchers.equalTo(Identity.ANONYMOUS)
        );
    }

}
