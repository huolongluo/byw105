package huolongluo.byw.util;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import huolongluo.bywx.utils.EncryptUtils;

//import cn.hutool.crypto.SecureUtil;
//import cn.hutool.crypto.asymmetric.KeyType;
//import cn.hutool.crypto.asymmetric.RSA;

public class RSACipher {

    private static com.android.coinw.security.EncryptUtils encryptUtils = new com.android.coinw.security.EncryptUtils();
    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(String encryptedData)
            throws Exception {
        String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLDGR6TVfa+zqegLZXQIDAQAB";
        if (publicKeyString.contains("-----BEGIN PUBLIC KEY-----") || publicKeyString.contains("-----END PUBLIC KEY-----"))
            publicKeyString = publicKeyString.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        publicKeyString = publicKeyString.trim();
        byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        //  int inputLen = encryptedData.length;
        //  ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 对数据分段解密
        // catDataDo(encryptedData, cipher, inputLen, out);
        //  byte[] decryptedData = out.toByteArray();
        //  out.close();
        byte[] decryptedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
        //  decrypted = new String(decryptedBytes);
        return decryptedBytes;
    }

    static int MAX_DECRYPT_BLOCK = 100;

    /**
     * 对数据进行分段处理
     *
     * @param encryptedData
     * @param cipher
     * @param inputLen
     * @param out
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static void catDataDo(byte[] encryptedData, Cipher cipher, int inputLen, ByteArrayOutputStream out) throws IllegalBlockSizeException, BadPaddingException {
        int i = 0;
        int offSet = 0;
        byte[] cache;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
    }

    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte[] encryptedBytes, decryptedBytes;
    Cipher cipher, cipher1;
    String encrypted, decrypted;
    private final static String CRYPTO_METHOD = "RSA";
    private final static int CRYPTO_BITS = 1024;
//    public RSACipher()
//            throws NoSuchAlgorithmException,
//            NoSuchPaddingException,
//            InvalidKeyException,
//            IllegalBlockSizeException,
//            BadPaddingException {
//
//        generateKeyPair();
//    }

    private void generateKeyPair()
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        kpg = KeyPairGenerator.getInstance(CRYPTO_METHOD);
        kpg.initialize(CRYPTO_BITS);
        kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    /**
     * Encrypt plain text to RSA encrypted and Base64 encoded string
     *
     * @param // args[0] should be plain text that will be encrypted
     * If args[1] is be, it should be RSA public key to be used as encrypt public key
     * @return a encrypted string that Base64 encoded
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String encrypt(String context, String publicKey)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
//        PublicKey rsaPublicKey = stringToPublicKey(publicKey);
//        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
//        encryptedBytes = cipher.doFinal(context.getBytes(StandardCharsets.UTF_8));
//        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
//        return EncryptUtils.encryptByPublicKey(context,publicKey);
        return encryptUtils.encodeByRSAPubKey(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String encrypt(String context)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLDGR6TVfa+zqegLZXQIDAQAB";
//        String enCrypt = context;
//        PublicKey rsaPublicKey = stringToPublicKey(publicKey);
//        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
//        encryptedBytes = cipher.doFinal(context.getBytes(StandardCharsets.UTF_8));
//        enCrypt = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
//        return enCrypt;
        return encryptUtils.encodeByRSAPubKey(context);
    }

    public String decrypt(String result)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLDGR6TVfa+zqegLZXQIDAQAB";
        PublicKey rsaPublicKey = stringToPublicKey(publicKey);
        cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher1.init(Cipher.DECRYPT_MODE, rsaPublicKey);
        decryptedBytes = cipher1.doFinal(Base64.decode(result, Base64.DEFAULT));
        decrypted = new String(decryptedBytes);
        return decrypted;
    }
    //分段加密

    public String decryptfd(String result)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException, IOException {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLDGR6TVfa+zqegLZXQIDAQAB";
        PublicKey rsaPublicKey = stringToPublicKey(publicKey);
        cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher1.init(Cipher.DECRYPT_MODE, rsaPublicKey);
        //decryptedBytes = cipher1.doFinal(Base64.decode(result, Base64.DEFAULT));
        //  decrypted = new String(decryptedBytes);
        // byte[] encryptedData =result.getBytes();
//        byte[] encryptedData = Base64.decode(result, Base64.DEFAULT);
//        RSA rs = SecureUtil.rsa(null, publicKey);
//        byte[] decryptedData = rs.decrypt(encryptedData, KeyType.PublicKey);
//        decrypted = new String(decryptedData);
//        return decrypted;
        return EncryptUtils.decryptByPublicKey(result);

    /*
        int offSet = 0;
        byte[] cache;
        int i = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = encryptedData.length;

     *//*   if(inputLen<=128){
           decryptedBytes = cipher1.doFinal(Base64.decode(result, Base64.DEFAULT));
            decrypted = new String(decryptedBytes);
            return decrypted;
        }
*//*
        //javax.crypto.BadPaddingException: error:04000069:RSA routines:OPENSSL_internal:BLOCK_TYPE_IS_NOT_01
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet >MAX_DECRYPT_BLOCK) {
                cache = cipher1.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher1.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        decrypted = new String(decryptedData);
        return decrypted;*/
    }

    public String getPublicKey(String option)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        switch (option) {
            case "pkcs1-pem":
                String pkcs1pem = "-----BEGIN RSA PUBLIC KEY-----\n";
                pkcs1pem += Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
                pkcs1pem += "-----END RSA PUBLIC KEY-----";
                return pkcs1pem;
            case "pkcs8-pem":
                String pkcs8pem = "-----BEGIN PUBLIC KEY-----\n";
                pkcs8pem += Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
                pkcs8pem += "-----END PUBLIC KEY-----";
                return pkcs8pem;
            case "base64":
                return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
            default:
                return null;
        }
    }

    public static PublicKey stringToPublicKey(String publicKeyString)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        try {
            if (publicKeyString.contains("-----BEGIN PUBLIC KEY-----") || publicKeyString.contains("-----END PUBLIC KEY-----"))
                publicKeyString = publicKeyString.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            publicKeyString = publicKeyString.trim();
            byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void main(String[] args) throws Exception {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLDGR6TVfa+zqegLZXQIDAQAB";
        String context = "moon123456";
        RSACipher rsaCipher = new RSACipher();
        String enStr = rsaCipher.encrypt(context, publicKey);
        //    System.out.println("加密后文字: \r\n" + enStr);
    }
}
