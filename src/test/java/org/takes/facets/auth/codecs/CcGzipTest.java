/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Yegor Bugayenko
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
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.auth.Identity;

/**
 * Test case for {@link CcGzip}.
 *
 * @author Aleksey Kurochka (eg04lt3r@gmail.com)
 * @version $Id$
 * @since 0.16
 */
public final class CcGzipTest {

    /**
     * CcGzip can compress and decompress.
     * @throws Exception If some problem inside
     */
    @Test
    public void compressesAndDecompresses() throws Exception {
        final Codec gzip = new CcGzip(
            new Codec() {
                @Override
                public byte[] encode(final Identity identity)
                    throws IOException {
                    return identity.urn().getBytes();
                }
                @Override
                public Identity decode(final byte[] bytes) throws IOException {
                    return new Identity() {
                        @Override
                        public String urn() {
                            return new String(bytes);
                        }
                        @Override
                        public Map<String, String> properties() {
                            return new HashMap<String, String>();
                        }
                    };
                }
            }
        );
        final String urn = "test:gzip";
        final byte[] encode = gzip.encode(
            new Identity() {
                @Override
                public String urn() {
                    return urn;
                }
                @Override
                public Map<String, String> properties() {
                    return new HashMap<String, String>();
                }
            }
        );
        MatcherAssert.assertThat(
            gzip.decode(encode).urn(),
            Matchers.containsString(urn)
        );
    }
}
