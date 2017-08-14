package com.haoxi.dove.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017\6\5 0005.
 */

public class MD5Tools {

    public final static String MD5(String pwd){
        char md5String[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};


        byte[] btInput = pwd.getBytes();

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(btInput);

            byte[] md = digest.digest();

            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = md5String[byte0 >>>4 & 0xf];
                str[k++] = md5String[byte0 & 0xf];

            }

            return new String(str);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
