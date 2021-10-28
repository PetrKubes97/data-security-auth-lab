package server_side.crypto;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;
import java.util.Base64;

public class Crypto {

    // Just for testing purposes
    public static void main(String[] args) {
        String test = createPasswordHash("salt", "hashedPassword");
        System.out.println(test);
    }


    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String createPasswordHash(String salt, String password) {
        final String passwordWithSalt = password + salt;
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(passwordWithSalt.getBytes());
        return Hex.toHexString(digest);
    }

    public static String generateAccessToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
