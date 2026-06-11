package io.github.cemtbi.urlshortener.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.cemtbi.urlshortener.model.dto.UrlRequest;
import io.github.cemtbi.urlshortener.model.entity.ShortUrl;
import io.github.cemtbi.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlService {

	private final UrlRepository repository;
	private final CodeService codeService;

	public ShortUrl createShortUrl(UrlRequest request) {
		String url = normalizeUrl(request.url());
		String code = (request.alias() != null && !request.alias().isBlank())
				? codeService.validateAlias(request.alias().trim())
				: codeService.generateUniqueCode();

		Instant expiresAt = calculateExpiryDays(request.expiryDays());

		ShortUrl shortUrl = new ShortUrl(url, code, expiresAt);
		return repository.save(shortUrl);
	}

	static Instant calculateExpiryDays(Integer expiryDays) {
		return (expiryDays != null && expiryDays > 0) ? Instant.now().plus(expiryDays, ChronoUnit.DAYS) : null;
	}

	static String normalizeUrl(String url) {
		if (url == null || url.isBlank()) {
			throw new IllegalArgumentException("URL cannot be empty");
		}

		String cleaned = url.trim();

		if (!cleaned.startsWith("http://") && !cleaned.startsWith("https://")) {
			cleaned = "https://" + cleaned;
		}

		try {
			return URI.create(cleaned)
					  .normalize()
					  .toURL()
					  .toString();
		} catch (IllegalArgumentException | MalformedURLException e) {
			throw new IllegalArgumentException("Invalid URL structure", e);
		}
	}

	public Optional<ShortUrl> findActiveUrl(String code) {
		return repository.findByCode(code)
				.filter(url -> url.getExpiresAt() == null || url.getExpiresAt().isAfter(Instant.now()));
	}

	public void clickEvent(ShortUrl shortUrl) {
		shortUrl.setClickCount(shortUrl.getClickCount() + 1);
		shortUrl.setLastAccessedAt(Instant.now());
		repository.save(shortUrl);
	}
}
