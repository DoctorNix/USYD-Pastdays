package Edstemus.database.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {

    // generating a random salt
    public static byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // makes byte array to a base64 encoded string
    public static String byteToString(byte[] input){
        return Base64.getEncoder().encodeToString(input);
    }

    // base64 encoded string back to byte array
    public static byte[] stringToByte(String input) {
        return Base64.getDecoder().decode(input);
    }

    // hash password with salt
    public static byte[] getHashWithSalt(String password, byte[] salt){
        try{
            MessageDigest digest  = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(salt);
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // hash the hashed password and salt to generate a single hash
    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        byte[] hash = getHashWithSalt(password, salt);

        // combine salt and hash for storage
        String saltString = byteToString(salt);
        String hashString = byteToString(hash);

        // combined salt and hash, separated by a colon
        return saltString + ":" + hashString;
    }


    public static boolean verifyPassword(String enteredPassword, String storedPasswordHash) {
        String[] parts = storedPasswordHash.split(":");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Stored password must be in 'salt:hash' format");
        }

        byte[] salt = stringToByte(parts[0]);
        byte[] storedHash = stringToByte(parts[1]);

        byte[] enteredHash = getHashWithSalt(enteredPassword, salt);

        return MessageDigest.isEqual(storedHash, enteredHash);
    }
}
