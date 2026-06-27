package io.github.cemtbi.urlshortener.service;

import java.time.Clock;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.github.cemtbi.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlCleanupService {
	
	private static final Logger log = LoggerFactory.getLogger(UrlCleanupService.class);
	
	private final UrlRepository urlRepository;
	
	private final Clock clock;
	
	@Scheduled(cron = "0 0 2 * * ?")
	public void cleanupExpiredUrls() {
		log.info("Starting cleanup for expired urls.");
		
		Instant now = Instant.now(clock);
		
		Integer deletedCount = urlRepository.deleteByExpiresAtBefore(now);
		
		log.info("Database cleanup completed. Deleted {} expired urls.", deletedCount);
	}
}
