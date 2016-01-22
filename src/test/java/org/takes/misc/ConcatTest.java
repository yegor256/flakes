/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Yegor Bugayenko
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

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Concat}.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.15.2
 */
public final class ConcatTest {

    /**
     * Concat can concatenate.
     */
    @Test
    public void concatenates() {
        MatcherAssert.assertThat(
            Joiner.on(" ").join(
                new Concat<String>(
                    Arrays.asList("one", "two"),
                    Arrays.asList("three", "four")
                )
            ),
            Matchers.equalTo("one two three four")
        );
    }

    /**
     * Concat can concatenate with empty list.
     */
    @Test
    public void concatenatesWithEmptyList() {
        MatcherAssert.assertThat(
            Joiner.on("+").join(
                new Concat<String>(
                    Arrays.asList("five", "six"),
                    Collections.<String>emptyList()
                )
            ),
            Matchers.equalTo("five+six")
        );
    }

}
