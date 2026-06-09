package io.github.cemtbi.urlshortener.model.dto;

import io.github.cemtbi.urlshortener.model.entity.ShortUrl;

public record UrlResponse(
        String code,
        String url,
        long clickCount
) {
    public static UrlResponse from(ShortUrl entity) {
        return new UrlResponse(entity.getCode(), entity.getUrl(), entity.getClickCount());
  }
}