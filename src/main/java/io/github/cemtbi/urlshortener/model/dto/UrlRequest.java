package io.github.cemtbi.urlshortener.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UrlRequest {

	@NotBlank(message = "URL cannot be blank")
	@Size(max = 2048, message = "URL is too long")
	private String url;

	@Size(min = 3, max = 32, message = "Alias must be between 3 and 32 characters")
	private String alias;

	private Integer expiryDays;

}