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
package org.takes.facets.auth.social;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.Version;
import com.restfb.WebRequestor;
import com.restfb.exception.FacebookException;
import com.restfb.types.User;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Href;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;

/**
 * Facebook OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.5
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsFacebook implements Pass {

    /**
     * Request for fetching app token.
     */
    private final transient com.jcabi.http.Request request;

    /**
     * Facebook login request handler.
     */
    private final transient WebRequestor requestor;

    /**
     * App name.
     */
    private final transient String app;

    /**
     * Key.
     */
    private final transient String key;

    /**
     * Ctor.
     * @param fapp Facebook app
     * @param fkey Facebook key
     */
    public PsFacebook(final String fapp, final String fkey) {
        this(
            new JdkRequest(
                new Href("https://graph.facebook.com/oauth/access_token")
                    .with("client_id", fapp)
                    .with("client_secret", fkey)
                    .toString()
            ),
            new DefaultWebRequestor(),
            fapp,
            fkey
        );
    }

    /**
     * Ctor with proper requestor for testing purposes.
     * @param frequest HTTP request for getting key
     * @param frequestor Facebook response
     * @param fapp Facebook app
     * @param fkey Facebook key
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    PsFacebook(final com.jcabi.http.Request frequest,
        final WebRequestor frequestor, final String fapp, final String fkey) {
        this.request = frequest;
        this.requestor = frequestor;
        this.app = fapp;
        this.key = fkey;
    }

    @Override
    public Opt<Identity> enter(final Request trequest)
        throws IOException {
        final Href href = new RqHref.Base(trequest).href();
        final Iterator<String> code = href.param("code").iterator();
        if (!code.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "code is not provided by Facebook"
            );
        }
        final User user = this.fetch(
            this.token(href.toString(), code.next())
        );
        final Map<String, String> props =
            new HashMap<String, String>(0);
        props.put("name", user.getName());
        props.put(
            "picture",
            new Href("https://graph.facebook.com/")
                .path(user.getId())
                .path("picture")
                .toString()
        );
        return new Opt.Single<Identity>(
            new Identity.Simple(
                String.format("urn:facebook:%s", user.getId()),
                props
            )
        );
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Get user name from Facebook, but the code provided.
     * @param token Facebook access token
     * @return The user found in FB
     */
    private User fetch(final String token) {
        try {
            return new DefaultFacebookClient(
                token,
                this.requestor,
                new DefaultJsonMapper(),
                Version.LATEST
            ).fetchObject(
                "me", User.class
            );
        } catch (final FacebookException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Retrieve Facebook access token.
     * @param home Home of this page
     * @param code Facebook "authorization code"
     * @return The token
     * @throws IOException If failed
     */
    private String token(final String home, final String code)
        throws IOException {
        final String response = this.request
            .uri()
            .set(
                URI.create(
                    new Href("https://graph.facebook.com/oauth/access_token")
                        .with("client_id", this.app)
                        .with("redirect_uri", home)
                        .with("client_secret", this.key)
                        .with("code", code)
                        .toString()
                )
            )
            .back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK).body();
        final String[] sectors = response.split("&");
        for (final String sector : sectors) {
            final String[] pair = sector.split("=");
            if (pair.length != 2) {
                throw new IllegalArgumentException(
                    String.format("Invalid response: '%s'", response)
                );
            }
            if ("access_token".equals(pair[0])) {
                return pair[1];
            }
        }
        throw new IllegalArgumentException(
            String.format(
                "Access token not found in response: '%s'",
                response
            )
        );
    }

}
