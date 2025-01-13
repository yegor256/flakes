/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
package org.takes.rs.xe;

import lombok.EqualsAndHashCode;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Xembly source to create "millis" element at the root.
 *
 * <p>Add this Xembly source to your page like this:
 *
 * <pre> new RsXembly(
 *   new XeStylesheet("/xsl/home.xsl"),
 *   new XeAppend(
 *     "page",
 *     new XeMillis(),
 *     // some other Xembly elements
 *     new XeMillis(true)
 *   )
 * )</pre>
 *
 * <p>And expect this attribute in the XML:
 *
 * <pre>&lt;?xml version="1.0"?&gt;
 * &lt;?xml-stylesheet href="/xsl/home.xsl" type="text/xsl"?&gt;
 * &lt;page&gt;
 * &lt;millis&gt;3.675&lt;/millis&gt;
 * &lt;/page&gt;
 * </pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class XeMillis implements XeSource {

    /**
     * Name of element.
     */
    private final CharSequence name;

    /**
     * Is it a finish?
     */
    private final boolean finish;

    /**
     * Ctor.
     * @since 1.4
     */
    public XeMillis() {
        this(false);
    }

    /**
     * Ctor.
     * @param fin Is it the finish?
     */
    public XeMillis(final boolean fin) {
        this("millis", fin);
    }

    /**
     * Ctor.
     * @param elm Element name
     * @param fin Is it the finish?
     */
    public XeMillis(final CharSequence elm, final boolean fin) {
        this.name = elm;
        this.finish = fin;
    }

    @Override
    public Iterable<Directive> toXembly() {
        final Directives dirs = new Directives();
        if (this.finish) {
            dirs.xpath(this.name.toString())
                .strict(1)
                .xset(
                    String.format(
                        "%d - number(text())",
                        System.currentTimeMillis()
                    )
                );
        } else {
            dirs.add(this.name.toString())
                .set(Long.toString(System.currentTimeMillis()));
        }
        return dirs;
    }
}
