package io.github.cemtbi.service;

import io.github.cemtbi.repository.UrlRepository;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CodeService {

    private static final int DEFAULT_LENGTH = 7;

    @NotNull
    private final UrlRepository repository;


    public String generateUniqueCode() {
        for(int i = 0; i < 10; i++) {
            String code = Base62.randomCode(DEFAULT_LENGTH);
            if (!repository.existsByCode(code)) {
                return code;
            }
        }
        throw new RuntimeException("Failed to generate unique code");
    }

    public String validateAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("Alias cannot be null or blank");
        }
        if (!alias.matches("^[0-9a-zA-Z_]{3,32}$")) {
            throw new IllegalArgumentException("Alias must be 3-32 characters long and can only contain letters, digits, and underscores");
        }
        if (repository.existsByCode(alias)) {
            throw new IllegalArgumentException("Alias already in use");
        }
        return alias;
    }
}
