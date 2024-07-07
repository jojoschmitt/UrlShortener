package de.abat.assignment.UrlShortener.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortUrl;

    @Column(nullable = false, updatable = false)
    private Integer ttl;  // In minutes

    // For some reason @CreationTimestamp does not work, so I implemented the initialization myself.
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationTimestamp;

    @Column(nullable = false, updatable = false)
    private LocalDateTime expirationTimestamp;

    @PrePersist
    public void prePersist() {
        this.expirationTimestamp = this.creationTimestamp.plusMinutes(this.ttl);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTime) {
        this.creationTimestamp = creationTime;
    }

    public LocalDateTime getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(LocalDateTime expirationDate) {
        this.expirationTimestamp = expirationDate;
    }
}
