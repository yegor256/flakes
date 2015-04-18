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
package org.takes.facets.forward;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Response;
import org.takes.misc.Sprintf;
import org.takes.rs.RsEmpty;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithoutHeader;

/**
 * Forwarding response.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true, of = "origin")
public final class RsForward extends HttpException implements Response {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 7676888610908953700L;

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Ctor.
     */
    public RsForward() {
        this(new RsEmpty());
    }

    /**
     * Ctor.
     * @param res Original response
     */
    public RsForward(final Response res) {
        this(res, "/");
    }

    /**
     * Ctor.
     * @param res Original response
     * @since 0.14
     */
    public RsForward(final RsForward res) {
        this(res.origin);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param loc Location
     */
    public RsForward(final Response res, final CharSequence loc) {
        this(res, HttpURLConnection.HTTP_SEE_OTHER, loc);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param loc Location
     * @since 0.14
     */
    public RsForward(final RsForward res, final CharSequence loc) {
        this(res.origin, loc);
    }

    /**
     * Ctor.
     * @param loc Location
     */
    public RsForward(final CharSequence loc) {
        this(HttpURLConnection.HTTP_SEE_OTHER, loc);
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param loc Location
     */
    public RsForward(final int code, final CharSequence loc) {
        this(new RsEmpty(), code, loc);
    }

    /**
     * Ctor.
     * @param res Original
     * @param code HTTP status code
     * @param loc Location
     * @since 0.14
     */
    public RsForward(final RsForward res, final int code,
        final CharSequence loc) {
        this(res.origin, code, loc);
    }

    /**
     * Ctor.
     * @param res Original
     * @param code HTTP status code
     * @param loc Location
     */
    public RsForward(final Response res, final int code,
        final CharSequence loc) {
        super(code, res.toString());
        this.origin = new RsWithHeader(
            new RsWithoutHeader(
                new RsWithStatus(res, code),
                "Location"
            ),
            new Sprintf("Location: %s", loc)
        );
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
