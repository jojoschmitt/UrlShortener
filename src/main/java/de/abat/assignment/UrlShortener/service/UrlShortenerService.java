package de.abat.assignment.UrlShortener.service;

import de.abat.assignment.UrlShortener.entity.UrlMapping;
import de.abat.assignment.UrlShortener.repository.UrlMappingRepository;
import de.abat.assignment.UrlShortener.util.ExceptionMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     *
     * @param unsafeOriginalUrl
     * @param unsafeTtl
     * @return The shortened URL as a string.
     * @throws NumberFormatException in case the provided TTL is not an integer.
     * @throws IllegalArgumentException in case the provided TTL is non-positive, the original or short URL is invalid.
     */
    public String shortenUrl(String unsafeOriginalUrl, Optional<String> unsafeShortUrlId, Optional<String> unsafeTtl) {
        UrlMapping urlMapping = new UrlMapping();

        String originalUrl = checkUnsafeOriginalUrl(unsafeOriginalUrl);
        urlMapping.setOriginalUrl(originalUrl);

        String shortUrlId = checkUnsafeShortUrlId(unsafeShortUrlId);
        String shortUrl = buildShortUrl(shortUrlId);
        urlMapping.setShortUrl(shortUrl);

        int ttl = checkUnsafeTtl(unsafeTtl);
        urlMapping.setTtl(ttl);

        urlMapping.setCreationTimestamp(LocalDateTime.now());

        urlMappingRepository.save(urlMapping);

        return shortUrl;
    }

    private String buildShortUrl(String shortUrlId) {
        return baseUrl + "/api/url/" + shortUrlId;
    }

    public String getOriginalUrl(String shortUrlId) {
        String shortUrl = buildShortUrl(shortUrlId);
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        return (urlMapping != null) ? urlMapping.getOriginalUrl() : null;
    }

    public void deleteShortenedUrl(String shortUrlId) {
        String shortUrl = buildShortUrl(shortUrlId);
        urlMappingRepository.deleteByShortUrl(shortUrl);
    }
    
    private String checkUnsafeOriginalUrl(String unsafeOriginalUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByOriginalUrl(unsafeOriginalUrl);
        if (urlMapping != null) {
            throw new IllegalArgumentException(ExceptionMessages.ORIGINAL_URL_EXISTS);
        }
        // TODO implement check for legit URLs
        return unsafeOriginalUrl;
    }

    private String checkUnsafeShortUrlId(Optional<String> shortUrlOptional) {
        String shortUrlId;
        if (shortUrlOptional.isPresent()) {
            shortUrlId = shortUrlOptional.get();
            // TODO implement check for legit URLs
        } else {
            shortUrlId = generateNewShortUrl();
        }

        String shortUrl = buildShortUrl(shortUrlId);
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping != null) {
            throw new IllegalArgumentException(ExceptionMessages.SHORT_URL_EXISTS);
        }
        return shortUrlId;
    }

    private int checkUnsafeTtl(Optional<String> ttlOptional) {
        int ttl;
        if (ttlOptional.isPresent()) {
            // Check for integer value
            ttl = Integer.parseInt(ttlOptional.get());
            // Deny negative TTL
            if (ttl < 1) {
                throw new IllegalArgumentException(ExceptionMessages.ONLY_POSITIVE_TTL);
            }
            // Limit TTL to 10 years
            if (ttl > 5256000) {
                throw new IllegalArgumentException(ExceptionMessages.TTL_LIMIT_EXCEEDED);
            }
        } else {
            ttl = -1;  // Default infinite
        }
        return ttl;
    }

    private String generateNewShortUrl() {
        // TODO Implement shortUrl generation
        return "abc122";
    }
}
