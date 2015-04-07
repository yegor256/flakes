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
package org.takes.rs;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Response;

/**
 * Test case for {@link RsGzip}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RsGzipTest {

    /**
     * RsGzip can build a compressed response.
     * @throws IOException If some problem inside
     */
    @Test
    public void makesCompressedResponse() throws IOException {
        final String text = "some unicode text: \u20ac\n\t";
        final Response response = new RsGzip(new RsText(text));
        MatcherAssert.assertThat(
            new RsPrint(response).printHead(),
            Matchers.containsString("Content-Encoding: gzip")
        );
        MatcherAssert.assertThat(
            IOUtils.toString(new GZIPInputStream(response.body())),
            Matchers.equalTo(text)
        );
    }

    /**
     * RsGzip can build a compressed PNG image.
     * @throws IOException If some problem inside
     */
    @Test
    public void makesCompressedPngImage() throws IOException {
        final RenderedImage image = new BufferedImage(
            1, 1, BufferedImage.TYPE_INT_ARGB
        );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        final ByteArrayOutputStream gzip = new ByteArrayOutputStream();
        new RsPrint(
            new RsGzip(
                new RsWithBody(baos.toByteArray())
            )
        ).printBody(gzip);
        final BufferedImage reverse = ImageIO.read(
            new GZIPInputStream(new ByteArrayInputStream(gzip.toByteArray()))
        );
        MatcherAssert.assertThat(reverse.getHeight(), Matchers.equalTo(1));
    }

}
