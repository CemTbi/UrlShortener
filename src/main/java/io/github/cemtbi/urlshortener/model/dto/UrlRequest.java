package io.github.cemtbi.urlshortener.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(
        @NotBlank(message = "URL cannot be blank")
        @Size(max = 2048, message = "URL is too long")
        @URL(message = "Invalid URL format")
        String url,

        @Size(min = 3, max = 32, message = "Alias must be between 3 and 32 characters")
        String alias,

        Integer expiryDays
) {}