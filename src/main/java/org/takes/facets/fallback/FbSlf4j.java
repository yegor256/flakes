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
package org.takes.facets.fallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.takes.Response;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

/**
 * Fallback that logs all problems through SFL4J.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.25
 */
@EqualsAndHashCode(callSuper = true)
public final class FbSlf4j extends FbWrap {

    /**
     * SLF4J logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FbSlf4j.class);

    /**
     * Ctor.
     */
    public FbSlf4j() {
        super(
            new Fallback() {
                @Override
                public Opt<Response> route(final RqFallback req)
                    throws IOException {
                    FbSlf4j.log(req);
                    return new Opt.Empty<Response>();
                }
            }
        );
    }

    /**
     * Log this request.
     * @param req Request
     * @throws IOException If fails
     */
    private static void log(final RqFallback req) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Throwable error = req.throwable();
        final PrintStream stream = new PrintStream(
            baos, false, StandardCharsets.UTF_8.toString()
        );
        try {
            error.printStackTrace(stream);
        } finally {
            stream.close();
        }
        FbSlf4j.LOGGER.error(
            "{} {} failed with {}: {}",
            new RqMethod.Base(req).method(),
            new RqHref.Base(req).href(),
            req.code(),
            baos.toString("UTF-8")
        );
    }

}
