package io.github.cemtbi.urlshortener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.cemtbi.urlshortener.service.Base62;

class Base62Test {

	@Test
	void testRandomCodeProperties() {
		int targetLength = 7;
		String code = Base62.randomCode(targetLength);

		// 1. Test: Is the code of the expected length?
		assertEquals(targetLength, code.length(), "The code does not have the expected length.");

		// 2. Test: Only valid characters (0-9, A-Z, a-z)
		// (0-9, A-Z, a-z)
		assertTrue(code.matches("^[\\w]+$"), "The code contains invalid special characters.");
	}

	@Test
	void testRandomCodeUniqueness() {
		// 3. Test: Are two consecutive codes different?
		String code1 = Base62.randomCode(7);
		String code2 = Base62.randomCode(7);

		assertNotEquals(code1, code2, "The two consecutive codes should not be identical.");
	}

	@Test
	void testInvalidLength() {
		// 4. Test: Does the method throw an exception as expected for invalid length?
		assertThrows(IllegalArgumentException.class, () -> {
			Base62.randomCode(0);
		});
	}
}
