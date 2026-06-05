package io.github.cemtbi.urlshortener.model.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "urls")
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ShortUrl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 2048)
	private final String url;

	@Column(nullable = false, unique = true, length = 32)
	private final String code;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column(nullable = true)
	private Instant lastAccessedAt;

	@Column(nullable = true)
	private final Instant expiresAt;

	@Column(nullable = false)
	private long clickCount;

}
