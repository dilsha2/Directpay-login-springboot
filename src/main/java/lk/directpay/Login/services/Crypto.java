package lk.directpay.Login.services;


import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

//convert to pkcs8 using below command
//openssl pkcs8 -topk8 -nocrypt -in key.pem -out keypcks8
@Service
public class Crypto {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Value("${crypto.private-key}")
    private String base64PrivateKey ;
    @Value("${crypto.password}")
    private String aesPassword;
    @Value("${crypto.merchant.passphrase}")
    private String merchantPassphrase;
    private final String aesAlgorithm = "AES/CBC/PKCS5Padding";

    private String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    private PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    private String encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return new String(cipher.doFinal(data.getBytes()));
    }

    private PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    private SecretKey getAesKeyFromPassword(String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(this.aesPassword.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private String decryptAes(String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(this.aesAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    private String encryptAes(String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(this.aesAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    private SecretKeySpec generateAESKey(String passphrase) {
        byte[] passphraseBytes = passphrase.getBytes(StandardCharsets.UTF_8);
        byte[] aesKey = new byte[16];   //128 bits
        System.arraycopy(passphraseBytes, 0, aesKey, 0, passphraseBytes.length);
        return new SecretKeySpec(aesKey, "AES");
    }

    /*
     * Public key encryption
     */
    public String encryptData(String data, String publicKey){
        try {
            return encrypt(data, publicKey);
        } catch (BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Encryption failed. err: " + e.getMessage());
            return null;
        }
    }

    /*
     * Public key decryption
     */
    public String decrypt(String data) {
        data = data.replace("\n", "");
        try {
            return this.decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(this.base64PrivateKey));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "decryption failed. err: " + e.getMessage());
            return null;
        }
    }

    /*
     * AES 256 encryption
     */
    public String encryptAes(String data) {
        try {
            // generate iv
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            String saltString = new String(salt);

            SecretKey key = getAesKeyFromPassword(saltString);
            String encryptedString = encryptAes(data, key, ivSpec);

            String base64Iv = Base64.getEncoder().encodeToString(iv);
            String base64Salt = Base64.getEncoder().encodeToString(salt);

            // base64salt.base64iv.ciper
            return base64Salt + "." + base64Iv + "." + encryptedString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * AES 256 decryption
     */
    public String decryptAes(String data) {
        try {
            // base64salt.base64iv.ciper
            String[] ciperData = data.split("\\.");
            byte[] iv = Base64.getDecoder().decode(ciperData[1]);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            String saltString = new String(Base64.getDecoder().decode(ciperData[0]));
            SecretKey key = this.getAesKeyFromPassword(saltString);

            return this.decryptAes(ciperData[2], key, ivSpec);
        } catch (Exception e) {
            e.printStackTrace();
            return "FAILED_TO_RETRIEVE";
        }
    }

    /*
     * Encrypt merchant's secret code using AES-128-ECB
     */
    public String encryptMerchantSecret(String data) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey key = generateAESKey(merchantPassphrase);

        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedCipherBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedCipherBytes);
    }


    /*
     * Decrypt merchant's secret code using AES-128-ECB
     */
    public String decryptMerchantSecret(String data) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        SecretKeySpec key = generateAESKey(merchantPassphrase);
        //Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

//        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
//        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        final byte[] bytePlainText = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(bytePlainText);
    }

    public String generateRandomBytes() throws Exception{
        byte[] nonce = new byte[32];
        new SecureRandom().nextBytes(nonce);
        StringBuilder result = new StringBuilder();
        for (byte temp : nonce) {
            result.append(String.format("%02x", temp));
        }
        return result.toString();
    }

    public String generateHmac(String data, String merchantSecret) throws NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Mac mac = Mac.getInstance("HmacSHA256");

        String plainMerchantSecret = decryptMerchantSecret(merchantSecret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(plainMerchantSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacSha256 = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hmacSha256);
        //return String.format("%040x", new BigInteger(1, hmacSha256));
    }


}
