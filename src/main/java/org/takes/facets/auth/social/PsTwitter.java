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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonObject;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Href;
import org.takes.misc.Opt;

/**
 * Twitter OAuth landing/callback page.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Prasath Premkumar (popprem@gmail.com)
 * @version $Id$
 * @since 0.16
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@EqualsAndHashCode(of = { "app", "key" })
public final class PsTwitter implements Pass {

    /**
     * URL for verifying user credentials.
     */
    private static final String VERIFY_URL =
        "https://api.twitter.com/1.1/account/verify_credentials.json";

    /**
     * App name.
     */
    private final transient String app;

    /**
     * Key.
     */
    private final transient String key;

    /**
     * Request for fetching app token.
     */
    private final transient com.jcabi.http.Request token;

    /**
     * Request for verifying user credentials.
     */
    private final transient com.jcabi.http.Request user;

    /**
     * Ctor.
     * @param name Twitter app
     * @param keys Twitter key
     */
    public PsTwitter(final String name, final String keys) {
        this(
            new JdkRequest(
                new Href("https://api.twitter.com/oauth2/token")
                    .with("grant_type", "client_credentials")
                    .toString()
            ),
            new JdkRequest(PsTwitter.VERIFY_URL), name, keys
        );
    }

    /**
     * Ctor with proper requestor for testing purposes.
     * @param tkn HTTP request for getting token
     * @param creds HTTP request for verifying credentials
     * @param name Facebook app
     * @param keys Facebook key
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    PsTwitter(final com.jcabi.http.Request tkn,
        final com.jcabi.http.Request creds,
        final String name,
        final String keys) {
        this.token = tkn;
        this.user = creds;
        this.app = name;
        this.key = keys;
    }

    @Override
    public Opt<Identity> enter(final Request request)
        throws IOException {
        return new Opt.Single<Identity>(this.identity(this.fetch()));
    }

    @Override
    public Response exit(final Response response, final Identity identity) {
        return response;
    }

    /**
     * Get user name from Twitter, with the token provided.
     * @param tkn Twitter access token
     * @return The user found in Twitter
     * @throws IOException If fails
     */
    private Identity identity(final String tkn) throws IOException {
        return parse(
            this.user
                .uri()
                .set(
                    URI.create(
                        new Href(PsTwitter.VERIFY_URL)
                            .with("access_token", tkn)
                            .toString()
                    )
                )
                .back()
                .header("accept", "application/json")
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json()
                .readObject()
        );
    }

    /**
     * Make identity from JSON object.
     * @param json JSON received from Twitter
     * @return Identity found
    */
    private static Identity parse(final JsonObject json) {
        final Map<String, String> props =
            new HashMap<String, String>(json.size());
        props.put("name", json.getString("name"));
        props.put("picture", json.getString("profile_image_url"));
        return new Identity.Simple(
            String.format("urn:twitter:%d", json.getInt("id")),
            props
        );
    }

    /**
     * Retrieve Twitter access token.
     * @return The Twitter access token
     * @throws IOException If failed
     */
    private String fetch() throws IOException {
        return this.token
            .method("POST")
            .header(
                "Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8"
            )
            .header(
                "Authorization",
                String.format(
                    "Basic %s", DatatypeConverter.printBase64Binary(
                        String.format("%s:%s", this.app, this.key)
                            .getBytes(StandardCharsets.UTF_8)
                    )
                )
            )
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject().getString("access_token");
    }
}
