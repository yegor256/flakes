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
package org.takes.rs.xe;

import lombok.EqualsAndHashCode;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Xembly source to create an Atom LINK element.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class XeLink extends XeWrap {

    /**
     * Ctor.
     * @param related Related
     * @param link HREF
     */
    public XeLink(final CharSequence related, final CharSequence link) {
        this(related, link, "text/xml");
    }

    /**
     * Ctor.
     * @param rel Related
     * @param href HREF
     * @param type Content type
     */
    public XeLink(final CharSequence rel, final CharSequence href,
        final CharSequence type) {
        super(
            new XeSource() {
                @Override
                public Iterable<Directive> toXembly() {
                    return new Directives().addIf("links").add("link")
                        .attr("rel", rel.toString())
                        .attr("href", href.toString())
                        .attr("type", type.toString());
                }
            }
        );
    }

}
