/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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

package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import org.hamcrest.core.IsEqual;
import org.junit.Ignore;
import org.junit.Test;
import org.takes.http.FtRemote;

/**
 * Test case for {@link TkSlf4j} when used in conjunction
 * with {@link FtRemote} and {@link TkEmpty}.
 * @since 0.11.2
 */
public final class TkSlf4jRemoteTest {

    /**
     * TkSlf4j can return an empty response body for {@link TkEmpty}.
     * @throws IOException if some I/O problem occurred.
     */
    @Ignore
    @Test
    public void returnsAnEmptyResponseBody() throws IOException {
        new FtRemote(
            new TkSlf4j(new TkEmpty())
        ).exec((final URI home) -> {
            new JdkRequest(home)
                    .method("POST")
                    .body().set("returnsAnEmptyResponseBody").back()
                    .fetch()
                    .as(RestResponse.class)
                    .assertBody(new IsEqual<>(""))
                    .assertStatus(HttpURLConnection.HTTP_OK);
        });
    }
}
