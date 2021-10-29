package server_side.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.bouncycastle.util.encoders.Hex;

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
    	byte[] digest = null;
    	try {
    		 MessageDigest messageDigest = MessageDigest.getInstance("SHA3-512");
			 digest = messageDigest.digest(passwordWithSalt.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	
        return Hex.toHexString(digest);
    }

    public static String generateAccessToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
