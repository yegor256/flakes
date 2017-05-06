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

import com.google.common.base.Joiner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Pattern;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.takes.tk.TkGreedy;
import org.takes.tk.TkText;

/**
 * Test case for {@link BkReuse}.
 *
 * @author Shan Huang (thuhuangs09@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class BkReuseTest {
    /**
     * BkReuse can handle 2 requests in 1 tcp connection.
     * @throws IOException Socket IOException.
     */
    @Test
    public void handleTwoRequestInOneConnection() throws IOException {
        final String text = "Hello world!";
        final Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(
            new ByteArrayInputStream(
                    Joiner.on("\r\n").join(
                            "POST / HTTP/1.1",
                            "Host: localhost",
                            "Content-Length: 4",
                            "",
                            "hi",
                            "POST / HTTP/1.1",
                            "Host: localhost",
                            "Content-Length: 4",
                            "",
                            "hi"
                    ).getBytes()
            )
        );
        Mockito.when(socket.getLocalAddress()).thenReturn(
            InetAddress.getLocalHost()
        );
        Mockito.when(socket.getLocalPort()).thenReturn(0);
        Mockito.when(socket.getInetAddress()).thenReturn(
            InetAddress.getLocalHost()
        );
        Mockito.when(socket.getPort()).thenReturn(0);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(baos);
        new BkReuse(new BkBasic(new TkGreedy(new TkText(text)))).accept(socket);
        final Pattern pattern = Pattern.compile(
                text + ".*?" + text,
                Pattern.DOTALL
        );
        Assert.assertTrue(
                String.format("Actual is %s", baos.toString()),
                pattern.matcher(baos.toString()).find()
        );
    }

    /**
     * Server should reply 411 error when a persistent connection request has
     * no Content-Length.
     * @throws IOException Socket IOException.
     */
    @Test
    public void requestWithoutContentLength() throws IOException {
        final Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(
            new ByteArrayInputStream(Joiner.on("\r\n").join(
                        "POST / HTTP/1.1",
                        "Host: localhost",
                        "",
                        "hi"
                    ).getBytes()
            )
        );
        Mockito.when(socket.getLocalAddress()).thenReturn(
            InetAddress.getLocalHost()
        );
        Mockito.when(socket.getLocalPort()).thenReturn(0);
        Mockito.when(socket.getInetAddress()).thenReturn(
            InetAddress.getLocalHost()
        );
        Mockito.when(socket.getPort()).thenReturn(0);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(baos);
        new BkReuse(new BkBasic(new TkText("411 Test"))).accept(socket);
        MatcherAssert.assertThat(
            baos.toString(),
            Matchers.containsString("HTTP/1.1 411 Length Required")
        );
    }

    /**
     * It's OK for no content-length with non-persistent connection.
     * @throws IOException Socket IOException.
     */
    @Test
    public void requestWithConnectionClose() throws IOException {
        final String text = "Close Test";
        final Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(
            new ByteArrayInputStream(Joiner.on("\r\n").join(
                        "POST / HTTP/1.1",
                        "Host: localhost",
                        "Connection: Close",
                        "",
                        "hi"
                    ).getBytes()
            )
        );
        Mockito.when(socket.getLocalAddress()).thenReturn(
            InetAddress.getLocalHost()
        );
        Mockito.when(socket.getLocalPort()).thenReturn(0);
        Mockito.when(socket.getInetAddress()).thenReturn(
            InetAddress.getLocalHost()
        );
        Mockito.when(socket.getPort()).thenReturn(0);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(baos);
        new BkBasic(new TkText(text)).accept(socket);
        MatcherAssert.assertThat(
            baos.toString(),
            Matchers.containsString(text)
        );
    }

}
