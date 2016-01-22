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

package org.takes.facets.auth.social;

import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.json.Json;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.rq.RqFake;

/**
 * Test case for {@link PsTwitter}.
 * @author Prasath Premkumar (popprem@gmail.com)
 * @author Adam Siemion (adam.siemion@lemonsoftware.pl)
 * @version $Id$
 * @since 1.0
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class PsTwitterTest {

    /**
     * PsTwitter can login.
     * @throws IOException If error occurs in the process
     */
    @Test
    public void logsIn() throws IOException {
        final int tid = RandomUtils.nextInt(1000);
        final String name = RandomStringUtils.randomAlphanumeric(10);
        final String picture = RandomStringUtils.randomAlphanumeric(10);
        final Pass pass = new PsTwitter(
            new FakeRequest(
                200,
                "HTTP OK",
                Collections.<Map.Entry<String, String>>emptyList(),
                String.format(
                    "{\"token_type\":\"bearer\",\"access_token\":\"%s\"}",
                    RandomStringUtils.randomAlphanumeric(10)
                ).getBytes()
            ),
            new FakeRequest(
                200,
                "HTTP OK",
                Collections.<Map.Entry<String, String>>emptyList(),
                Json.createObjectBuilder()
                    .add("id", tid)
                    .add("name", name)
                    .add("profile_image_url", picture)
                    .build()
                    .toString()
                    .getBytes()
            ),
            RandomStringUtils.randomAlphanumeric(10),
            RandomStringUtils.randomAlphanumeric(10)
        );
        final Identity identity = pass.enter(
            new RqFake("GET", "")
        ).get();
        MatcherAssert.assertThat(
            identity.urn(),
            CoreMatchers.equalTo(String.format("urn:twitter:%d", tid))
        );
        MatcherAssert.assertThat(
            identity.properties().get("name"),
            CoreMatchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            identity.properties().get("picture"),
            CoreMatchers.equalTo(picture)
        );
    }
}
