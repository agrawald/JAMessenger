package com.chat.common.utils;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptionUtils {
    private static final String TAG="EncryptionUtils";
    private static final String ALGO = "AES/ECB/PKCS5Padding";
    private static final String key = "Bar12345Bar12345"; // 128 bit key
    private static Cipher cipher;
    private static final Key aesKey = new SecretKeySpec(key.getBytes(), "AES");

    public static String encrypt(String text){
        try {
            // Create key and cipher
            cipher = Cipher.getInstance(ALGO);
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
            return "";
        }
    }

    public static String decrypt(String text){
        try {
            // Create key and cipher
            cipher = Cipher.getInstance(ALGO);

            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] encryptedVal = Base64.decode(text, Base64.DEFAULT);
            String decrypted = new String(cipher.doFinal(encryptedVal));
            return decrypted;
        }
        catch (Exception e ) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
            return "";
        }
    }
}
