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
package org.takes.rq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Input stream from chunked coded http request body.
 *
 * @author Maksimenko Vladimir (xupypr@xupypr.com)
 * @version $Id$
 * @since 0.31.2
 * @checkstyle LineLengthCheck (1 lines)
 * @link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.6.1">Chunked Transfer Coding</a>
 */
final class ChunkedInputStream extends InputStream {

    /**
     * The inputstream that we're wrapping.
     */
    private final transient InputStream origin;

    /**
     * The chunk size.
     */
    private transient int size;

    /**
     * The current position within the current chunk.
     */
    private transient int pos;

    /**
     * True if we'are at the beginning of stream.
     */
    private transient boolean bof;

    /**
     * True if we've reached the end of stream.
     */
    private transient boolean eof;

    /**
     * Ctor.
     * @param stream The raw input stream
     * @throws IOException If an IO error occurs
     */
    ChunkedInputStream(final InputStream stream) throws IOException {
        super();
        this.bof = true;
        this.origin = stream;
    }

    @Override
    public int read() throws IOException {
        if (!this.eof && this.pos >= this.size) {
            this.nextChunk();
        }
        final int result;
        if (this.eof) {
            result = -1;
        } else {
            ++this.pos;
            result = this.origin.read();
        }
        return result;
    }

    @Override
    public int read(final byte[] buf, final int off, final int len)
        throws IOException {
        if (!this.eof && this.pos >= this.size) {
            this.nextChunk();
        }
        final int result;
        if (this.eof) {
            result = -1;
        } else {
            final int shift = Math.min(len, this.size - this.pos);
            final int count = this.origin.read(buf, off, shift);
            this.pos += count;
            if (shift == len) {
                result = len;
            } else {
                result = shift + this.read(buf, off + shift, len - shift);
            }
        }
        return result;
    }

    @Override
    public int read(final byte[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    /**
     * Read the CRLF terminator.
     * @throws IOException If an IO error occurs.
     */
    private void readCRLF() throws IOException {
        final int crsymbol = this.origin.read();
        final int lfsymbol = this.origin.read();
        if (crsymbol != '\r' || lfsymbol != '\n') {
            throw new IOException(
                String.format(
                    "%s %d%s%d",
                    "CRLF expected at end of chunk: ",
                    crsymbol,
                    "/",
                    lfsymbol
                )
            );
        }
    }

    /**
     * Read the next chunk.
     * @throws IOException If an IO error occurs.
     */
    private void nextChunk() throws IOException {
        if (!this.bof) {
            this.readCRLF();
        }
        this.size = ChunkedInputStream.chunkSize(this.origin);
        this.bof = false;
        this.pos = 0;
        if (this.size == 0) {
            this.eof = true;
        }
    }

    /**
     * Expects the stream to start with a chunksize in hex with optional
     * comments after a semicolon. The line must end with a CRLF: "a3; some
     * comment\r\n" Positions the stream at the start of the next line.
     * @param stream The new input stream.
     * @return The chunk size as integer
     * @throws IOException when the chunk size could not be parsed
     */
    private static int chunkSize(final InputStream stream)
        throws IOException {
        final ByteArrayOutputStream baos = ChunkedInputStream.sizeLine(stream);
        final int result;
        final String data = baos.toString(Charset.defaultCharset().name());
        final int separator = data.indexOf(';');
        try {
            // @checkstyle MagicNumberCheck (10 lines)
            if (separator > 0) {
                result = Integer.parseInt(
                    data.substring(0, separator).trim(),
                    16
                );
            } else {
                result = Integer.parseInt(data.trim(), 16);
            }
            return result;
        } catch (final NumberFormatException ex) {
            throw new IOException(
                String.format(
                    "Bad chunk size: %s",
                    baos.toString(Charset.defaultCharset().name())
                ),
                ex
            );
        }
    }

    /**
     * Possible states of FSM that used to find chunk size.
     */
    private enum State {
        /**
         * Normal.
         */
        NORMAL,
        /**
         * If \r was scanned.
         */
        R,
        /**
         * Inside quoted string.
         */
        QUOTED_STRING,
        /**
         * End.
         */
        END;
    }

    /**
     * Extract line with chunk size from stream.
     * @param stream Input stream.
     * @return Line with chunk size.
     * @throws IOException If fails.
     */
    private static ByteArrayOutputStream sizeLine(final InputStream stream)
        throws IOException {
        State state = State.NORMAL;
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        while (state != State.END) {
            state = next(stream, state, result);
        }
        return result;
    }

    /**
     * Get next state for FSM.
     * @param stream Input stream.
     * @param state Current state.
     * @param line Current chunk size line.
     * @return New state.
     * @throws IOException If fails.
     */
    private static State next(final InputStream stream, final State state,
        final ByteArrayOutputStream line) throws IOException {
        final int next = stream.read();
        if (next == -1) {
            throw new IOException("chunked stream ended unexpectedly");
        }
        final State result;
        switch (state) {
            case NORMAL:
                result = nextNormal(state, line, next);
                break;
            case R:
                if (next == '\n') {
                    result = State.END;
                } else {
                    throw new IOException(
                        String.format(
                            "%s%s",
                            "Protocol violation: Unexpected",
                            " single newline character in chunk size"
                        )
                    );
                }
                break;
            case QUOTED_STRING:
                result = nextQuoted(stream, state, line, next);
                break;
            default:
                throw new IllegalStateException("Bad state");
        }
        return result;
    }

    /**
     * Maintain next symbol for current state = State.NORMAL.
     * @param state Current state.
     * @param line Current chunk size line.
     * @param next Next symbol.
     * @return New state.
     */
    private static State nextNormal(final State state,
        final ByteArrayOutputStream line, final int next) {
        final State result;
        switch (next) {
            case '\r':
                result = State.R;
                break;
            case '\"':
                result = State.QUOTED_STRING;
                break;
            default:
                result = state;
                line.write(next);
                break;
        }
        return result;
    }

    /**
     * Maintain next symbol for current state = State.QUOTED_STRING.
     * @param stream Input stream.
     * @param state Current state.
     * @param line Current chunk size line.
     * @param next Next symbol.
     * @return New state.
     * @throws IOException If fails.
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    private static State nextQuoted(final InputStream stream, final State state,
        final ByteArrayOutputStream line, final int next)
            throws IOException {
        final State result;
        switch (next) {
            case '\\':
                result = state;
                line.write(stream.read());
                break;
            case '\"':
                result = State.NORMAL;
                break;
            default:
                result = state;
                line.write(next);
                break;
        }
        return result;
    }
}
