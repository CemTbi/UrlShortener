package io.github.cemtbi.urlshortener.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.cemtbi.urlshortener.repository.UrlRepository;

@ExtendWith(MockitoExtension.class)
class UrlCleanupServiceTest {

	@Mock
	private UrlRepository urlRepository;

	@Mock
	private Clock clock;

	@InjectMocks
	private UrlCleanupService urlCleanupService;

	private final Instant fixedInstant = Instant.parse("2026-06-10T12:00:00Z");

	@BeforeEach
	void setUpClock() {
		when(clock.instant()).thenReturn(fixedInstant);
	}

	@Test
	void shouldReturnNumberOfDeletedUrlsOnCleanup() {
		// GIVEN
		when(urlRepository.deleteByExpiresAtBefore(fixedInstant)).thenReturn(5);

		// WHEN
		urlCleanupService.cleanupExpiredUrls();

		// THEN
		verify(urlRepository, times(1)).deleteByExpiresAtBefore(fixedInstant);
	}
}
