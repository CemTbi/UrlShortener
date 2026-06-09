package io.github.cemtbi.urlshortener.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.cemtbi.urlshortener.model.entity.ShortUrl;
import io.github.cemtbi.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RedirectController {

	
	private final UrlService service;
	
	@GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        ShortUrl url = service.findActiveUrl(code)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.clickEvent(url);

        return ResponseEntity
        		.status(HttpStatus.FOUND) 
                .header(HttpHeaders.LOCATION, url.getUrl())
                .build();
    }
}
