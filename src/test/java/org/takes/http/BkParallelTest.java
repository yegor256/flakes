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
package org.takes.http;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link BkParallel}.
 *
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @since 0.15.2
 */
@SuppressWarnings
    (
        {
            "PMD.CyclomaticComplexity",
            "PMD.AvoidInstantiatingObjectsInLoops",
            "PMD.StdCyclomaticComplexity",
            "PMD.ModifiedCyclomaticComplexity"
        }
    )
public final class BkParallelTest {
    /**
     * BkParallel runs requests in parallel, hence even
     * when handling of a request blocks, other requests
     * should be handled.
     * @throws Exception If some problem inside
     */
    @Test
    public void requestsAreParallel() throws Exception {
        final ServerSocket socket = new ServerSocket(0);
        final String uri = String.format(
            "http://localhost:%d", socket.getLocalPort()
        );
        // @checkstyle MagicNumberCheck (1 line)
        final int count = 3;
        final CountDownLatch started = new CountDownLatch(count);
        final CountDownLatch completed = new CountDownLatch(count);
        final Take take = new Take() {
            @Override
            public Response act(final Request req) {
                started.countDown();
                try {
                    started.await();
                } catch (final InterruptedException ex) {
                    throw new IllegalStateException(ex);
                }
                completed.countDown();
                return new TkEmpty().act(req);
            }
        };
        final Exit exit = new Exit() {
            @Override
            public boolean ready() {
                return completed.getCount() == 0;
            }
        };
        new Thread(
            // @checkstyle AnonInnerLengthCheck (23 lines)
            new Runnable() {
                @Override
                public void run() {
                    try {
                        new FtBasic(
                            new BkParallel(
                                new BkBasic(take),
                                count
                            ),
                            socket
                        ).start(exit);
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        ).start();
        for (int idx = 0; idx < count; ++idx) {
            new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new JdkRequest(uri)
                                .fetch()
                                .as(RestResponse.class)
                                .assertStatus(HttpURLConnection.HTTP_OK);
                        } catch (final IOException ex) {
                            throw new IllegalStateException(ex);
                        }
                    }
                }
            ).start();
        }
        completed.await(1L, TimeUnit.MINUTES);
        MatcherAssert.assertThat(started.getCount(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(completed.getCount(), Matchers.equalTo(0L));
    }
}
