package io.github.cemtbi.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Component
public final class Base62 {

	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            

	private static final int BASE = ALPHABET.length;

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