package io.github.cemtbi.urlshortener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.cemtbi.repository.UrlRepository;
import io.github.cemtbi.service.CodeService;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

	@Mock
	private UrlRepository repository;

	@InjectMocks
	CodeService codeService;

	// Code generation
	/*
	 * Returns code when the first attempt is unique.
	 */
	@Test
	void testUniqueCodeGeneration() {
		// GIVEN
		when(repository.existsByCode(anyString())).thenReturn(false);

		// WHEN
		String code = codeService.generateUniqueCode();

		// THEN
		assertThat(code).isNotNull().hasSize(7).matches("^[\\w]+$");

		verify(repository, times(1)).existsByCode(anyString());
	}

	/*
	 * Successfully generates a unique code after multiple attempts.
	 */
	@Test
	void testUniqueCodeGenerationWithRetries() {
		// GIVEN
		when(repository.existsByCode(anyString())).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true)
				.thenReturn(false);

		// WHEN
		String code = codeService.generateUniqueCode();

		// THEN
		assertThat(code).isNotNull().hasSize(7).matches("^[\\w]+$");

		verify(repository, times(5)).existsByCode(anyString());
	}

	/*
	 * Fails to generate a unique code after 10 attempts and throws an exception.
	 */
	@Test
	void testUniqueCodeGenerationFailure() {
		// GIVEN
		when(repository.existsByCode(anyString())).thenReturn(true);

		// WHEN + THEN
		assertThatThrownBy(() -> codeService.generateUniqueCode())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Failed to generate unique code");

		verify(repository, times(10)).existsByCode(anyString());
	}

	// Alias validation
	/*
	 * Validates the format of the generated alias.
	 */
	@Test
	void aliasValidation() {
		// GIVEN
		when(repository.existsByCode("my_alias123")).thenReturn(false);

		// WHEN
		String result = codeService.validateAlias("my_alias123");

		// THEN
		assertThat(result).isEqualTo("my_alias123");
	}

	/*
	 * Validate that alias is not null.
	 */
	@Test
	void aliasValidationNotNull() {
		assertThatThrownBy(() -> codeService.validateAlias(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Alias cannot be null or blank");
		verifyNoInteractions(repository);
	}

	/*
	 * Validate that alias is not blank.
	 */
	@Test
	void aliasValidationNotBlank() {
		assertThatThrownBy(() -> codeService.validateAlias("   "))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Alias cannot be null or blank");
		verifyNoInteractions(repository);
	}

	/*
	 * Validate that alias must be 3-32 characters long.
	 */
	@Test
	void aliasValidationLength() {
		assertThatThrownBy(() -> codeService.validateAlias("ab"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Alias must be 3-32 characters long");
		verifyNoInteractions(repository);
	}

	/*
	 * Validate that alias can only contain letters, digits, and underscores.
	 */
	@Test
	void aliasValidationCharacters() {
		assertThatThrownBy(() -> codeService.validateAlias("ab!"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("can only contain letters, digits, and underscores");
		verifyNoInteractions(repository);
	}

	/*
	 * Validate that alias does not already exist in the repository.
	 */
	@Test
	void aliasValidationExists() {
		// GIVEN
		when(repository.existsByCode("my_alias123")).thenReturn(true);

		// WHEN + THEN
		assertThatThrownBy(() -> codeService.validateAlias("my_alias123"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Alias already in use");
	}
}
