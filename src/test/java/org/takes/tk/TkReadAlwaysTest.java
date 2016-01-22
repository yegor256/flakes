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
package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkReadAlways}.
 * @author Dan Baleanu (dan.baleanu@gmail.com)
 * @version $Id$
 * @since 0.30
 */
public final class TkReadAlwaysTest {

    /**
     * Send a request with body which is ignored.
     * @throws Exception If there are problems
     */
    @Test
    public void requestBodyIsIgnored() throws Exception {
        final String expected = "response ok";
        final Take take = new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsText(expected);
            }
        };
        new FtRemote(new TkReadAlways(take)).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    new JdkRequest(home)
                        .method("POST").header(
                            "Content-Type", "application/x-www-form-urlencoded"
                        ).body()
                        .formParam("name", "Jeff Warraby")
                        .formParam("age", "4")
                        .back()
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .assertBody(Matchers.equalTo(expected));
                }
            }
        );
    }
}
