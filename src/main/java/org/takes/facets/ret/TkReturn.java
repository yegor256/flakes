/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015, 2016 Yegor Bugayenko
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
package org.takes.facets.ret;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqCookies;
import org.takes.rs.RsRedirect;
import org.takes.rs.RsWithCookie;

/**
 * Take that understands Return cookie. If Return cookie
 * is set, sends redirect response to stored location.
 * Otherwise delegates to original Take.
 *
 * @author Ivan Inozemtsev (ivan.inozemtsev@gmail.com)
 * @version $Id$
 * @since 0.20
 */
@ToString(of = { "origin", "cookie" })
@EqualsAndHashCode(of = { "origin", "cookie" })
public final class TkReturn implements Take {

    /**
     * Original take.
     */
    private final transient Take origin;

    /**
     * Cookie name.
     */
    private final transient String cookie;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkReturn(final Take take) {
        this(take, RsReturn.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param take Original take
     * @param name Cookie name
     */
    public TkReturn(final Take take, final String name) {
        this.origin = take;
        this.cookie = name;
    }

    @Override
    public Response act(final Request request) throws IOException {
        final RqCookies cookies = new RqCookies.Base(request);
        final Iterator<String> values = cookies.cookie(this.cookie).iterator();
        final Response response;
        if (values.hasNext()) {
            response = new RsWithCookie(
                new RsRedirect(
                    URLDecoder.decode(
                        values.next(),
                        Charset.defaultCharset().name()
                    )
                ),
                this.cookie,
                ""
            );
        } else {
            response = this.origin.act(request);
        }
        return response;
    }
}
