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
import java.util.Locale;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;

/**
 * Fork by host name.
 *
 * <p>Use this class in combination with {@link TkFork},
 * for example:
 *
 * <pre> Take take = new TkFork(
 *   new FkHost("www.example.com", new TkText("home")),
 *   new FkHost("doc.example.com", new TkText("doc is here"))
 * );</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @see TkFork
 * @since 0.32
 */
@EqualsAndHashCode(callSuper = true)
public final class FkHost extends FkWrap {

    /**
     * Ctor.
     * @param host Host
     * @param take Take to use
     */
    public FkHost(final String host, final Take take) {
        super(FkHost.fork(host, take));
    }

    /**
     * Make fork.
     * @param host Host
     * @param take Take to use
     * @return Fork
     */
    private static Fork fork(final String host, final Take take) {
        return new Fork() {
            @Override
            public Opt<Response> route(final Request req) throws IOException {
                final String hst = new RqHeaders.Smart(
                    new RqHeaders.Base(req)
                ).single("host");
                final Opt<Response> rsp;
                if (host.toLowerCase(Locale.ENGLISH)
                    .equals(hst.toLowerCase(Locale.ENGLISH))) {
                    rsp = new Opt.Single<>(take.act(req));
                } else {
                    rsp = new Opt.Empty<>();
                }
                return rsp;
            }
        };
    }

}
