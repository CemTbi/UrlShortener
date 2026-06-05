package io.github.cemtbi.urlshortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.cemtbi.urlshortener.model.entity.ShortUrl;

@Repository
public interface UrlRepository extends JpaRepository<ShortUrl, Long> {
	Optional<ShortUrl> findByCode(String code);

	boolean existsByCode(String code);
}
