package server_side.crypto;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class Crypto {

    // Just for testing purposes
    public static void main(String[] args) {
        String test = createPasswordHash("salt", "hashedPassword");
        System.out.println(test);
    }

    public static String createPasswordHash(String salt, String password) {
        final String passwordWithSalt = password + salt;
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(passwordWithSalt.getBytes());
        return Hex.toHexString(digest);
    }

    public static byte[] getNextSalt() {
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[50];
        rand.nextBytes(salt);
        return salt;
    }
}
