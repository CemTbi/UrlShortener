package io.github.cemtbi.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.cemtbi.urlshortener.model.dto.UrlRequest;
import io.github.cemtbi.urlshortener.model.entity.ShortUrl;
import io.github.cemtbi.urlshortener.repository.UrlRepository;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
	
	@Mock
	private UrlRepository repository;

	@Mock
	private CodeService codeService;
	
	@InjectMocks
	private UrlService urlService;

	
	//------------------------------------------------------------
	// CreateShortUrl()
	//------------------------------------------------------------
	
	@Test
	void createShortUrl_withValidCustomAlias_returnsShortUrlWithAlias() {
		//GIVEN 
		UrlRequest request = new UrlRequest();
		request.setUrl("https://example.de");
		request.setAlias("my_alias123");
		request.setExpiryDays(7);
		
		when(codeService.validateAlias(request.getAlias())).thenReturn(request.getAlias());
		when(repository.save(any(ShortUrl.class))).thenAnswer(i -> i.getArgument(0));
		
		//WHEN
		ShortUrl result = urlService.createShortUrl(request);
		
		//THEN
		assertThat(result).isNotNull();
		assertThat(result.getUrl()).isEqualTo(request.getUrl());
		assertThat(result.getCode()).isEqualTo(request.getAlias());
		
		verify(codeService).validateAlias(request.getAlias());
		verify(repository).save(any(ShortUrl.class));
		verify(codeService, never()).generateUniqueCode();
	}
	
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "   "})
	void createShortUrl_withMissingOrBlankAlias_generatesRandomUniqueCode(String invalidAlias) {
		//GIVEN 
		UrlRequest request = new UrlRequest();
		request.setUrl("https://example.de");
		request.setAlias(invalidAlias);
		request.setExpiryDays(7);
		
		String code = Base62.randomCode(7);
		when(codeService.generateUniqueCode()).thenReturn(code);
		when(repository.save(any(ShortUrl.class))).thenAnswer(i -> i.getArgument(0));
		
		//WHEN
		ShortUrl result = urlService.createShortUrl(request);
		
		//THEN
		assertThat(result.getCode()).isEqualTo(code);
		assertThat(result.getUrl()).isEqualTo(request.getUrl());
		
		verify(codeService).generateUniqueCode();
		verify(codeService, never()).validateAlias(any());
		verify(repository).save(any(ShortUrl.class));
	}
	
	
	@ParameterizedTest
	@NullSource
	@ValueSource(ints = 0)
	void createShortUrl_withMissingOrZeroExpiryDays_setsNoExpirationDay(Integer invalidExpiryDays) {
		//GIVEN 
		UrlRequest request = new UrlRequest();
		request.setUrl("https://example.de");
		request.setAlias("my_alias123");
		request.setExpiryDays(invalidExpiryDays);
		
		when(codeService.validateAlias(request.getAlias())).thenReturn(request.getAlias());
		when(repository.save(any(ShortUrl.class))).thenAnswer(i -> i.getArgument(0));
		
		//WHEN
		ShortUrl result = urlService.createShortUrl(request);
		
		//THEN
		assertThat(result.getExpiresAt()).isNull(); 
		assertThat(result.getUrl()).isEqualTo(request.getUrl());
		verify(repository).save(any(ShortUrl.class));
	}
	
	
	//------------------------------------------------------------
	// normalizeUrl()
	//------------------------------------------------------------
	
	@ParameterizedTest
	@CsvSource({
		"https://google.com, https://google.com",
		"http://example.de,  http://example.de",
		"google.com, 		 https://google.com",
		"example.de, 		 https://example.de"
	})
	void normalizeUrl_withVariousFormats_returnUrl(String input, String expected) {
		assertThat(UrlService.normalizeUrl(input)).isEqualTo(expected);
	}
	
	
	@ParameterizedTest
	@NullAndEmptySource
	void normalizeUrl_withMissingOrEmptyUrl_throwsIllegalArgumentException(String invalidUrl) {
		assertThatThrownBy(() -> UrlService.normalizeUrl(invalidUrl))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("URL cannot be empty");
	}
	
	
	@ParameterizedTest
	@ValueSource(strings = {
		"https://test .com",   
		"https://test.de:80a", 
		"https://"
	})
	void normalizeUrl_withMalformedStructure_throwsException(String invalidUrl) {
		assertThatThrownBy(() -> UrlService.normalizeUrl(invalidUrl))
			.isInstanceOfAny(IllegalArgumentException.class, MalformedURLException.class)
			.hasMessage("Invalid URL structure");
	}
	
	
	//------------------------------------------------------------
	// findActiveUrl()
	//------------------------------------------------------------
	
	
	@ParameterizedTest
	@NullSource
	@ValueSource(ints = 7)
	void findActiveUrl_withExisitngAndNonExpiredUrl_returnsOptionalWithShortUrl(Integer days) {
		// GIVEN
		ShortUrl shortUrl = new ShortUrl(null, Base62.randomCode(7), UrlService.calculateExpiryDays(days));
		
		when(repository.findByCode(shortUrl.getCode())).thenReturn(Optional.of(shortUrl));
		
		//WHEN
		Optional<ShortUrl> result = urlService.findActiveUrl(shortUrl.getCode());

		// THEN
		assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo(shortUrl.getCode());
        assertThat(result.get().getExpiresAt()).satisfiesAnyOf(
            expiresAt -> assertThat(expiresAt).isNull(),
            expiresAt -> assertThat(expiresAt).isAfter(Instant.now())
        );
		
		verify(repository).findByCode(shortUrl.getCode());
	}
	
	
	@Test
	void findActiveUrl_withExpiredUrl_returnsEmptyOptional() {
		// GIVEN
		ShortUrl expired = new ShortUrl(null, Base62.randomCode(7), Instant.now().minus(1, ChronoUnit.DAYS));
		
		when(repository.findByCode(expired.getCode())).thenReturn(Optional.of(expired));
		
		//WHEN
		Optional<ShortUrl> result = urlService.findActiveUrl(expired.getCode());

		// THEN
		assertThat(result).isEmpty();
		verify(repository).findByCode(expired.getCode());
	}
	
	
	@Test
    void findActiveUrl_withUnknownCode_returnsEmpty() {
        // GIVEN 
        when(repository.findByCode("unknown")).thenReturn(Optional.empty());

        // WHEN
        Optional<ShortUrl> result = urlService.findActiveUrl("unknown");

        // THEN
        assertThat(result).isEmpty();
        verify(repository).findByCode("unknown");
    }
	
	
	//------------------------------------------------------------
	// clickEvent()
	//------------------------------------------------------------
	
	
	@Test
	void clickEvent_calledOnce_accmulatesClickCount() {
		// GIVEN
		ShortUrl shortUrl = new ShortUrl(null, Base62.randomCode(7), Instant.now().plus(1, ChronoUnit.DAYS));
		long countBefore = shortUrl.getClickCount();
		when(repository.save(any(ShortUrl.class))).thenAnswer(i -> i.getArgument(0));
		
		//WHEN
		urlService.clickEvent(shortUrl);

		// THEN
		assertThat(shortUrl.getClickCount()).isEqualTo(countBefore + 1);
		assertThat(shortUrl.getLastAccessedAt()).isNotNull()
			.isAfterOrEqualTo(Instant.now().minusSeconds(2));
		
		verify(repository).save(any(ShortUrl.class));
	}
	
	
	@Test
    void clickEvent_calledMultipleTimes_accumulatesClickCount() {
        // GIVEN
        ShortUrl shortUrl = new ShortUrl(null, Base62.randomCode(7), null);
        when(repository.save(any(ShortUrl.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        urlService.clickEvent(shortUrl);
        urlService.clickEvent(shortUrl);
        urlService.clickEvent(shortUrl);

        // THEN
        assertThat(shortUrl.getClickCount()).isEqualTo(3);
        verify(repository, times(3)).save(shortUrl);
    }
}
