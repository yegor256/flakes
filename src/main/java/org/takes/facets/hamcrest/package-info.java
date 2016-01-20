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

/**
 * Matchers.
 *
 * <p>This package contains Hamcrest matchers for all key interfaces
 * in the framework. Use them as in this example:
 *
 * <pre> public class FooTest {
 *   &#64;Test
 *   public void returnsOK() {
 *     final Response response = new TkIndex().act(new RqFake());
 *     MatcherAssert.assertThat(
 *       response,
 *       new HmStatus(Matchers.equalTo(HttpURLConnection.HTTP_OK))
 *     );
 *   }
 * }</pre>
 *
 * @author Erim Erturk (erimerturk@gmail.com)
 * @version $Id$
 * @since 0.13
 */
package org.takes.facets.hamcrest;
