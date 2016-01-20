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
package org.takes.rs.xe;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.SyntaxException;

/**
 * Chain of directives.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "directives")
public final class XeDirectives implements XeSource {

    /**
     * Items.
     */
    private final transient Iterable<Directive> directives;

    /**
     * Ctor.
     * @param dirs Directives
     */
    public XeDirectives(final String... dirs) {
        this(XeDirectives.transform(Arrays.asList(dirs)));
    }

    /**
     * Ctor.
     * @param dirs Directives
     */
    public XeDirectives(final Directive... dirs) {
        this(Arrays.asList(dirs));
    }

    /**
     * Ctor.
     * @param dirs Directives
     */
    public XeDirectives(final Iterable<Directive> dirs) {
        this.directives = dirs;
    }

    @Override
    public Iterable<Directive> toXembly() {
        return this.directives;
    }

    /**
     * Transform strings to directives.
     * @param texts Texts
     * @return Directives
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Iterable<Directive> transform(final Iterable<String> texts) {
        final Collection<Directive> list = new LinkedList<Directive>();
        for (final String text : texts) {
            try {
                for (final Directive dir : new Directives(text)) {
                    list.add(dir);
                }
            } catch (final SyntaxException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return list;
    }

}
