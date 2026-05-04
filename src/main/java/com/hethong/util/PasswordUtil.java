package com.hethong.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int WORK_FACTOR = 12;

    private PasswordUtil() {}

    /**
     * Hashes a plaintext password using BCrypt.
     *
     * @param plainPassword the raw password entered by the user
     * @return the BCrypt hash string to store in the database
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verifies a plaintext password against a stored BCrypt hash.
     *
     * @param plainPassword  the raw password entered by the user
     * @param hashedPassword the BCrypt hash stored in the database
     * @return true if the password matches, false otherwise
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
