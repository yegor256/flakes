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
package org.takes.http;

import java.io.IOException;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqWithHeader;

/**
 * Front with a command line interface.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(of = { "take", "options" })
public final class FtCLI implements Front {

    /**
     * Take.
     */
    private final transient Take take;

    /**
     * Command line args.
     */
    private final transient Options options;

    /**
     * Ctor.
     * @param tks Take
     * @param args Arguments
     */
    public FtCLI(final Take tks, final String... args) {
        this(tks, Arrays.asList(args));
    }

    /**
     * Ctor.
     * @param tks Take
     * @param args Arguments
     */
    public FtCLI(final Take tks, final Iterable<String> args) {
        this.take = tks;
        this.options = new Options(args);
    }

    @Override
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void start(final Exit exit) throws IOException {
        final Take tks;
        if (this.options.hitRefresh()) {
            tks = new Take() {
                @Override
                public Response act(final Request request) throws IOException {
                    return FtCLI.this.take.act(
                        new RqWithHeader(
                            request, "X-Take-HitRefresh: yes"
                        )
                    );
                }
            };
        } else {
            tks = this.take;
        }
        final Front front = new FtBasic(
            new BkParallel(
                new BkSafe(new BkBasic(tks)),
                this.options.threads()
            ),
            this.options.port()
        );
        final Exit ext = this.exit(exit);
        if (this.options.isDaemon()) {
            final Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            front.start(ext);
                        } catch (final IOException ex) {
                            throw new IllegalStateException(ex);
                        }
                    }
                }
            );
            thread.setDaemon(true);
            thread.start();
        } else {
            front.start(ext);
        }
    }

    /**
     * Create exit.
     * @param exit Original exit
     * @return New exit
     */
    private Exit exit(final Exit exit) {
        final long start = System.currentTimeMillis();
        final long max = this.options.lifetime();
        final Exit custom = new Exit() {
            @Override
            public boolean ready() {
                return System.currentTimeMillis() - start > max;
            }
        };
        return new Exit() {
            @Override
            public boolean ready() {
                return exit.ready() || custom.ready();
            }
        };
    }

}
