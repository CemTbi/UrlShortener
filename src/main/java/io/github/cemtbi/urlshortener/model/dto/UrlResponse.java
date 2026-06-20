package io.github.cemtbi.urlshortener.model.dto;

import java.time.Instant;

import io.github.cemtbi.urlshortener.model.entity.ShortUrl;

public record UrlResponse(
        String code,
        String url,
        Instant expiryDate,
        Instant lastAccessedAt,
        Instant createdAt,
        long clickCount
) {
    public static UrlResponse from(ShortUrl entity) {
        return new UrlResponse(entity.getCode(), entity.getUrl(), entity.getExpiresAt(), entity.getLastAccessedAt(), entity.getCreatedAt(), entity.getClickCount());
  }
}