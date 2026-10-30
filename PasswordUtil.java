package com.mishra;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Salted PBKDF2-HMAC-SHA256 password hashing that needs nothing beyond the JDK.
 *
 * <p>Stored format: {@code pbkdf2_sha256$<iterations>$<base64 salt>$<base64 hash>}. Values in any
 * other format (such as legacy plaintext passwords) never verify, so those accounts need a password
 * reset or a one-off migration that hashes the existing rows.
 */
public final class PasswordUtil {
  private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
  private static final String PREFIX = "pbkdf2_sha256";
  private static final int ITERATIONS = 210_000;
  private static final int SALT_BYTES = 16;
  private static final int HASH_BYTES = 32;
  private static final SecureRandom RANDOM = new SecureRandom();

  private PasswordUtil() {}

  /** Returns a salted hash of {@code password} in the stored format. */
  public static String hash(String password) {
    if (password == null) {
      throw new IllegalArgumentException("password must not be null");
    }
    byte[] salt = new byte[SALT_BYTES];
    RANDOM.nextBytes(salt);
    byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, HASH_BYTES);
    Base64.Encoder encoder = Base64.getEncoder();
    return PREFIX
        + "$"
        + ITERATIONS
        + "$"
        + encoder.encodeToString(salt)
        + "$"
        + encoder.encodeToString(hash);
  }

  /** Returns whether {@code password} matches {@code storedHash}, in constant time. */
  public static boolean verify(String password, String storedHash) {
    if (password == null || storedHash == null) {
      return false;
    }
    String[] parts = storedHash.split("\\$");
    if (parts.length != 4 || !PREFIX.equals(parts[0])) {
      return false;
    }
    try {
      int iterations = Integer.parseInt(parts[1]);
      Base64.Decoder decoder = Base64.getDecoder();
      byte[] salt = decoder.decode(parts[2]);
      byte[] expected = decoder.decode(parts[3]);
      if (iterations <= 0 || salt.length == 0 || expected.length == 0) {
        return false;
      }
      byte[] actual = pbkdf2(password.toCharArray(), salt, iterations, expected.length);
      return MessageDigest.isEqual(expected, actual);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int lengthBytes) {
    PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, lengthBytes * 8);
    try {
      return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException(ALGORITHM + " is not available", e);
    } finally {
      spec.clearPassword();
    }
  }
}
