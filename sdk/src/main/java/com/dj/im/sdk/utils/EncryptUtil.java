package com.dj.im.sdk.utils;

import android.util.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    /**
     * MD5 加密
     *
     * @param plainText 加密内容
     * @return 加密后的密文
     */
    public static String MD5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");//获取MD5实例
            md.update(plainText.getBytes());//此处传入要加密的byte类型值
            byte[] digest = md.digest();//此处得到的是md5加密后的byte类型值

            /*
               下边的运算就是自己添加的一些二次小加密，记住这个千万不能弄错乱，
                   否则在解密的时候，你会发现值不对的（举例：在注册的时候加密方式是一种，
                在我们登录的时候是不是还需要加密它的密码然后和数据库的进行比对，但是
            最后我们发现，明明密码对啊，就是打不到预期效果，这时候你就要想一下，你是否
             有改动前后的加密方式）
            */
            int i;
            StringBuilder sb = new StringBuilder();
            for (int offset = 0; offset < digest.length; offset++) {
                i = digest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    sb.append(0);
                sb.append(Integer.toHexString(i));//通过Integer.toHexString方法把值变为16进制
            }
            return sb.toString().substring(0, 32);//从下标0开始，length目的是截取多少长度的值
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
