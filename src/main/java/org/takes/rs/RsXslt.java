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
package org.takes.rs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.io.InputStreamOf;
import org.cactoos.io.ReaderOf;
import org.cactoos.io.WriterTo;
import org.cactoos.scalar.Unchecked;
import org.takes.Response;
import org.takes.misc.Opt;

/**
 * Response that converts XML into HTML using attached XSL stylesheet.
 *
 * <p>The encapsulated response must produce an XML document with
 * an attached XSL stylesheet, for example:
 *
 * <pre>&lt;?xml version="1.0"?&gt;
 * &lt;?xml-stylesheet href="/xsl/home.xsl" type="text/xsl"?&gt;
 * &lt;page/&gt;
 * </pre>
 *
 * <p>{@link org.takes.rs.RsXslt} will try to find that {@code /xsl/home.xsl}
 * resource in classpath. If it's not found a runtime exception will thrown.
 *
 * <p>The best way to use this decorator is in combination with
 * {@link org.takes.rs.xe.RsXembly}, for example:
 *
 * <pre> new RsXSLT(
 *   new RsXembly(
 *     new XeStylesheet("/xsl/home.xsl"),
 *     new XeAppend(
 *       "page",
 *       new XeDate(),
 *       new XeLocalhost(),
 *       new XeSLA()
 *     )
 *   )
 * )</pre>
 *
 * <p><strong>Note:</strong> It is highly recommended to use
 * Saxon as a default XSL transformer. All others, including Apache
 * Xalan, won't work correctly in most cases.</p>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 * @see org.takes.rs.xe.RsXembly
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsXslt extends RsWrap {

    /**
     * Cached factory.
     */
    private static final Map<URIResolver, TransformerFactory> FACTORIES =
        new ConcurrentHashMap<>(0);

    /**
     * Ctor.
     * @param rsp Original response
     */
    public RsXslt(final Response rsp) {
        this(rsp, new RsXslt.InClasspath());
    }

    /**
     * Ctor.
     * @param rsp Original response
     * @param resolver URI resolver
     */
    public RsXslt(final Response rsp, final URIResolver resolver) {
        super(
            new RsWithHeader(
                new ResponseOf(
                    rsp::head,
                    () -> RsXslt.transform(rsp.body(), resolver)
                ),
                () -> String.format(
                    "X-Takes-RsXslt-TransformerFactory: %s",
                    RsXslt.factory(resolver).getClass().getCanonicalName()
                )
            )
        );
    }

    /**
     * Get factory for the given resolver.
     * @param resolver Resolver
     * @return Factory
     */
    private static TransformerFactory factory(final URIResolver resolver) {
        return RsXslt.FACTORIES.computeIfAbsent(
            resolver,
            res -> {
                final TransformerFactory fct = TransformerFactory.newInstance();
                fct.setURIResolver(res);
                new Unchecked<>(
                    () -> {
                        fct.setFeature(
                            XMLConstants.FEATURE_SECURE_PROCESSING,
                            true
                        );
                        return 0;
                    }).value();
                return fct;
            }
        );
    }

    /**
     * Build body.
     * @param origin Original body
     * @param resolver Resolver
     * @return Body
     * @throws IOException If fails
     */
    private static InputStream transform(final InputStream origin,
        final URIResolver resolver) throws IOException {
        final TransformerFactory fct = RsXslt.factory(resolver);
        try {
            return RsXslt.transform(fct, origin);
        } catch (final TransformerException ex) {
            throw new IOException(
                String.format(
                    "Can't transform via %s",
                    fct.getClass().getName()
                ),
                ex
            );
        }
    }

    /**
     * Transform XML into HTML.
     * @param factory Transformer factory
     * @param xml XML page to be transformed.
     * @return Resulting HTML page.
     * @throws TransformerException If fails
     */
    private static InputStream transform(final TransformerFactory factory,
        final InputStream xml) throws TransformerException {
        final byte[] input;
        try {
            input = RsXslt.consume(xml);
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Failed to consume XML by XSLT",
                ex
            );
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Source xsl = RsXslt.stylesheet(
            factory,
            new StreamSource(new ReaderOf(input))
        );
        RsXslt.transformer(factory, xsl).transform(
            new StreamSource(
                new ReaderOf(input)
            ),
            new StreamResult(
                new WriterTo(baos)
            )
        );
        return new InputStreamOf(baos.toByteArray());
    }

    /**
     * Consume input stream.
     * @param input Input stream
     * @return Bytes found
     * @throws IOException If fails
     */
    private static byte[] consume(final InputStream input) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buf = new byte[4096];
        try {
            while (true) {
                final int bytes = input.read(buf);
                if (bytes < 0) {
                    break;
                }
                baos.write(buf, 0, bytes);
            }
        } finally {
            input.close();
        }
        return baos.toByteArray();
    }

    /**
     * Retrieve a stylesheet from this XML (throws an exception if
     * no stylesheet is attached).
     * @param factory Transformer factory
     * @param xml The XML
     * @return Stylesheet found
     * @throws TransformerConfigurationException If fails
     */
    private static Source stylesheet(final TransformerFactory factory,
        final Source xml) throws TransformerConfigurationException {
        final Opt<Source> stylesheet = new Opt.Single<>(
            factory.getAssociatedStylesheet(
                xml, null, null, null
            )
        );
        if (!stylesheet.has()) {
            throw new IllegalArgumentException(
                "No associated stylesheet found in XML"
            );
        }
        return stylesheet.get();
    }

    /**
     * Make a transformer from this stylesheet.
     * @param factory Transformer factory
     * @param stylesheet The stylesheet
     * @return Transformer
     * @throws TransformerConfigurationException If fails
     */
    private static Transformer transformer(final TransformerFactory factory,
        final Source stylesheet) throws TransformerConfigurationException {
        final Transformer tnfr = factory.newTransformer(stylesheet);
        if (tnfr == null) {
            throw new TransformerConfigurationException(
                String.format(
                    "%s failed to create new XSL transformer for '%s'",
                    factory.getClass(),
                    stylesheet.getSystemId()
                )
            );
        }
        return tnfr;
    }

    /**
     * Classpath URI resolver.
     * @since 0.1
     */
    private static final class InClasspath implements URIResolver {
        @Override
        public Source resolve(final String href, final String base)
            throws TransformerException {
            final URI uri;
            if (base == null || base.isEmpty()) {
                uri = URI.create(href);
            } else {
                uri = URI.create(base).resolve(href);
            }
            final InputStream input;
            if (uri.isAbsolute() && !"file".equals(uri.getScheme())) {
                try {
                    input = uri.toURL().openStream();
                } catch (final IOException ex) {
                    throw new IllegalStateException(
                        String.format("Failed to open URL '%s'", uri),
                        ex
                    );
                }
            } else {
                input = this.getClass().getResourceAsStream(uri.getPath());
                if (input == null) {
                    throw new TransformerException(
                        String.format(
                            "\"%s\" not found in classpath, base=\"%s\"",
                            href, base
                        )
                    );
                }
            }
            return new StreamSource(
                new ReaderOf(input)
            );
        }
    }

}
