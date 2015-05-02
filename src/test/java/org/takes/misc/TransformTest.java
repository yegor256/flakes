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

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Transform}.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
public final class TransformTest {

    /**
     * Transform can transform list.
     */
    @Test
    public void transformList() {
        final List<String> alist = new ArrayList<String>(3);
        alist.add("a1");
        alist.add("b1");
        alist.add("c1");
        MatcherAssert.assertThat(
            new Transform<String, String>(
                alist,
                new TransformAction<String, String>() {
                    @Override
                    public String transform(final String element) {
                        return element.concat("t");
                    }
                }
            ),
            Matchers.hasItems("a1t", "b1t", "c1t")
        );
    }

}
