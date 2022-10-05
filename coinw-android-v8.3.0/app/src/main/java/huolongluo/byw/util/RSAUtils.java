/**
 * hpx.com Inc.
 * Copyright (c) 2012-YEARAll Rights Reserved.
 */
package huolongluo.byw.util;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**  
 * @author qlj  
 * @version $Id: RSAUtils.java, v 0.1 2018-05-14 13:38 legend Exp $$  
 */
public class RSAUtils {
    /**
     * 加密算法RSA
     */
//    public static final String KEY_ALGORITHM = "RSA";
//    public static final String KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String KEY_ALGORITHM = "RSA/ECB/NoPadding";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     *
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encode(signature.sign());
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     *
     * @return
     * @throws Exception
     *
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }

    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 对数据分段解密
        catDataDo(encryptedData, cipher, inputLen, out);
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 对数据进行分段处理
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

    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 对数据分段解密
        catDataDo(encryptedData, cipher, inputLen, out);
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 对数据分段加密
        catDataDo(data, cipher, inputLen, out);
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 对数据分段加密
        catDataDo(data, cipher, inputLen, out);
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encode(key.getEncoded());
    }

    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encode(key.getEncoded());
    }


    public static String entryBuPublicKeyString(String source,String publicKey) throws Exception{
        byte[] data = source.getBytes();
        byte[] encodedData = encryptByPublicKey(data, publicKey);
        String encoded = Base64.encode(encodedData);
        return encoded;
    }

    public static void main(String[] args){

        try {
//            Map<String, Object> keyMap = RSAUtils.genKeyPair();
//            String  publicKey = RSAUtils.getPublicKey(keyMap);
//            String privateKey = RSAUtils.getPrivateKey(keyMap);
//            System.err.println("公钥: \n\r" + publicKey);
//            System.err.println("私钥： \n\r" + privateKey);
//            String publicKeys = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLDGR6TVfa+zqegLZXQIDAQAB";
//            String privatekeys = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ6vEza9chHIZG8zM6dsjO9etGF+8fI7QkDlJnChcklYt2SXF5VuPJ7WXyCY9MjYd3fdTSsV9A26GWG3vW/0zEvk+T/SIUAivLdk9o6PfcqAAJZYJIfYcYB6224ZVuU0rkIQfWmknCwL3yxUFYNQEmkB+WgsMZHpNV9r7Op6AtldAgMBAAECgYAM7f8GytDs mgN0/BsNXU7ugiz4dLuKHUH8v68uJjmFuHDZBWaqilzuJFGD+nKXmuvBHIsZI6TLKYq/55TL9ope0+GeuOx1b0i29sWyhGlFmDSE1gHsSWEqG+3hJjGwD/LMxOu5zmKkYxU4QmrXUnHG2Tm4fYcpmMoHZWtrDB10kQJBAM9Kq/a53/urt9FKgcGVij7G2GRCOmMeex4Mm4PoJ7EoE0XyQBrcPjKglGhScjMNkIFKSK/eBxqaNtHOwsPsQu8CQQDD+HjjfzuJ6WNRGXu7giNrgNXNNTmMdS3FgnNGe6v8MihI49EJDpwTdm1eGCACT8MSxzPZJ6vKyfBxVGahi7hzAkEAlZ8IUKYJjdZ8b4yf6+LMTlOojXVgP3sY0q+28Jb5T13ly2735mtiWZiehk48L61yyF+d55MS/ZiiSP48hr5Z8QJAc67WtfziiavTLAmuB+dvv0NDcqrLuQVnKqOyJtZUlUCKlVSejgkoyzhqoVP+eT7aedhL3BHWEJSaslvPZeeTzwJABTFIEiR/5nUIfR86tzY0RdUiYEVhGGPCh5e91R8yfZLmHmAubGJvCdyfXvSmSB4VNXThKjuBFB5UAQ6gyVV+7A==";
            Map<String, Object> stringObjectMap = genKeyPair();
//            String source = "5,1800";
            String publicKeys = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLDGR6TVfa+zqegLZXQIDAQAB";
            String source= "xxxxxxxxx";
//            byte[] data = source.getBytes();
//            byte[] encodedData = encryptByPublicKey(data, publicKeys);

//            System.out.println("加密后文字：\r\n" + new String(encodedData));

//            String encoded = Base64.encode(encodedData);


            System.out.println("base64后文字：\r\n" + RSAUtils.entryBuPublicKeyString(source,publicKeys));

//            byte[] decoded = Base64.decode(encoded);
//            byte[] decodedData = decryptByPrivateKey(decoded, privatekeys);
//            String target = new String(decodedData);
//            System.out.println("解密后文字: \r\n" + target);

//            RSACipher.decryptByPublicKey("msg");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}