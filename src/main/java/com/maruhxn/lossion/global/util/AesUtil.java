package com.maruhxn.lossion.global.util;

import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class AesUtil {
    @Value("${aes.secret-key}")
    private static String privateKey;
    @Value("${aes.iv}")
    private static String privateIv;
    private static final String ALGORITHM = "AES";
    private static final String SPEC_NAME = "AES/CBC/PKCS5Padding";

    public static Key getKey() {
        byte[] keyBytes = new byte[16];
        byte[] b = privateKey.getBytes(StandardCharsets.UTF_8);

        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }

        System.arraycopy(b, 0, keyBytes, 0, len);

        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * 초기화 벡터 반환
     */
    public static IvParameterSpec getIv() {
        return new IvParameterSpec(privateIv.getBytes(StandardCharsets.UTF_8));
    }

    public static String encrypt(String plainText) {
        Cipher cipher = null;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance(SPEC_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), getIv());
            encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR);
        }
        return new String(Base64.getEncoder().encode(encrypted));
    }

    public static String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(SPEC_NAME);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), getIv()); // 모드가 다르다.
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.INTERNAL_ERROR);
        }
    }
}
