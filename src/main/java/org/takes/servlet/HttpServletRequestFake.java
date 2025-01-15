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
package org.takes.servlet;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import org.takes.Request;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

/**
 * Fake HttpServletRequest (for unit tests).
 *
 * @since 1.15
 */
@SuppressWarnings({
    "PMD.TooManyMethods",
    "PMD.ExcessiveImports",
    "PMD.ExcessivePublicCount"
})
public final class HttpServletRequestFake implements HttpServletRequest {
    /**
     * A Takes Request.
     */
    private final Request request;

    /**
     * Ctor.
     * @param req A Takes Request object
     */
    public HttpServletRequestFake(final Request req) {
        this.request = req;
    }

    @Override
    public String getHeader(final String key) {
        final Enumeration<String> headers = this.getHeaders(key);
        if (!headers.hasMoreElements()) {
            throw new NoSuchElementException(
                String.format(
                    "Value of header %s not found",
                    key
                )
            );
        }
        return headers.nextElement();
    }

    @Override
    public Enumeration<String> getHeaders(final String key) {
        try {
            return Collections.enumeration(
                new RqHeaders.Base(this.request).header(key)
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(
                String.format("Failed to read header '%s'", key),
                ex
            );
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        try {
            return Collections.enumeration(
                new RqHeaders.Base(this.request).names()
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(
                "Failed to parse headers in the request",
                ex
            );
        }
    }

    @Override
    public String getMethod() {
        try {
            return new RqMethod.Base(this.request).method();
        } catch (final IOException ex) {
            throw new IllegalArgumentException(
                "Failed to get method from the request",
                ex
            );
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStreamOf(this.request.body());
    }

    @Override
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public String getRemoteAddr() {
        return "127.0.0.1";
    }

    @Override
    public String getRemoteHost() {
        return "localhost";
    }

    @Override
    public String getRequestId() {
        return "1";
    }

    @Override
    public String getLocalName() {
        return "localhost";
    }

    @Override
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public String getLocalAddr() {
        return "127.0.0.1";
    }

    @Override
    public int getLocalPort() {
        return new SecureRandom().nextInt();
    }

    @Override
    public String getRequestURI() {
        try {
            return new RqHref.Base(this.request).href().path();
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Failed to get HREF from Request",
                ex
            );
        }
    }

    @Override
    public String getProtocolRequestId() {
        return "";
    }

    @Override
    public String getQueryString() {
        final String raw = this.getRequestURI();
        final URI uri;
        try {
            uri = new URI(raw);
        } catch (final URISyntaxException ex) {
            throw new IllegalStateException(
                String.format("Failed to parse URI '%s'", raw),
                ex
            );
        }
        return uri.getQuery();
    }

    @Override
    public ServletConnection getServletConnection() {
        return new ServletConnectionFake();
    }

    @Override
    public String getServerName() {
        final String raw = this.getRequestURI();
        final URI uri;
        try {
            uri = new URI(raw);
        } catch (final URISyntaxException ex) {
            throw new IllegalStateException(
                String.format("Failed to parse URI '%s'", raw),
                ex
            );
        }
        Opt<String> optHost = new Opt.Single<>(uri.getHost());
        String host;
        if (!optHost.has() || optHost.get().isEmpty()) {
            host = "localhost";
        } else {
            host = optHost.get();
        }
        return host;
    }

    @Override
    public int getServerPort() {
        final String raw = this.getRequestURI();
        final URI uri;
        try {
            uri = new URI(raw);
        } catch (final URISyntaxException ex) {
            throw new IllegalStateException(
                String.format("Failed to parse URI '%s'", raw),
                ex
            );
        }
        int port = uri.getPort();
        if (port == -1) {
            port = 80;
        }
        return port;
    }

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("#getAuthType()");
    }

    @Override
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException("#getCookies()");
    }

    @Override
    public long getDateHeader(final String key) {
        throw new UnsupportedOperationException("#getDateHeader()");
    }

    @Override
    public int getIntHeader(final String key) {
        throw new UnsupportedOperationException("#getIntHeader()");
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("#getPathInfo()");
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("#getPathTranslated()");
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException("#getContextPath()");
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("#getRemoteUser()");
    }

    @Override
    public boolean isUserInRole(final String role) {
        throw new UnsupportedOperationException("#isUserInRole()");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("#getUserPrincipal");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("#getRequestedSessionId");
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException("#getRequestURL()");
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("#getServletPath()");
    }

    @Override
    public HttpSession getSession(final boolean create) {
        throw new UnsupportedOperationException("#getSession()");
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("#getSession");
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException("#changeSessionId()");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("#isRequestedSessionIdValid()");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException(
            "#isRequestedSessionIdFromCookie()"
        );
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException(
            "#isRequestedSessionIdFromURL()"
        );
    }

    /**
     * Checks whether the requested session ID came in as part of the request
     * URL.
     * @deprecated Use isRequestedSessionIdFromURL() instead.
     * @return True, if the requested session ID came in as part of the request
     *  URL.
     */
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException(
            "#isRequestedSessionIdFromUrl()"
        );
    }

    @Override
    public boolean authenticate(
        final HttpServletResponse resp
    ) {
        throw new UnsupportedOperationException("#authenticate()");
    }

    @Override
    public void login(
        final String user,
        final String password
    ) {
        throw new UnsupportedOperationException("#login()");
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException("#logout()");
    }

    @Override
    public Collection<Part> getParts() {
        throw new UnsupportedOperationException("#getParts()");
    }

    @Override
    public Part getPart(
        final String name
    ) {
        throw new UnsupportedOperationException("#getPart()");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(
        final Class<T> cls
    ) {
        throw new UnsupportedOperationException("#upgrade()");
    }

    @Override
    public Object getAttribute(final String name) {
        throw new UnsupportedOperationException("#getAttribute()");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("#getAttributeNames()");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("#getCharacterEncoding()");
    }

    @Override
    public void setCharacterEncoding(
        final String encoding
    ) {
        throw new UnsupportedOperationException("#setCharacterEncoding()");
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("#getContentLength()");
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException("#getContentLengthLong()");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("#getContentType()");
    }

    @Override
    public String getParameter(final String key) {
        throw new UnsupportedOperationException("#getParameter()");
    }

    @Override
    public Enumeration<String> getParameterNames() {
        throw new UnsupportedOperationException("#getParameterNames()");
    }

    @Override
    public String[] getParameterValues(final String key) {
        throw new UnsupportedOperationException("#getParameterValues()");
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException("#getParameterMap()");
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("#getProtocol()");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("#getScheme()");
    }

    @Override
    public BufferedReader getReader() {
        throw new UnsupportedOperationException("#getReader()");
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        throw new UnsupportedOperationException("#setAttribute");
    }

    @Override
    public void removeAttribute(final String name) {
        throw new UnsupportedOperationException("#removeAttribute");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("#getLocale()");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException("#getLocales()");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("#isSecure()");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String dispatcher) {
        throw new UnsupportedOperationException("#getRequestDispatcher()");
    }

    /**
     * Gets the real path corresponding to the given virtual path.
     * @param path The path.
     * @deprecated Use ServletContext.getRealPath(java.lang.String) instead.
     * @return The real path, or null if the translation cannot be performed.
     */
    @Deprecated
    public String getRealPath(final String path) {
        throw new UnsupportedOperationException("#getRealPath()");
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("#getRemotePort()");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("#getServletContext()");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("#startAsync()");
    }

    @Override
    public AsyncContext startAsync(
        final ServletRequest req,
        final ServletResponse resp
    ) throws IllegalStateException {
        throw new UnsupportedOperationException("#startAsync(req, resp)");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("#isAsyncStarted()");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("#isAsyncSupported()");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("#getAsyncContext()");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("#getDispatcherType()");
    }
}
