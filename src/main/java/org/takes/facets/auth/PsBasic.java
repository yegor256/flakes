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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.misc.Optional;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithHeader;

/**
 * Pass that checks the user according RFC-2617.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Endrigo Antonini (teamed@endrigo.com.br)
 * @version $Id$
 * @since 0.20
 */
@EqualsAndHashCode(of = { "entry", "realm" })
public final class PsBasic implements Pass {

    /**
     * Pattern for basic authorization name.
     */
    private static final Pattern AUTH = Pattern.compile("Basic");

    /**
     * Entry to validate user information.
     */
    private final transient PsBasic.Entry entry;

    /**
     * Realm.
     */
    private final transient String realm;

    /**
     * Ctor.
     * @param rlm Realm
     * @param basic Entry
     */
    public PsBasic(final String rlm, final PsBasic.Entry basic) {
        this.realm = rlm;
        this.entry = basic;
    }

    @Override
    public Optional<Identity> enter(final Request request) throws IOException {
        final String decoded = new String(
            DatatypeConverter.parseBase64Binary(
                PsBasic.AUTH.split(
                    new RqHeaders.Smart(
                        new RqHeaders.Base(request)
                    ).single("authorization")
                )[1]
            ), StandardCharsets.UTF_8
        ).trim();
        final String user = decoded.split(":")[0];
        final Optional<Identity> identity = this.entry.enter(
            user,
            decoded.substring(user.length() + 1)
        );
        if (!identity.has()) {
            throw new RsForward(
                new RsWithHeader(
                    new RsFlash("access denied", Level.WARNING),
                    String.format(
                        "WWW-Authenticate: Basic ream=\"%s\"",
                        this.realm
                    )
                ),
                HttpURLConnection.HTTP_UNAUTHORIZED,
                new RqHref.Base(request).href()
            );
        }
        return identity;
    }

    @Override
    public Response exit(final Response response, final Identity identity) {
        return response;
    }

    /**
     * Entry interface that is used to check if the received information is
     * valid.
     *
     * @author Endrigo Antonini (teamed@endrigo.com.br)
     * @version $Id$
     * @since 0.20
     */
    public interface Entry {
        /**
         * Check if is a valid user.
         * @param user User
         * @param pwd Password
         * @return Identity.
         */
        Optional<Identity> enter(String user, String pwd);
    }

    /**
     * Fake implementation of {@link PsBasic.Entry}.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Endrigo Antonini (teamed@endrigo.com.br)
     * @version $Id$
     * @since 0.20
     *
     */
    public static final class Fake implements PsBasic.Entry {
        /**
         * Should we authenticate a user?
         */
        private final transient boolean condition;
        /**
         * Ctor.
         * @param cond Condition
         */
        public Fake(final boolean cond) {
            this.condition = cond;
        }
        @Override
        public Optional<Identity> enter(final String usr, final String pwd) {
            final Optional<Identity> user;
            if (this.condition) {
                user = new Optional<Identity>(
                    new Identity.Simple(
                        String.format("urn:basic:%s", usr)
                    )
                );
            } else {
                user = Optional.empty();
            }
            return user;
        }
    }

    /**
     * Empty check.
     *
     * @author Endrigo Antonini (teamed@endrigo.com.br)
     * @version $Id$
     * @since 0.20
     */
    public static final class Empty implements PsBasic.Entry {
        @Override
        public Optional<Identity> enter(final String user, final String pwd) {
            return Optional.empty();
        }
    }

    /**
     * Default entry.
     *
     * @author Georgy Vlasov (wlasowegor@gmail.com)
     * @version $Id$
     * @since 0.22
     */
    public static final class Default implements PsBasic.Entry {
        /**
         * How keys in
         * {@link org.takes.facets.auth.PsBasic.Default#usernames} are
         * formatted.
         */
        private static final String KEY_FORMAT = "%s %s";
        /**
         * Encoding for URLEncode#encode.
         */
        private static final String ENCODING = "UTF-8";
        /**
         * Map from login/password pairs to URNs.
         */
        private final transient Map<String, String> usernames;
        /**
         * Public ctor.
         * @param users Strings with user's login, password and URN with
         *  space characters as separators. Each of login, password and urn
         *  are URL-encoded substrings. For example,
         *  {@code "mike my%20password urn:jcabi-users:michael"}.
         * @todo #558:30min Default ctor. According to new qulice version,
         *  constructor must contain only variables initialization and
         *  other constructor calls. Refactor code according to that rule
         *  and remove `ConstructorOnlyInitializesOrCallOtherConstructors`
         *  warning suppression.
         */
        @SuppressWarnings
            (
                "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
            )
        public Default(final String... users) {
            this.usernames = new HashMap<>(users.length);
            for (final String user : users) {
                final String unified = user.replace("%20", "+");
                PsBasic.Default.validateUser(unified);
                this.usernames.put(
                    PsBasic.Default.key(unified),
                    unified.substring(unified.lastIndexOf(' ') + 1)
                );
            }
        }
        @Override
        public Optional<Identity> enter(final String user, final String pwd) {
            final Optional<String> urn = this.urn(user, pwd);
            final Optional<Identity> identity;
            if (urn.has()) {
                try {
                    identity = new Optional<Identity>(
                        new Identity.Simple(
                            URLDecoder.decode(
                                urn.get(), PsBasic.Default.ENCODING
                            )
                        )
                    );
                } catch (final UnsupportedEncodingException ex) {
                    throw new IllegalStateException(ex);
                }
            } else {
                identity = Optional.empty();
            }
            return identity;
        }
        /**
         * Returns an URN corresponding to a login-password pair.
         * @param user Login.
         * @param pwd Password.
         * @return Optional with URN or empty if there is no such login-password
         *  pair.
         */
        private Optional<String> urn(final String user, final String pwd) {
            final String urn;
            try {
                urn = this.usernames.get(
                    String.format(
                        PsBasic.Default.KEY_FORMAT,
                        URLEncoder.encode(
                            user,
                            PsBasic.Default.ENCODING
                        ),
                        URLEncoder.encode(
                            pwd,
                            PsBasic.Default.ENCODING
                        )
                    )
                );
            } catch (final UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
            return new Optional<>(urn);
        }
        /**
         * Creates a key for
         *  {@link org.takes.facets.auth.PsBasic.Default#usernames} map.
         * @param unified User string made of 3 urlencoded substrings
         *  separated with non-urlencoded space characters.
         * @return Login and password parts with <pre>%20</pre> replaced with
         *  <pre>+</pre>.
         */
        private static String key(final String unified) {
            return String.format(
                PsBasic.Default.KEY_FORMAT,
                unified.substring(0, unified.indexOf(' ')),
                unified.substring(
                    unified.indexOf(' ') + 1,
                    unified.lastIndexOf(' ')
                )
            );
        }
        /**
         * Checks if a unified user string is correctly formatted.
         * @param unified String with urlencoded user login, password and urn
         *  separated with spaces.
         */
        private static void validateUser(final String unified) {
            final boolean amount = PsBasic.Default.countSpaces(unified) != 2;
            final boolean nearby =
                unified.indexOf(' ') + 1 == unified.lastIndexOf(' ');
            if (amount || nearby) {
                throw new IllegalArgumentException(
                    String.format(
                        "One of users was incorrectly formatted: %s",
                        unified
                    )
                );
            }
        }
        /**
         * Counts spaces in a string.
         * @param txt Any string.
         * @return Amount of spaces in string.
         */
        private static int countSpaces(final String txt) {
            int spaces = 0;
            for (final char character : txt.toCharArray()) {
                if (character == ' ') {
                    spaces += 1;
                }
            }
            return spaces;
        }
    }
}
