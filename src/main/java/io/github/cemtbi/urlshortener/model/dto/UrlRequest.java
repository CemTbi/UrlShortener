package io.github.cemtbi.urlshortener.model.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UrlRequest(
        @NotBlank(message = "URL cannot be blank")
        @Size(max = 2048, message = "URL is too long")
        @URL(message = "Invalid URL format")
        String url,

        @Size(min = 3, max = 32, message = "Alias must be between 3 and 32 characters")
        String alias,

        @Positive(message = "Expiry days must be greater than 0")
        Integer expiryDays
) {}