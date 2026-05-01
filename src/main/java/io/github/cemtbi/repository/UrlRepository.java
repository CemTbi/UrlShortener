package io.github.cemtbi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.cemtbi.model.entity.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long>{

}
