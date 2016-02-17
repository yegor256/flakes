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
package org.takes.misc;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * HTTP URI/HREF.
 *
 * <p>The class is immutable and thread-safe.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.7
 */
@SuppressWarnings
    (
        {
            "PMD.TooManyMethods",
            "PMD.OnlyOneConstructorShouldDoInitialization"
        }
    )
public final class Href implements CharSequence {

    /**
     * URI (without the query part).
     */
    private final transient URI uri;

    /**
     * Params.
     */
    private final transient SortedMap<String, List<String>> params;

    /**
     * Ctor.
     */
    public Href() {
        this("/");
    }

    /**
     * Ctor.
     * @param txt Text of the link
     * @todo #558:30min Href ctor. According to new qulice version, constructor
     *  must contain only variables initialization and other constructor calls.
     *  Refactor code according to that rule and remove
     *  `ConstructorOnlyInitializesOrCallOtherConstructors`
     *  warning suppression.
     */
    @SuppressWarnings
        (
            {
                "PMD.AvoidInstantiatingObjectsInLoops",
                "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
            }
        )
    public Href(final CharSequence txt) {
        this.params = new TreeMap<String, List<String>>();
        final URI link = Href.createURI(txt.toString());
        final String query = link.getRawQuery();
        if (query == null) {
            this.uri = link;
        } else {
            final String href = link.toString();
            this.uri = URI.create(
                href.substring(0, href.length() - query.length() - 1)
            );
            final String[] pairs = query.split("&");
            for (final String pair : pairs) {
                final String[] parts = pair.split("=", 2);
                final String key = Href.decode(parts[0]);
                final String value;
                if (parts.length > 1) {
                    value = Href.decode(parts[1]);
                } else {
                    value = "";
                }
                if (!this.params.containsKey(key)) {
                    this.params.put(key, new LinkedList<String>());
                }
                this.params.get(key).add(value);
            }
        }
    }

    /**
     * Ctor.
     * @param link The link
     * @param map Map of params
     */
    private Href(final URI link,
        final SortedMap<String, List<String>> map) {
        this.uri = link;
        this.params = map;
    }

    @Override
    public int length() {
        return this.toString().length();
    }

    @Override
    public char charAt(final int index) {
        return this.toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.toString().subSequence(start, end);
    }

    @Override
    public String toString() {
        final StringBuilder text = new StringBuilder(this.bare());
        if (!this.params.isEmpty()) {
            boolean first = true;
            for (final Map.Entry<String, List<String>> ent
                : this.params.entrySet()) {
                for (final String value : ent.getValue()) {
                    if (first) {
                        text.append('?');
                        first = false;
                    } else {
                        text.append('&');
                    }
                    text.append(Href.encode(ent.getKey()));
                    if (!value.isEmpty()) {
                        text.append('=').append(Href.encode(value));
                    }
                }
            }
        }
        return text.toString();
    }

    /**
     * Get path part of the HREF.
     * @return Path
     * @since 0.9
     */
    public String path() {
        return this.uri.getPath();
    }

    /**
     * Get URI without params.
     * @return Bare URI
     * @since 0.14
     */
    public String bare() {
        final StringBuilder text = new StringBuilder(this.uri.toString());
        if (this.uri.getPath().isEmpty()) {
            text.append('/');
        }
        return text.toString();
    }

    /**
     * Get query param.
     * @param key Param name
     * @return Values (could be empty)
     * @since 0.9
     */
    public Iterable<String> param(final Object key) {
        final List<String> values = this.params.get(key.toString());
        final Iterable<String> iter;
        if (values == null) {
            iter = new VerboseIterable<String>(
                Collections.<String>emptyList(),
                String.format(
                    "there are no URI params by name \"%s\" among %d others",
                    key, this.params.size()
                )
            );
        } else {
            iter = new VerboseIterable<String>(
                values,
                String.format(
                    "there are only %d URI params by name \"%s\"",
                    values.size(), key
                )
            );
        }
        return iter;
    }

    /**
     * Add this path to the URI.
     * @param suffix The suffix
     * @return New HREF
     */
    public Href path(final Object suffix) {
        return new Href(
            URI.create(
                new StringBuilder(this.uri.toString().replaceAll("/$", ""))
                    .append('/')
                    .append(Href.encode(suffix.toString())).toString()
            ),
            this.params
        );
    }

    /**
     * Add this extra param.
     * @param key Key of the param
     * @param value The value
     * @return New HREF
     */
    public Href with(final Object key, final Object value) {
        final SortedMap<String, List<String>> map =
            new TreeMap<String, List<String>>(this.params);
        if (!map.containsKey(key.toString())) {
            map.put(key.toString(), new LinkedList<String>());
        }
        map.get(key.toString()).add(value.toString());
        return new Href(this.uri, map);
    }

    /**
     * Without this query param.
     * @param key Key of the param
     * @return New HREF
     */
    public Href without(final Object key) {
        final SortedMap<String, List<String>> map =
            new TreeMap<String, List<String>>(this.params);
        map.remove(key.toString());
        return new Href(this.uri, map);
    }

    /**
     * Encode into URL.
     * @param txt Text
     * @return Encoded
     */
    private static String encode(final String txt) {
        try {
            return URLEncoder.encode(
                txt, Charset.defaultCharset().name()
            );
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Decode from URL.
     * @param txt Text
     * @return Decoded
     */
    private static String decode(final String txt) {
        try {
            return URLDecoder.decode(
                txt, Charset.defaultCharset().name()
            );
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Parses the specified content to create the corresponding {@code URI}
     * instance. In case of an {@code URISyntaxException}, it will automatically
     * encode the character that causes the issue then it will try again
     * if it is possible otherwise an {@code IllegalArgumentException} will
     * be thrown.
     * @param txt The content to parse
     * @return The {@code URI} corresponding to the specified content.
     * @throws IllegalArgumentException in case the content could not be parsed
     * @throws IllegalStateException in case an invalid character could not be
     *  encoded properly.
     */
    private static URI createURI(final String txt) {
        URI result;
        try {
            result = new URI(txt);
        } catch (final URISyntaxException ex) {
            final int index = ex.getIndex();
            if (index == -1) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
            final StringBuilder value = new StringBuilder(txt);
            value.replace(
                index,
                index + 1,
                Href.encode(value.substring(index, index + 1))
            );
            result = Href.createURI(value.toString());
        }
        return result;
    }
}
