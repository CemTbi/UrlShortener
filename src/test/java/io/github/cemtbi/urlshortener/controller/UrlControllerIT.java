package io.github.cemtbi.urlshortener.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import io.github.cemtbi.urlshortener.model.dto.UrlRequest;
import io.github.cemtbi.urlshortener.model.entity.ShortUrl;
import io.github.cemtbi.urlshortener.repository.UrlRepository;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest          
@AutoConfigureMockMvc
@Transactional 
class UrlControllerIT {

	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UrlRepository repository;
    
    @Autowired
    private Clock clock;

    private ShortUrl existingUrl;
    
    @BeforeEach
    void setup() {
    	ShortUrl url = new ShortUrl("https://example.com", "abc123", Instant.now(clock).plus(1, ChronoUnit.DAYS));
        existingUrl = repository.save(url);
    }
    
 // POST /api/urls

    @Test
    void create_validRequest_returns201WithLocationHeader() throws Exception {
        UrlRequest request = new UrlRequest("https://github.com", "github", 7);

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // 201 Created — not 200
                .andExpect(status().isCreated())
                // Location header must point to the new resource
                .andExpect(header().string("Location", containsString("/api/urls/")))
                // Response body must contain the original URL
                .andExpect(jsonPath("$.url").value("https://github.com"))
                // A code must have been generated
                .andExpect(jsonPath("$.code").isNotEmpty());
    }

    @Test
    void create_invalidUrl_returns400() throws Exception {
        // Empty URL — should fail @Valid bean validation
        UrlRequest request = new UrlRequest("", "empty", 7);

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_missingBody_returns400() throws Exception {
        // Sending a POST with no body at all
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // GET /api/urls/{code}

    @Test
    void get_existingCode_returns200WithUrlResponse() throws Exception {
        mockMvc.perform(get("/api/urls/{code}", existingUrl.getCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("abc123"))
                .andExpect(jsonPath("$.url").value("https://example.com"));
    }

    @Test
    void get_unknownCode_returns404() throws Exception {
        mockMvc.perform(get("/api/urls/{code}", "doesnotexist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_inactiveUrl_returns404() throws Exception {
        // Mark the URL as inactive — findActiveUrl() should not find it
    	ShortUrl url = new ShortUrl("https://inactive.com", "inactive", Instant.now(clock).minus(1, ChronoUnit.DAYS));
        repository.save(url);

        mockMvc.perform(get("/api/urls/{code}", url.getCode()))
                .andExpect(status().isNotFound());
    }
    
}
