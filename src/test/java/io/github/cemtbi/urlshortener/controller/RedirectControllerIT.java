package io.github.cemtbi.urlshortener.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import io.github.cemtbi.urlshortener.model.entity.ShortUrl;
import io.github.cemtbi.urlshortener.repository.UrlRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RedirectControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository repository;
    
    @Autowired
    private Clock clock;

    private ShortUrl existingUrl;

    @BeforeEach
    void setUp() {
    	ShortUrl url = new ShortUrl("https://test.com", "test123", Instant.now(clock).plus(1, ChronoUnit.DAYS));
        existingUrl = repository.save(url);
    }

    @Test
    void redirect_existingCode_returns302WithLocationHeader() throws Exception {
        mockMvc.perform(get("/{code}", existingUrl.getCode()))
                // 302 Found = redirect
                .andExpect(status().isFound())
                // Location must be the original URL
                .andExpect(header().string("Location", "https://test.com"));
    }

    @Test
    void redirect_unknownCode_returns404() throws Exception {
        mockMvc.perform(get("/{code}", "nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirect_incrementsClickCount() throws Exception {
        mockMvc.perform(get("/{code}", existingUrl.getCode()));

        // Reload from DB and verify the click was counted
        ShortUrl updated = repository.findById(existingUrl.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updated.getClickCount()).isEqualTo(1);
    }

    @Test
    void redirect_inactiveUrl_returns404() throws Exception {
    	ShortUrl url = new ShortUrl("https://inactive123.com", "inactive123", Instant.now(clock).minus(1, ChronoUnit.DAYS));
        repository.save(url);

        mockMvc.perform(get("/{code}", url.getCode()))
                .andExpect(status().isNotFound());
    }
}