/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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
package org.takes.rq.multipart;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Lowered;
import org.cactoos.text.StartsWith;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.misc.VerboseIterable;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqLengthAware;
import org.takes.rq.RqMultipart;

/**
 * Request decorator, that decodes FORM data from
 * {@code multipart/form-data} format (RFC 2045).
 *
 * <p>For {@code application/x-www-form-urlencoded}
 * format use {@link org.takes.rq.RqForm}.
 *
 * <p>It is highly recommended to use {@link org.takes.rq.RqGreedy}
 * decorator before passing request to this class.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.33
 * @see <a href="http://www.w3.org/TR/html401/interact/forms.html">
 *  Forms in HTML</a>
 * @see org.takes.rq.RqGreedy
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@lombok.EqualsAndHashCode(of = "origin")
@SuppressWarnings("PMD.ExcessiveImports")
public final class RqMtBase implements RqMultipart {
    /**
     * The encoding used to create the request.
     */
    private static final Charset ENCODING = Charset.forName("UTF-8");

    /**
     * Pattern to get boundary from header.
     */
    private static final Pattern BOUNDARY = Pattern.compile(
        ".*[^a-z]boundary=([^;]+).*"
    );

    /**
     * Pattern to get name from header.
     */
    private static final Pattern NAME = Pattern.compile(
        ".*[^a-z]name=\"([^\"]+)\".*"
    );

    /**
     * Carriage return constant.
     */
    private static final String CRLF = "\r\n";

    /**
     * Scalar of Map of params and values.
     */
    private final Scalar<Map<String, List<Request>>> smap;

    /**
     * Scalar of Internal buffer.
     */
    private final Scalar<ByteBuffer> sbuffer;

    /**
     * Scalar of InputStream based on request body.
     */
    private final Scalar<InputStream> sstream;

    /**
     * Original request.
     */
    private final Request origin;

    /**
     * Ctor.
     * @param req Original request
     * @throws IOException If fails
     * @checkstyle ExecutableStatementCountCheck (2 lines)
     */
    public RqMtBase(final Request req) throws IOException {
        this.origin = req;
        this.sstream = new Sticky<>(() -> new RqLengthAware(req).body());
        this.sbuffer = new Sticky<>(() -> {
            return ByteBuffer.allocate(
                // @checkstyle MagicNumberCheck (1 line)
                Math.min(8192, RqMtBase.this.sstream.value().available())
            );
        });
        this.smap = new Sticky<>(() -> RqMtBase.this.requests(req));
    }

    @Override
    public Iterable<Request> part(final CharSequence name) {
        final Map<String, List<Request>> map = new Unchecked<>(this.smap).value();
        final List<Request> values = map.getOrDefault(
            new UncheckedText(
                new Lowered(name.toString())
            ).asString(),
            Collections.emptyList()
        );
        final Iterable<Request> iter;
        if (values.isEmpty()) {
            iter = new VerboseIterable<>(
                Collections.emptyList(),
                new FormattedText(
                    "there are no parts by name \"%s\" among %d others: %s",
                    name, map.size(), map.keySet()
                ).toString()
            );
        } else {
            iter = new VerboseIterable<>(
                values,
                new FormattedText(
                    "there are just %d parts by name \"%s\"",
                    values.size(), name
                ).toString()
            );
        }
        return iter;
    }

    @Override
    public Iterable<String> names() {
        return new Unchecked<>(this.smap).value().keySet();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return new CloseMultipart(this.origin.body());
    }

    /**
     * Build a request for each part of the origin request.
     * @param req Origin request
     * @return The requests map that use the part name as a map key
     * @throws IOException If fails
     */
    private Map<String, List<Request>> requests(
        final Request req) throws IOException {
        final String header = new RqHeaders.Smart(req).single("Content-Type");
        final Unchecked<Boolean> multipart = new Unchecked<>(
            new StartsWith(
                new Lowered(header),
                new TextOf("multipart/form-data")
            )
        );
        if (!multipart.value()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    // @checkstyle LineLength (1 line)
                    "RqMtBase can only parse multipart/form-data, while Content-Type specifies a different type: \"%s\"",
                    header
                )
            );
        }
        final Matcher matcher = RqMtBase.BOUNDARY.matcher(header);
        if (!matcher.matches()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "boundary is not specified in Content-Type header: \"%s\"",
                    header
                )
            );
        }
        final ByteBuffer buffer = new Unchecked<>(this.sbuffer).value();
        final InputStream stream = new Unchecked<>(this.sstream).value();
        final ReadableByteChannel body = Channels.newChannel(stream);
        if (body.read(buffer) < 0) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "failed to read the request body"
            );
        }
        final byte[] boundary = String.format(
            "%s--%s", RqMtBase.CRLF, matcher.group(1)
        ).getBytes(RqMtBase.ENCODING);
        buffer.flip();
        buffer.position(boundary.length - 2);
        final Collection<Request> requests = new LinkedList<>();
        while (buffer.hasRemaining()) {
            final byte data = buffer.get();
            if (data == '-') {
                break;
            }
            buffer.position(buffer.position() + 1);
            requests.add(this.make(boundary, body));
        }
        return RqMtBase.asMap(requests);
    }

    /**
     * Make a request.
     *  Scans the origin request until the boundary reached. Caches
     *  the  content into a temporary file and returns it as a new request.
     * @param boundary Boundary
     * @param body Origin request body
     * @return Request
     * @throws IOException If fails
     */
    private Request make(final byte[] boundary,
        final ReadableByteChannel body) throws IOException {
        final File file = File.createTempFile(
            RqMultipart.class.getName(), ".tmp"
        );
        try (WritableByteChannel channel = Files.newByteChannel(
            file.toPath(),
            StandardOpenOption.READ,
            StandardOpenOption.WRITE
        )
        ) {
            channel.write(
                ByteBuffer.wrap(
                    this.head().iterator().next().getBytes(RqMtBase.ENCODING)
                )
            );
            channel.write(
                ByteBuffer.wrap(RqMtBase.CRLF.getBytes(RqMtBase.ENCODING))
            );
            this.copy(channel, boundary, body);
        }
        return new RqTemp(file);
    }

    /**
     * Copy until boundary reached.
     * @param target Output file channel
     * @param boundary Boundary
     * @param body Origin request body
     * @throws IOException If fails
     * @checkstyle ExecutableStatementCountCheck (2 lines)
     */
    private void copy(final WritableByteChannel target,
        final byte[] boundary, final ReadableByteChannel body)
        throws IOException {
        int match = 0;
        boolean cont = true;
        final ByteBuffer buffer = new Unchecked<>(this.sbuffer).value();
        while (cont) {
            if (!buffer.hasRemaining()) {
                buffer.clear();
                for (int idx = 0; idx < match; ++idx) {
                    buffer.put(boundary[idx]);
                }
                match = 0;
                if (body.read(buffer) == -1) {
                    break;
                }
                buffer.flip();
            }
            final ByteBuffer btarget = buffer.slice();
            final int offset = buffer.position();
            btarget.limit(0);
            while (buffer.hasRemaining()) {
                final byte data = buffer.get();
                if (data == boundary[match]) {
                    ++match;
                } else if (data == boundary[0]) {
                    match = 1;
                } else {
                    match = 0;
                    btarget.limit(buffer.position() - offset);
                }
                if (match == boundary.length) {
                    cont = false;
                    break;
                }
            }
            target.write(btarget);
        }
    }

    /**
     * Convert a list of requests to a map.
     * @param reqs Requests
     * @return Map of them
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Map<String, List<Request>> asMap(
        final Collection<Request> reqs) throws IOException {
        final Map<String, List<Request>> map = new HashMap<>(reqs.size());
        for (final Request req : reqs) {
            final String header =
                new RqHeaders.Smart(req).single("Content-Disposition");
            final Matcher matcher = RqMtBase.NAME.matcher(header);
            if (!matcher.matches()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    String.format(
                        "\"name\" not found in Content-Disposition header: %s",
                        header
                    )
                );
            }
            final String name = matcher.group(1);
            if (!map.containsKey(name)) {
                map.put(name, new LinkedList<>());
            }
            map.get(name).add(req);
        }
        return map;
    }

    /**
     * Decorator allowing to close all the parts of the request.
     * @since 0.33
     */
    private class CloseMultipart extends FilterInputStream {

        /**
         * Creates a {@code CloseParts} with the specified input.
         * @param input The underlying input stream.
         */
        CloseMultipart(final InputStream input) {
            super(input);
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                for (final List<Request> requests
                    : new Unchecked<>(RqMtBase.this.smap).value().values()) {
                    for (final Request request : requests) {
                        request.body().close();
                    }
                }
            }
        }
    }
}
