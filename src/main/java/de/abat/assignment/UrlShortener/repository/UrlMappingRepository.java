package de.abat.assignment.UrlShortener.repository;

import de.abat.assignment.UrlShortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    UrlMapping findByOriginalUrl(String originalUrl);
    UrlMapping findByShortUrl(String shortUrl);
    void deleteByShortUrl(String shortUrl);

    /**
     * Deletes all URL mappings with an expiry timestamp in the past.
     * However, only when the expiry timestamp comes after the creation timestamp.
     * As the TTL is subtracted from the creation timestamp to form the expiry timestamp,
     * mappings with indefinite validity i.e. TTL value of -1, will not be deleted.
     * @param now The current timestamp.
     */
    @Modifying
    @Transactional
    @Query("delete from UrlMapping u where u.expirationTimestamp > u.creationTimestamp and u.expirationTimestamp < ?1")
    void deleteByExpiration(LocalDateTime now);
}
