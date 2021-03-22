package com.dj.im.sdk.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by ChenLei on 2020/4/8
 * Describe: 十六进制的工具类
 */
public class HexUtil {

    // 十六进制转字符串的对照表
    private static final char[] KEY_CHARS = new char[]{'东', '经', '为', '价', '值', '而', '奋', '斗', '用', '户', '至', '上', '积', '极', '主', '动'};
    // KEY_CHARS的对照表
    private static final Map<Character, Integer> KEY_CHARS_MAP = new HashMap<>();

    static {
        for (int i = 0; i < KEY_CHARS.length; i++) {
            KEY_CHARS_MAP.put(KEY_CHARS[i], i);
        }
    }

    /**
     * 十六进制数据转字符串
     *
     * @param bytes 十六进制数据
     */
    public static String hex2String(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            //5.取出b的高四位的值
            //先把高四位通过右移操作拽到低四位,&15保留符号转为int
            int high = (b >> 4) & 15;
            //6.取出b的低四位的值
            int low = b & 15;
            //7.以high为下标从characters中取出对应的十六进制字符
            char highChar = KEY_CHARS[high];
            //8.以low为下标从characters中取出对应的十六进制字符
            char lowChar = KEY_CHARS[low];
            builder.append(highChar).append(lowChar);
        }
        return builder.toString();
    }

    /**
     * 秘钥字符串转十六进制数据
     *
     * @param str
     * @return
     */
    public static byte[] string2Hex(String str) {
        if (str.length() % 2 == 0) {
            byte[] bytesKey = new byte[str.length() / 2];
            for (int i = 0; i < bytesKey.length; i++) {
                char highChar = str.charAt(i * 2);
                char lowChar = str.charAt(i * 2 + 1);
                Integer high = KEY_CHARS_MAP.get(highChar);
                Integer low = KEY_CHARS_MAP.get(lowChar);
                if (high != null && low != null) {
                    int value = (high << 4) | low;
                    // 强转byte保留符号
                    bytesKey[i] = (byte) value;
                }
            }
            return bytesKey;
        }
        return null;
    }
}
