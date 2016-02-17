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
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;

/**
 * AES codec which supports 128 bits key.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
 * @since 0.13.8
 */
@EqualsAndHashCode(of = { "origin", "secret", "enc", "dec" })
public final class CcAES implements Codec {

    /**
     * The cipher for encryption.
     */
    private final transient Cipher enc;

    /**
     * The cipher for decryption.
     */
    private final transient Cipher dec;

    /**
     * Original codec.
     */
    private final transient Codec origin;

    /**
     * The AES secret key object.
     */
    private final transient SecretKey secret;

    /**
     * Ctor.
     * @param codec Original codec
     * @param key The encryption key
     * @exception IOException errors on creating internal components
     * @since 0.22
     */
    public CcAES(final Codec codec, final String key) throws IOException {
        this(codec, key.getBytes(Charset.defaultCharset()));
    }

    /**
     * Ctor.
     * @param codec Original codec
     * @param key The encryption key
     * @exception IOException errors on creating internal components
     * @todo #558:30min CcAES ctor. According to new qulice version, constructor
     *  must contain only variables initialization and other constructor calls.
     *  Refactor code according to that rule and remove
     *  `ConstructorOnlyInitializesOrCallOtherConstructors`
     *  warning suppression.
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    public CcAES(final Codec codec, final byte[] key) throws IOException {
        final int block = 16;
        if (key.length != block) {
            throw new IllegalArgumentException(
                String.format(
                    "the length of the AES key must be exactly %d bytes",
                    block
                )
            );
        }
        this.origin = codec;
        final byte[] passcode = new byte[key.length];
        System.arraycopy(key, 0, passcode, 0, key.length);
        final SecureRandom random = new SecureRandom();
        final byte[] ivbytes = new byte[block];
        random.nextBytes(ivbytes);
        final AlgorithmParameterSpec spec = new IvParameterSpec(ivbytes);
        this.secret = new SecretKeySpec(passcode, "AES");
        this.enc = this.create(Cipher.ENCRYPT_MODE, spec);
        this.dec = this.create(Cipher.DECRYPT_MODE, spec);
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        return this.encrypt(this.origin.encode(identity));
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        return this.origin.decode(this.decrypt(bytes));
    }

    /**
     * Encrypt the given bytes using AES.
     *
     * @param bytes Bytes to encrypt
     * @return Encrypted byte using AES algorithm
     * @throws IOException for all unexpected exceptions
     */
    private byte[] encrypt(final byte[] bytes) throws IOException {
        try {
            return this.enc.doFinal(bytes);
        } catch (final BadPaddingException ex) {
            throw new IOException(ex);
        } catch (final IllegalBlockSizeException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Decrypt the given bytes using AES.
     *
     * @param bytes Bytes to decrypt
     * @return Decrypted bytes
     * @throws IOException for all unexpected exceptions
     */
    private byte[] decrypt(final byte[] bytes) throws IOException {
        try {
            return this.dec.doFinal(bytes);
        } catch (final BadPaddingException ex) {
            throw new IOException(ex);
        } catch (final IllegalBlockSizeException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Create new cipher based on the valid mode from {@link Cipher} class.
     * @param mode Either Cipher.ENRYPT_MODE or Cipher.DECRYPT_MODE
     * @param spec The algorithm parameter spec for cipher creation
     * @return The cipher
     * @throws IOException For any unexpected exceptions
     */
    private Cipher create(final int mode, final AlgorithmParameterSpec spec)
        throws IOException {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(mode, this.secret, spec);
            return cipher;
        } catch (final InvalidKeyException ex) {
            throw new IOException(ex);
        } catch (final NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        } catch (final NoSuchPaddingException ex) {
            throw new IOException(ex);
        } catch (final InvalidAlgorithmParameterException ex) {
            throw new IOException(ex);
        }
    }

}
