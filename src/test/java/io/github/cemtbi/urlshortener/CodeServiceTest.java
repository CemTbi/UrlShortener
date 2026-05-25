package io.github.cemtbi.urlshortener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.cemtbi.repository.UrlRepository;
import io.github.cemtbi.service.CodeService;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    CodeService codeService;

    /*
     * Returns code when the first attempt is unique.
     */
    @Test
    void testUniqueCodeGeneration() {
    	// GIVEN
        when(repository.existsByCode(anyString())).thenReturn(false);
        
        // WHEN
        String code = codeService.generateUniqueCode();

        // THEN
        assertThat(code).isNotNull();
        assertThat(code).hasSize(7);
        assertThat(code).matches("^[0-9a-zA-Z]+$");
        verify(repository, times(1)).existsByCode(anyString());
    }
}
