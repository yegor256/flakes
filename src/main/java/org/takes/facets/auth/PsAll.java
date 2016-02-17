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
package org.takes.facets.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.takes.Request;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * A Pass which you can enter only if you can enter every Pass in a list.
 * @author Georgy Vlasov (wlasowegor@gmail.com)
 * @version $Id$
 * @since 0.22
 */
public class PsAll implements Pass {

    /**
     * Passes that have to be entered.
     */
    private final transient List<? extends Pass> all;

    /**
     * Index of identity to return.
     */
    private final transient int index;

    /**
     * Ctor.
     * @param passes All Passes to be checked.
     * @param identity Index of a Pass whose Identity to return on successful
     *  {@link PsAll#enter(Request)}
     * @todo #558:30min PsAll ctor. According to new qulice version, constructor
     *  must contain only variables initialization and other constructor calls.
     *  Refactor code according to that rule and remove
     *  `ConstructorOnlyInitializesOrCallOtherConstructors`
     *  warning suppression.
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    public PsAll(final List<? extends Pass> passes, final int identity) {
        this.all = new ArrayList<Pass>(passes);
        this.index = identity;
        if (this.index < 0) {
            throw new IllegalArgumentException("Index must be >= 0");
        }
        if (this.index >= this.all.size()) {
            throw new IllegalArgumentException(
                String.format(
                    "Trying to return index %s from a list of %s passes",
                    this.index,
                    this.all.size()
                )
            );
        }
    }

    @Override
    public final Opt<Identity> enter(final Request request) throws IOException {
        final Opt<Identity> result;
        if (this.allMatch(request)) {
            result = this.all.get(this.index).enter(request);
        } else {
            result = new Opt.Empty<Identity>();
        }
        return result;
    }

    @Override
    public final Response exit(final Response response, final Identity identity)
        throws IOException {
        if (this.index >= this.all.size()) {
            throw new IOException(
                "Index of identity is greater than Pass collection size"
            );
        }
        return this.all.get(this.index).exit(response, identity);
    }

    /**
     * Checks if you can enter every Pass with a request.
     * @param request Request that is used to enter Passes.
     * @return True if every request can be entered, false otherwise
     * @throws IOException If any of enter attempts fail
     */
    private boolean allMatch(final Request request) throws IOException {
        boolean success = true;
        for (final Pass pass : this.all) {
            if (!pass.enter(request).has()) {
                success = false;
                break;
            }
        }
        return success;
    }
}
