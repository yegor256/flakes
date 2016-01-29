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
package org.takes.rs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Response with properly indented XML body.
 *
 * <p>The class is immutable and thread-safe.
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@ToString(of = "origin")
@EqualsAndHashCode(of = "origin")
public final class RsPrettyXML implements Response {

    /**
     * Xerces feature to disable external DTD validation.
     */
    private static final String LOAD_EXTERNAL_DTD =
        "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Response with properly transformed body.
     */
    private final transient List<Response> transformed;

    /**
     * Ctor.
     * @param res Original response
     */
    public RsPrettyXML(final Response res) {
        this.transformed = new CopyOnWriteArrayList<Response>();
        this.origin = res;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.make().head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.make().body();
    }

    /**
     * Make a response.
     * @return Response just made
     * @throws IOException If fails
     */
    private Response make() throws IOException {
        synchronized (this.transformed) {
            if (this.transformed.isEmpty()) {
                this.transformed.add(
                    new RsWithBody(
                        this.origin,
                        RsPrettyXML.transform(this.origin.body())
                    )
                );
            }
        }
        return this.transformed.get(0);
    }

    /**
     * Format body with proper indents using SAX.
     * @param body Response body
     * @return New properly formatted body
     * @throws IOException If fails
     */
    private static byte[] transform(final InputStream body) throws IOException {
        final SAXSource source = new SAXSource(new InputSource(body));
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            final XMLReader xmlreader = SAXParserFactory.newInstance()
                .newSAXParser().getXMLReader();
            source.setXMLReader(xmlreader);
            xmlreader.setFeature(
                RsPrettyXML.LOAD_EXTERNAL_DTD, false
            );
            final Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
            // @checkstyle MultipleStringLiteralsCheck (2 line)
            transformer.setOutputProperty(
                OutputKeys.OMIT_XML_DECLARATION, "yes"
            );
            RsPrettyXML.prepareDocType(body, transformer);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, new StreamResult(result));
        } catch (final TransformerException ex) {
            throw new IOException(ex);
        } catch (final SAXException ex) {
            throw new IOException(ex);
        } catch (final ParserConfigurationException ex) {
            throw new IOException(ex);
        }
        return result.toByteArray();
    }

    /**
     * Parses body to get DOCTYPE and configure Transformer
     * with proper method, public id and system id.
     * @param body The body to be parsed.
     * @param transformer Transformer to configure with proper properties.
     * @throws IOException if something goes wrong.
     */
    private static void prepareDocType(final InputStream body,
        final Transformer transformer) throws IOException {
        try {
            final DocumentType doctype = RsPrettyXML.getDocType(body);
            if (null != doctype) {
                if (null == doctype.getSystemId()
                    && null == doctype.getPublicId()
                    // @checkstyle MultipleStringLiteralsCheck (3 line)
                    && "html".equalsIgnoreCase(doctype.getName())) {
                    transformer.setOutputProperty(OutputKeys.METHOD, "html");
                    transformer.setOutputProperty(OutputKeys.VERSION, "5.0");
                    return;
                }
                if (null != doctype.getSystemId()) {
                    transformer.setOutputProperty(
                        OutputKeys.DOCTYPE_SYSTEM,
                        doctype.getSystemId()
                    );
                }
                if (null != doctype.getPublicId()) {
                    transformer.setOutputProperty(
                        OutputKeys.DOCTYPE_PUBLIC,
                        doctype.getPublicId()
                    );
                }
            }
        } finally {
            body.reset();
        }
    }

    /**
     * Parses the input stream and returns DocumentType built without loading
     * any external DTD schemas.
     * @param body The body to be parsed.
     * @return The documents DocumentType.
     * @throws IOException if something goes wrong.
     */
    private static DocumentType getDocType(final InputStream body)
        throws IOException {
        final DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature(RsPrettyXML.LOAD_EXTERNAL_DTD, false);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(body).getDoctype();
        } catch (final ParserConfigurationException ex) {
            throw new IOException(ex);
        } catch (final SAXException ex) {
            throw new IOException(ex);
        }
    }

}
