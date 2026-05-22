package io.github.cemtbi.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 2048)
	private String originalUrl;

	@Column(nullable = false, unique = true, length = 10)
	private String code;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = true)
	private LocalDateTime expiresAt;

	@Column(nullable = false)
	private long clickCount = 0;

	

}
