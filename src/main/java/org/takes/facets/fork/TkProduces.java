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
package org.takes.facets.fork;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.tk.TkWrap;

/**
 * Take that acts on request with specified "Accept" HTTP headers only.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Eugene Kondrashev (eugene.kondrashev@gmail.com)
 * @version $Id$
 * @since 0.14
 */
@EqualsAndHashCode(callSuper = true)
public final class TkProduces extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     * @param types Accept types
     */
    public TkProduces(final Take take, final String types) {
        super(
            new Take() {
                @Override
                public Response act(final Request req) throws IOException {
                    return new RsFork(
                        req,
                        new FkTypes(types, take.act(req))
                    );
                }
            }
        );
    }

}
