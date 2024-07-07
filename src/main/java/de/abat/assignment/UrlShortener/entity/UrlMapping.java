package de.abat.assignment.UrlShortener.entity;

import de.abat.assignment.UrlShortener.service.ShortUrlCoder;
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
    private String shortUrlRep;

    @Column(nullable = false, unique = true)
    private String shortUrl;

    @Column(nullable = false, unique = true)
    private long shortUrlId;

    @Column(nullable = false, updatable = false)
    private Integer ttl;  // In minutes

    // For some reason @CreationTimestamp does not work, so I implemented the initialization myself.
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationTimestamp;

    @Column(nullable = false, updatable = false)
    private LocalDateTime expirationTimestamp;

    @PrePersist
    public void prePersist() {
        this.shortUrlId = ShortUrlCoder.decode(this.shortUrlRep);
        this.shortUrl = ShortUrlCoder.toShortUrl(this.shortUrlRep);
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

    public String getShortUrlRep() {
        return shortUrlRep;
    }

    public void setShortUrlRep(String shortUrlRep) {
        this.shortUrlRep = shortUrlRep;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public long getShortUrlId() {
        return shortUrlId;
    }

    public void setShortUrlId(long shortUrlId) {
        this.shortUrlId = shortUrlId;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
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
