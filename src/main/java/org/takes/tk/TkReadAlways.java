/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Yegor Bugayenko
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

import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqChunk;
import org.takes.rq.RqLengthAware;

/**
 * A Take decorator which reads and ignores the request body.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dan Baleanu (dan.baleanu@gmail.com)
 * @version $Id$
 * @since 0.30
 */
@ToString(of = {"origin"})
@EqualsAndHashCode
public final class TkReadAlways implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkReadAlways(final Take take) {
        this.origin = take;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final Request reqsafe = new RqChunk(new RqLengthAware(req));
        final Response res = this.origin.act(reqsafe);
        final InputStream body = reqsafe.body();
        for (int count = body.available(); count > 0;
            count = body.available()) {
            if (body.skip((long) count) < (long) count) {
                break;
            }
        }
        return res;
    }

}
