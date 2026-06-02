package io.github.cemtbi.model.entity;

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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "urls")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ShortUrl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@Column(nullable = false, length = 2048)
	private String url;

	@NonNull
	@Column(nullable = false, unique = true, length = 32)
	private String code;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();
	
	@Column(nullable = true)
	private Instant lastAccessedAt;

	@NonNull
	@Column(nullable = true)
	private Instant expiresAt;

	@Column(nullable = false)
	private long clickCount = 0;

}
