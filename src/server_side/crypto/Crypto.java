package server_side.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    	byte[] digest = null;
    	try {
    		 MessageDigest messageDigest = MessageDigest.getInstance("SHA3-512");
			 digest = messageDigest.digest(passwordWithSalt.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	
    	
        return encodeHexString(digest); 
    }
    
    private static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }
    
    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
    
    public static String generateAccessToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static String getNextSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);

    }
}
