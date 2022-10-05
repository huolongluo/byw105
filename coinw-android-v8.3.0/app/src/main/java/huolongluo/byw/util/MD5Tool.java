package huolongluo.byw.util;

import android.text.TextUtils;
import android.util.Base64;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by dell on 2019/6/14.
 */
public class MD5Tool {

    private final byte[] DESIV = new byte[]{0x22, 0x54, 0x36, 110, 0x40, (byte) 0xac, (byte) 0xad, (byte) 0xdf};// 向量
    private AlgorithmParameterSpec iv = null;// 加密算法的参数接口
    private Key key = null;
    private String charset = "utf-8";
    String deSkey = "iQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvH";

    /**
     * 构造函数
     *
     * @param deSkey
     * @param charset
     * @throws Exception
     */
    private MD5Tool(String deSkey, String charset) throws Exception {
        this.charset = charset;
        DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes(this.charset));// 设置密钥参数
        iv = new IvParameterSpec(DESIV);// 设置向量
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
        key = keyFactory.generateSecret(keySpec);// 得到密钥对象
    }

    private static MD5Tool instance;

    public static MD5Tool getDefault() {
        try {
            if (instance == null) {
                instance = new MD5Tool("utf-8");
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MD5Tool(String charset) throws Exception {
        this.charset = charset;
        DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes(this.charset));// 设置密钥参数
        iv = new IvParameterSpec(DESIV);// 设置向量
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
        key = keyFactory.generateSecret(keySpec);// 得到密钥对象
    }

    /**
     * 加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String encode(String data) {
        Cipher enCipher = null;// 得到加密对象Cipher
        try {
            enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
            byte[] pasByte = enCipher.doFinal(data.getBytes(this.charset));
            // Base64 base64Encoder = new Base64();
            return Base64.encodeToString(pasByte, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String decode(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        Cipher deCipher = null;
        try {
            deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            deCipher.init(Cipher.DECRYPT_MODE, key, iv);
            // Base64 base64Decoder = new Base64();
            //此处注意doFinal()的参数的位数必须是8的倍数，否则会报错（通过encode加密的字符串读出来都是8的倍数位，但写入文件再读出来，就可能因为读取的方式的问题，导致最后此处的doFinal()的参数的位数不是8的倍数）
            //此处必须用base64Decoder，若用data。getBytes()则获取的字符串的byte数组的个数极可能不是8的倍数，而且不与上面的BASE64Encoder对应（即使解密不报错也不会得到正确结果）
            byte[] pasByte = deCipher.doFinal(Base64.decode(data.getBytes(), Base64.DEFAULT));
            return new String(pasByte, this.charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取MD5的值，可用于对比校验
     *
     * @param sourceStr
     * @return
     */
    private static String getMD5Value(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return result;
    }
}
