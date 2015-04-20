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
package org.takes.rq;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.misc.Sprintf;
import org.takes.misc.VerboseIterable;

/**
 * HTTP headers parsing
 * <p/>
 * <p>All implementations of this interface must be immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
public interface RqHeaders extends Request {

    /**
     * Get single header.
     *
     * @param key Header name
     * @return List of values (can be empty)
     * @throws IOException If fails
     */
    Iterable<String> header(CharSequence key) throws IOException;

    /**
     * Get all header names.
     *
     * @return All names
     * @throws IOException If fails
     */
    Iterable<String> names() throws IOException;

    /**
     * Request decorator, for HTTP headers parsing.
     * <p/>
     * <p>The class is immutable and thread-safe.
     *
     * @author Yury Lauresh (lavresh@gmail.com)
     * @version $Id$
     * @since 0.13.8
     */
    @EqualsAndHashCode(callSuper = true)
    final class Base extends RqWrap implements RqHeaders {
        /**
         * Ctor.
         *
         * @param req Original request
         */
        public Base(final Request req) {
            super(req);
        }

        @Override
        public Iterable<String> header(final CharSequence key)
            throws IOException {
            final Map<String, List<String>> map = this.map();
            final List<String> values = map.get(
                key.toString().toLowerCase(Locale.ENGLISH)
            );
            final Iterable<String> iter;
            if (values == null) {
                iter = new VerboseIterable<String>(
                    Collections.<String>emptyList(),
                    new Sprintf(
                        // @checkstyle LineLengthCheck (1 line)
                        "there are no headers by name \"%s\" among %d others: %s",
                        key, map.size(), map.keySet()
                    )
                );
            } else {
                iter = new VerboseIterable<String>(
                    values,
                    new Sprintf(
                        "there are only %d headers by name \"%s\"",
                        values.size(), key
                    )
                );
            }
            return iter;
        }

        @Override
        public Iterable<String> names() throws IOException {
            return this.map().keySet();
        }

        /**
         * Parse them all in a map.
         *
         * @return Map of them
         * @throws IOException If fails
         */
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        private Map<String, List<String>> map() throws IOException {
            final Iterator<String> head = this.head().iterator();
            if (!head.hasNext()) {
                throw new IOException(
                    "a valid request must contain at least one line in the head"
                );
            }
            head.next();
            final ConcurrentMap<String, List<String>> map =
                new ConcurrentHashMap<String, List<String>>(0);
            while (head.hasNext()) {
                final String line = head.next();
                final String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    throw new IOException(
                        String.format("invalid HTTP header: \"%s\"", line)
                    );
                }
                final String key = parts[0].trim().toLowerCase(Locale.ENGLISH);
                map.putIfAbsent(key, new LinkedList<String>());
                map.get(key).add(parts[1].trim());
            }
            return map;
        }
    }

}
