package io.github.cemtbi.urlshortener.service;

import java.security.SecureRandom;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Base62 {

	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private static final int BASE = ALPHABET.length(); // OR "RADIX"

	private static final SecureRandom RANDOM = new SecureRandom();

	public static String randomCode(int length) {
		if (length <= 0)
			throw new IllegalArgumentException("Length must be greater than 0");

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomIndex = RANDOM.nextInt(BASE);
			sb.append(ALPHABET.charAt(randomIndex));
		}
		return sb.toString();
	}
}