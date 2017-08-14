package com.haoxi.dove.utils;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by hyan on 16-12-12.
 */

public class RSAHelper {
    private static String modulus = "126618712665802456639891575903079810574012379420492778855867753046627709756975323905249362068857084580576324007523793731204011338454737593115465901962413502094192220152856850326867041452522226058733907862638123185202586531767334905233957204347890957254301465818973699468586555176448015507058244373880056370413";
    private static String exponent = "65537";

    public static byte[] encrypt(byte[] bytes) {
        if (null == bytes) return null;
        else {
            try {
                RSAPublicKey publicKey = (RSAPublicKey) RSAUtil.getPublicKey(modulus, exponent);
                return RSAUtil.encryptData(bytes, publicKey);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
