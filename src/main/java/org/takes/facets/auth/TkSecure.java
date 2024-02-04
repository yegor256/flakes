/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2022 Yegor Bugayenko
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
package org.takes.facets.auth;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;

import java.net.HttpURLConnection;
import java.util.logging.Level;

/**
 * Take available for authenticated users.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(of = {"origin", "loc"})
@EqualsAndHashCode
public final class TkSecure implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Location where to forward.
     */
    private final String loc;

    /**
     * Ctor.
     *
     * @param take Original
     * @since 0.10
     */
    public TkSecure(final Take take) {
        this(take, "/");
    }

    /**
     * Ctor.
     *
     * @param take     Original
     * @param location Where to forward
     */
    public TkSecure(final Take take, final String location) {
        this.origin = take;
        this.loc = location;
    }

    @Override
    public Response act(final Request request) throws Exception {
        if (new RqAuth(request).identity().equals(Identity.ANONYMOUS)) {
            throw new RsForward(
                    new RsFlash("access denied", Level.WARNING),
                    HttpURLConnection.HTTP_UNAUTHORIZED,
                    this.loc
            );
        }
        return this.origin.act(request);
    }

}
