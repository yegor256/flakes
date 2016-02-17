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
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;

/**
 * Test case for {@link BkTimeable}.
 *
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.14.2
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class BkTimeableTest {

    /**
     * Temp directory.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Rule
    public final transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * BkTimeable can stop long running Back.
     * @throws java.lang.Exception If some problem inside
     */
    @Test
    public void stopsLongRunningBack() throws Exception {
        final String response = "interrupted";
        final CountDownLatch ready = new CountDownLatch(1);
        final Exit exit = new Exit() {
            @Override
            public boolean ready() {
                ready.countDown();
                return false;
            }
        };
        final Take take = new Take() {
            @Override
            public Response act(final Request req) {
                Response rsp;
                try {
                    // @checkstyle MagicNumberCheck (1 line)
                    TimeUnit.SECONDS.sleep(10L);
                    rsp = new RsText("finish");
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    rsp = new RsText(response);
                }
                return rsp;
            }
        };
        final File file = this.temp.newFile();
        file.delete();
        final Thread thread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        new FtCLI(
                            take,
                            String.format("--port=%s", file.getAbsoluteFile()),
                            "--threads=1",
                            "--lifetime=3000",
                            "--max-latency=100"
                        ).start(exit);
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
        thread.start();
        ready.await();
        final int port = Integer.parseInt(FileUtils.readFileToString(file));
        new JdkRequest(String.format("http://localhost:%d", port))
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertBody(Matchers.startsWith(response));
        try {
            thread.join();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }
}
