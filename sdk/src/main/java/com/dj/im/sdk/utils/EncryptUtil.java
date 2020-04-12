package com.dj.im.sdk.utils;

import android.util.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Create by ChenLei on 2020/4/8
 * Describe: 加解密工具类
 * 主要提供AES对称加密 和 RSA非对称加密
 */
public class EncryptUtil {

    private static final String IV_STRING = "------djim------";
    // RSA公钥(取自 rsa_public_key.pem)
    private static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3xpr7jzWvJ70cT7Q8RGKCyAI1MU86CJBBEzwhcEvfGqUxLSYZHbTxCUU0P+plVfJmVDq6Kh4UBg+//+wHhe8MZfV917jCh/7vvWGtedJq6NidckmZUNnPDCxx3WlXyIxSOWpjfol7FQLw72u9r5gRrSQgndqq87l8WMfgY9UuLwIDAQAB";

    /**
     * 生成一个对称加密的秘钥
     */
    public static byte[] generateSymmetricEncryptionKey() {
        byte[] keyBytes = new byte[16];
        for (int i = 0; i < keyBytes.length; i++) {
            // 随机生成 -128~127 数字
            int random = new Random().nextInt(256) - 128;
            keyBytes[i] = (byte) random;
        }
        // 将byte转为字符串
        return keyBytes;
    }

    /**
     * 对称加密
     *
     * @param secretByte 秘钥
     * @param data       待加密的数据
     * @return 加密后的数据
     */
    public static byte[] symmetricEncrypt(byte[] secretByte, byte[] data) {
        Key key = new SecretKeySpec(secretByte, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV_STRING.getBytes()));
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对称解密
     *
     * @param secretByte 秘钥
     * @param data       加密后的数据
     * @return 解密后的数据
     */
    public static byte[] symmetricDecrypt(byte[] secretByte, byte[] data) {
        Key key = new SecretKeySpec(secretByte, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV_STRING.getBytes()));
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 非对称算法公钥加密
     * asymmetricalEncrypt
     *
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public static byte[] asymmetricalEncrypt(byte[] data) {
        try {
            byte[] publicKeyByte = Base64.decode(RSA_PUBLIC, Base64.DEFAULT);
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //初始化公钥
            //密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyByte);
            //产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

            //数据加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
