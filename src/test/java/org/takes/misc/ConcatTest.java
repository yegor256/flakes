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
 * Test case for {@link Concat}.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.32.1
 */
public final class ConcatTest {

    /**
     * Basic concat unit test.
     */
    @Test
    public void concat() {
        final List<String> alist = new ArrayList<String>(2);
        final String aone = "a1";
        final String atwo = "a2";
        alist.add(aone);
        alist.add(atwo);
        final List<String> blist = new ArrayList<String>(2);
        final String bone = "b1";
        final String btwo = "b2";
        blist.add(bone);
        blist.add(btwo);
        MatcherAssert.assertThat(
                (Iterable<String>) new Concat<String>(alist, blist),
                Matchers.hasItems(aone, atwo, bone, btwo)
        );
    }

    /**
     * Concat test with empty inputs.
     */
    @Test
    public void concatWithEmpty() {
        final List<String> alist = new ArrayList<String>(2);
        final String aone = "an1";
        final String atwo = "an2";
        alist.add(aone);
        alist.add(atwo);
        final List<String> blist = new ArrayList<String>(0);
        MatcherAssert.assertThat(
                (Iterable<String>) new Concat<String>(alist, blist),
                Matchers.hasItems(aone, atwo)
        );
        MatcherAssert.assertThat(
                (Iterable<String>) new Concat<String>(alist, blist),
                Matchers.not(Matchers.hasItems(""))
        );
        MatcherAssert.assertThat(
                (Iterable<String>) new Concat<String>(blist, blist),
                Matchers.emptyIterable()
        );
    }

    /**
     * Concat test with condition.
     */
    @Test
    public void concatWithCondition() {
        final List<String> alist = new ArrayList<String>(2);
        final String aone = "at1";
        final String atwo = "at2";
        alist.add(aone);
        alist.add(atwo);
        final List<String> blist = new ArrayList<String>(2);
        final String bone = "bt1";
        final String btwo = "bt2";
        blist.add(bone);
        blist.add(btwo);
        Iterable<String> result = new Concat<String>(
            alist,
            blist,
            new Concat.Condition<String>() {
                @Override
                public boolean add(final String element) {
                    return element.endsWith("1");
                }
            }
        );
        MatcherAssert.assertThat(result, Matchers.hasItems(aone, bone));
        MatcherAssert.assertThat(
            result,
            Matchers.not(Matchers.hasItems(atwo, btwo))
        );
    }

}
