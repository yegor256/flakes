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
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.json.JsonObject;
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
 * Google OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@EqualsAndHashCode(of = { "app", "key", "redir" })
public final class PsGoogle implements Pass {

    /**
     * App name.
     */
    private final transient String app;

    /**
     * Key.
     */
    private final transient String key;

    /**
     * Redirect URI.
     */
    private final transient String redir;

    /**
     * Google OAuth url.
     */
    private final transient String gauth;

    /**
     * Google API url.
     */
    private final transient String gapi;

    /**
     * Ctor.
     * @param gapp Google app
     * @param gkey Google key
     * @param uri Redirect URI (exactly as registered in Google console)
     */
    public PsGoogle(final String gapp, final String gkey,
        final String uri) {
        this(
            gapp,
            gkey,
            uri,
            "https://accounts.google.com",
            "https://www.googleapis.com"
        );
    }

    /**
     * Ctor.
     * @param gapp Google app
     * @param gkey Google key
     * @param uri Redirect URI (exactly as registered in Google console)
     * @param gurl Google OAuth url
     * @param aurl Google API url
     * @checkstyle ParameterNumberCheck (2 lines)
     */
    PsGoogle(final String gapp, final String gkey,
        final String uri, final String gurl, final String aurl) {
        this.app = gapp;
        this.key = gkey;
        this.redir = uri;
        this.gauth = gurl;
        this.gapi = aurl;
    }

    @Override
    public Opt<Identity> enter(final Request request)
        throws IOException {
        final Href href = new RqHref.Base(request).href();
        final Iterator<String> code = href.param("code").iterator();
        if (!code.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "code is not provided by Google, probably some mistake"
            );
        }
        return new Opt.Single<Identity>(this.fetch(this.token(code.next())));
    }

    @Override
    public Response exit(final Response response,
        final Identity identity) {
        return response;
    }

    /**
     * Get user name from Google, with the token provided.
     * @param token Google access token
     * @return The user found in Google
     * @throws IOException If fails
     */
    private Identity fetch(final String token) throws IOException {
        // @checkstyle LineLength (1 line)
        final String uri = new Href(this.gapi).path("oauth2").path("v1").path("userinfo")
            .with("alt", "json")
            .with("access_token", token)
            .toString();
        return PsGoogle.parse(
            new JdkRequest(uri).fetch()
                .as(JsonResponse.class).json()
                .readObject()
        );
    }

    /**
     * Retrieve Google access token.
     * @param code Google "authorization code"
     * @return The token
     * @throws IOException If failed
     */
    private String token(final String code) throws IOException {
        return new JdkRequest(
            new Href(this.gauth).path("o").path("oauth2").path("token")
                .toString()
        ).body()
            .formParam("client_id", this.app)
            .formParam("redirect_uri", this.redir)
            .formParam("client_secret", this.key)
            .formParam("grant_type", "authorization_code")
            .formParam("code", code)
            .back()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .method(com.jcabi.http.Request.POST)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class).json()
            .readObject()
            .getString("access_token");
    }

    /**
     * Make identity from JSON object.
     * @param json JSON received from Google
     * @return Identity found
     */
    private static Identity parse(final JsonObject json) {
        final ConcurrentMap<String, String> props =
            new ConcurrentHashMap<String, String>(json.size());
        // @checkstyle MultipleStringLiteralsCheck (1 line)
        props.put("picture", json.getString("picture", "#"));
        props.put("name", json.getString("name", "unknown"));
        return new Identity.Simple(
            String.format("urn:google:%s", json.getString("id")), props
        );
    }

}
