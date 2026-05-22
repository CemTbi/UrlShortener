package io.github.cemtbi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.cemtbi.model.entity.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long>{
    Optional<Url> findByCode(String code);
    boolean existsByCode(String code);
}
