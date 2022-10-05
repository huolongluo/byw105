package huolongluo.byw.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AES {
	
	public static void main(String[] args) {

		String plaintext = "hello world. 你好, 世界!";
        String key="new_coinws_coinw"; // 这是秘钥
		try {
            String ciphertext = AES.encrypt(plaintext, key);
            System.out.println(ciphertext);
            String decrypt = AES.decrypt("Mz+8l21YhDlIMbwZFKfk6/lB9ZLh2UFAkgqGYTMnNFk=", key);
            System.out.println(decrypt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /**
     * AES加密字符串
     * @param content 需要被加密的字符串
     * @param password 加密需要的密码
     * @return 密文
     */
    public static String encrypt(String content, String password) throws Exception {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者

            kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
            // 128位的key生产者
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥

            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回

            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥

            Cipher cipher = Cipher.getInstance("AES");// 创建密码器

            byte[] byteContent = content.getBytes();

            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent);// 加密

            return Base64.encode(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密AES加密过的字符串
     * @param content  AES加密过过的内容
     * @param password 加密时的密码
     * @return 明文
     */
    public static String decrypt(String content, String password) throws Exception {
        byte[] byteContent= Base64.decode(content);
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key); // 初始化为解密模式的密码器
            byte[] result = cipher.doFinal(byteContent);
            return new String(result);  // 明文

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}