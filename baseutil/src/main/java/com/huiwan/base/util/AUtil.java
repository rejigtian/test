package com.huiwan.base.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * aes加密与解密
 */
public class AUtil {

    /**
     * 加密
     * 1.创建和初始化密码器
     * 2.内容加密
     * 3.返回字符串
     */
    public static String aInText(String content, String keyStr, String ivStr) {
        try {
            Key key = getKey(keyStr);
            IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
            //1.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            //2.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY，第三个是iv
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            //3.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byte_encode = content.getBytes("utf-8");
            //4.根据密码器的初始化方式--加密：将数据加密
            byte[] byte_AES = cipher.doFinal(byte_encode);
            //5.将加密后的数据转换为字符串并将字符串返回
            return Base64.encode(byte_AES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果有错就返加nulll
        return null;
    }

    public static String aInText(String content) {
        return aInText(content, "dX9PAbRsEtKgsYAW", "dX9PAbRsEtKgsYAW");
    }
    public static String aInText(String content, String key) {
        return aInText(content, key, key);
    }
    private static SecretKeySpec getKey(String strKey) {
        byte[] arrBTmp = strKey.getBytes();
        byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）

        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        return new SecretKeySpec(arrB, "AES");
    }

    public static String aOutText(String content) {
        return aOutText(content, "wespylogingetips", "wespylogingetips");
    }

    public static String aOutText(String content, String key) {
        return aOutText(content, key, key);
    }

    public static String aOutText(String content, String keyStr, String ivStr) {
        try {
            Key secretKey = getKey(keyStr);
            IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] c = Base64.decode(content);
            byte[] result = cipher.doFinal(c);
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
