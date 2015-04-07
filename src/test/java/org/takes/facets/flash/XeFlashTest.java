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
package org.takes.facets.flash;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link XeFlash}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 */
public final class XeFlashTest {

    /**
     * XeFlash can accept RsFlash cookie.
     * @throws IOException If some problem inside
     */
    @Test
    public void acceptsRsFlashCookie() throws IOException {
        final Pattern pattern = Pattern.compile(
            "^Set-Cookie: RsFlash=(.*?);Path.*"
        );
        final Iterator<String> itr = new RsFlash("hello").head().iterator();
        final List<String> cookies = new ArrayList<String>(0);
        while (itr.hasNext()) {
            final Matcher matcher = pattern.matcher(itr.next());
            if (matcher.find()) {
                cookies.add(matcher.group(1));
            }
        }
        MatcherAssert.assertThat(cookies, Matchers.hasSize(1));
        MatcherAssert.assertThat(
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeFlash(
                            new RqWithHeader(
                                new RqFake(),
                                "Cookie",
                                "RsFlash=".concat(cookies.get(0))
                            )
                        )
                    )
                ).body()
            ),
            XhtmlMatchers.hasXPaths(
                "/root/flash[message='hello']",
                "/root/flash[level='INFO']"
            )
        );
    }
}
