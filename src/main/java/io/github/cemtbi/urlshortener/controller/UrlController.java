package io.github.cemtbi.urlshortener.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.cemtbi.urlshortener.model.dto.UrlRequest;
import io.github.cemtbi.urlshortener.model.dto.UrlResponse;
import io.github.cemtbi.urlshortener.model.entity.ShortUrl;
import io.github.cemtbi.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/urls")
@RestController
public class UrlController {
	
	private final UrlService service;
	
	@PostMapping
    public ResponseEntity<UrlResponse> createUrl(@Valid @RequestBody UrlRequest request) {
		ShortUrl url = service.createShortUrl(request);
		URI location = ServletUriComponentsBuilder
						.fromCurrentRequest()
						.path("/{code}")
						.buildAndExpand(url.getCode())
						.toUri();
		return ResponseEntity
				.created(location)
				.body(UrlResponse.from(url));
	}
	
	@GetMapping("/{code}")
    public ResponseEntity<UrlResponse> retreiveUrl(@PathVariable String code) {
        ShortUrl url = service.findActiveUrl(code)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity
        		.ok(UrlResponse
        		.from(url));
    }
}
