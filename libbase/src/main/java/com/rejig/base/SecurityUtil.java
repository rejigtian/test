/*******************************************************************************
*
*    Copyright (c) 2013年 wepie. All rights reserved.
*
*    SecurityUtil
*
*    @author: qiyaozheng
*    @since:  April 15, 2014
*
******************************************************************************/

package com.rejig.base;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class SecurityUtil {

    private static String LOG_TAG = "SecurityUtil";


    public static byte[] sha1Hash(byte[] data) {
        if (null == data || data.length == 0) {
            return null;
        }
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("SHA-1");
            algorithm.reset();
            algorithm.update(data);
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] md5Hash(byte[] data) {
        if (null == data || data.length == 0) {
            return null;
        }
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(data);
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] md5Hash(InputStream is) {
        if (null == is) {
            return null;
        }

        MessageDigest algorithm;
        byte[] buffer = new byte[4028];
        int length;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            while ((length = is.read(buffer)) != -1) {
                algorithm.update(buffer, 0, length);
            }
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] md5Hash(File file) {
        if (null == file) {
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return md5Hash(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String generateGUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static PublicKey getPublicKey(byte[] key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static byte[] rsaDecode(byte[] cipherData, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherData);
    }

    public static byte[] rsaEncode(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 计算给定数据的 MD5 签名。
     *
     * @param data
     *            要计算签名的数据。
     * @return data 的 MD5 签名。
     */
    public static byte[] md5(byte[] data) {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update(data);
            return msgDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算给定数据的 MD5 签名。
     *
     * @param value
     *            要计算签名的数据。
     * @return value 的 MD5 签名。
     */
    public static byte[] md5(String value) {
        byte[] data;
        try {
            data = value.getBytes("utf-8");
            return md5(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算给定数据的 MD5 签名。
     *
     * @param data
     *            要计算签名的数据。
     * @return data 的 MD5 签名。
     */
    public static String md5String(byte[] data) {
        byte[] digest = md5(data);
        return asHexString(digest);
    }

    /**
     * 计算给定数据的 MD5 签名。
     *
     * @param value
     *            要计算签名的数据。
     * @return value 的 MD5 签名。
     */
    public static String md5String(String value) {
        byte[] digest = md5(value);
        return asHexString(digest);
    }

    /**
     * 将给定的二进制数据转换为十六进制字符串。
     *
     * @param data
     *            要转换的二进制数据。
     * @return data 的十六进制字符串表示。
     */
    public static String asHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString().toLowerCase(java.util.Locale.US);
    }

    public static String hmacSHA1(String key, String src) {
        byte[] secretKey = key.getBytes();
        SecretKeySpec secret = new SecretKeySpec(secretKey, "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secret);
            byte[] digest = mac.doFinal(src.getBytes());
            return Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String hmacSHA1(String src) {
        byte[] secretKey = "3Q0OEVW45wwODGh08GwBrc118zBGKHO&".getBytes();
        SecretKeySpec secret = new SecretKeySpec(secretKey, "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secret);
            byte[] digest = mac.doFinal(src.getBytes());
            return Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String hmacSHA1UrlSafe(String src) {
        byte[] secretKey = "3Q0OEVW45wwODGh08GwBrc118zBGKHO&".getBytes();
        SecretKeySpec secret = new SecretKeySpec(secretKey, "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secret);
            byte[] digest = mac.doFinal(src.getBytes());
            return Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String rawUrlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, "UTF-8").replaceAll("[*]", "%2A");
    }

}
