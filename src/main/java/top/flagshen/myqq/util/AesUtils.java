package top.flagshen.myqq.util;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * @ClassName AesUtils
 * @Description AES加解密工具类
 * @Author Pan.R.H
 * @Date 2021/4/29 12:12
 */
public class AesUtils {
    private static final String AES = "AES";
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String key = "5638c145682c4d6fbf813c95acfa4feb";
    private static final String iv = "24185abc465d4f2b";
    /**
     * 加密
     *
     * @param data 待加密明文数据
     * @param key  加密密码
     * @param iv   加密向量（偏移量）
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
        return aes(data, key, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param encryptedData 加密后的数据
     * @param key           解密密码
     * @param iv            解密向量（偏移量）
     * @return
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) {
        return aes(encryptedData, key, iv, Cipher.DECRYPT_MODE);
    }

    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     *
     * @param input 原始字节数组
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     * @param mode  Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(AES_CBC);
            cipher.init(mode, secretKey, ivSpec);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decodeHex(final char[] data) throws Exception {

        final int len = data.length;

        if ((len & 0x01) != 0) {
            throw new Exception("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    /**
     * Hex解码.
     */
    public static byte[] decodeHex(String input) {
        try {
            return decodeHex(input.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int toDigit(final char ch, final int index) throws Exception {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }



    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    public static char[] encodeHex(final byte[] data){
        return encodeHex(data, DIGITS_LOWER);
    }

    /**
     * 生成AES密钥,可选长度为128,192,256位.
     */
    public static byte[] generateAesKey(int keysize)throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(keysize);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (GeneralSecurityException e) {
            throw new Exception(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String mi = encryptHexString("1746059490");
        System.out.println("加密后的内容:" + mi);
        String password = decodeHexString("0FB42654AC0B769147CEFF268198A187");
        System.out.println("解密后的内容:" + password);

    }

    /**
     * 将明文密码转为十六进制字符串密文
     * @return
     */
    public static String encryptHexString(String word) {

        // 如果密码为空则返回空
        if (StringUtils.isEmpty(word)) {
            return null;
        }

        try {
            byte[] keyUtf8Array = key.getBytes("utf-8");

            byte[] ivUtf8Array = iv.getBytes("utf-8");

            byte[] wordUtf8Array = word.getBytes("utf-8");

            byte[] encryptedData = encrypt(wordUtf8Array, keyUtf8Array, ivUtf8Array);

            return encodeHexString(encryptedData).toUpperCase();
        } catch (Exception e) {
            return null;
        }


    }

    /**
     *
     * 解密
     * @return
     */
    public static String decodeHexString(String word) {
        // 如果密码为空则返回空
        if (StringUtils.isEmpty(word)) {
            return StringUtils.EMPTY;
        }

        try {
            byte[] keyUtf8Array = key.getBytes("utf-8");

            byte[] ivUtf8Array = iv.getBytes("utf-8");

            byte[] wordArray = decodeHex(word);

            byte[] decryptedData = decrypt(wordArray, keyUtf8Array, ivUtf8Array);

            return new String(decryptedData, "utf-8");
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }


    }
}
