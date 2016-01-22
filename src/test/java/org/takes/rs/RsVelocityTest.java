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
package org.takes.rs;

import java.io.IOException;
import java.util.Collections;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.misc.StateAwareInputStream;

/**
 * Test case for {@link RsVelocity}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
public final class RsVelocityTest {

    /**
     * RsVelocity can build text response.
     * @throws IOException If some problem inside
     */
    @Test
    public void buildsTextResponse() throws IOException {
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsVelocity(
                    "hello, ${name}!",
                    new RsVelocity.Pair("name", "Jeffrey")
                ).body()
            ),
            Matchers.equalTo("hello, Jeffrey!")
        );
    }

    /**
     * RsVelocity should close template's InputStream after serving response.
     * @throws IOException If some problem inside
     */
    @Test
    public void closesTemplateInputStream() throws IOException {
        final String template = "hello, world!";
        final StateAwareInputStream templateStream =
            new StateAwareInputStream(IOUtils.toInputStream(template));
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsVelocity(
                    templateStream,
                    Collections.<CharSequence, Object>emptyMap()
                ).body()
            ),
            Matchers.equalTo(template)
        );
        MatcherAssert.assertThat(templateStream.isClosed(), Matchers.is(true));
    }
}
