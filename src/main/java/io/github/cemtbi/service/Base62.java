package io.github.cemtbi.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Component
public final class Base62 {

	private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
			.toCharArray();

	private static final int BASE = ALPHABET.length;

	private static final SecureRandom RANDOM = new SecureRandom();

	public static String randomCode(int length) {
		if (length <= 0)
			throw new IllegalArgumentException("Length must be greater than 0");

		char[] result = new char[length];
		for (int i = 0; i < length; i++) {
			result[i] = ALPHABET[RANDOM.nextInt(BASE)];
		}
		return new String(result);
	}

}