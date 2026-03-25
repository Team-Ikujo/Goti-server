package com.goti.global.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * 데이터를 AES-256-GCM으로 암호화한 후 Base64 URL-Safe 문자열로 반환합니다.
 */
@Slf4j
@Component
public class TokenEncryptor {

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int GCM_IV_LENGTH = 12; // GCM 권장 IV 길이
	private static final int GCM_TAG_LENGTH = 128; // 인증 태그 길이

	private final SecretKey secretKey;
	private final SecureRandom secureRandom;

	public TokenEncryptor(@Value("${queue.token.secret-key}") String secretKeyString) {
		if (secretKeyString == null || secretKeyString.getBytes(StandardCharsets.UTF_8).length != 32) {
			throw new IllegalArgumentException("AES-256 Secret Key는 반드시 32바이트여야 합니다.");
		}
		this.secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8), "AES");
		this.secureRandom = new SecureRandom();
	}

	public String encrypt(String rawData) {
		try {
			byte[] iv = new byte[GCM_IV_LENGTH];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

			byte[] cipherText = cipher.doFinal(rawData.getBytes(StandardCharsets.UTF_8));

			ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
			byteBuffer.put(iv);
			byteBuffer.put(cipherText);

			return Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());

		} catch (Exception e) {
			log.error("토큰 암호화 중 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 에러코드 지정 필요
		}
	}

	public String decrypt(String encryptedToken) {
		try {
			byte[] decoded = Base64.getUrlDecoder().decode(encryptedToken);

			if (decoded.length < GCM_IV_LENGTH) {
				throw new IllegalArgumentException("토큰의 길이가 너무 짧습니다.");
			}

			byte[] iv = new byte[GCM_IV_LENGTH];
			System.arraycopy(decoded, 0, iv, 0, iv.length);

			byte[] cipherText = new byte[decoded.length - iv.length];
			System.arraycopy(decoded, iv.length, cipherText, 0, cipherText.length);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

			byte[] plainText = cipher.doFinal(cipherText);

			return new String(plainText, StandardCharsets.UTF_8);

		} catch (Exception e) {
			log.error("토큰 복호화 실패. 위조되었거나 손상된 토큰입니다. Token: {}", encryptedToken, e);
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
	}
}