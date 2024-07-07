package de.abat.assignment.UrlShortener.service;

import de.abat.assignment.UrlShortener.entity.UrlMapping;
import de.abat.assignment.UrlShortener.repository.UrlMappingRepository;
import de.abat.assignment.UrlShortener.util.ExceptionMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private ShortUrlRepGenerator shortUrlRepGenerator;

    /**
     *
     * @param unsafeOriginalUrl
     * @param unsafeTtl
     * @return The shortened URL as a string.
     * @throws NumberFormatException in case the provided TTL is not an integer.
     * @throws IllegalArgumentException in case the provided TTL is non-positive, the original or short URL is invalid.
     */
    public String shortenUrl(String unsafeOriginalUrl, Optional<String> unsafeShortUrlRep, Optional<String> unsafeTtl) {
        UrlMapping urlMapping = new UrlMapping();

        String originalUrl = checkUnsafeOriginalUrl(unsafeOriginalUrl);
        urlMapping.setOriginalUrl(originalUrl);

        String shortUrlRep = checkUnsafeShortUrlRep(unsafeShortUrlRep);
        urlMapping.setShortUrlRep(shortUrlRep);

        int ttl = checkUnsafeTtl(unsafeTtl);
        urlMapping.setTtl(ttl);

        urlMapping.setCreationTimestamp(LocalDateTime.now());

        urlMappingRepository.save(urlMapping);

        return urlMappingRepository.findByShortUrlRep(shortUrlRep).getShortUrl();
    }

    public String getOriginalUrl(String shortUrlRep) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrlRep(shortUrlRep);
        return (urlMapping != null) ? urlMapping.getOriginalUrl() : null;
    }

    @Transactional
    public void deleteShortenedUrl(String shortUrlRep) {
        urlMappingRepository.deleteByShortUrlRep(shortUrlRep);
    }
    
    private String checkUnsafeOriginalUrl(String unsafeOriginalUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByOriginalUrl(unsafeOriginalUrl);
        if (urlMapping != null) {
            throw new IllegalArgumentException(ExceptionMessages.ORIGINAL_URL_EXISTS);
        }
        // TODO implement check for legit URLs
        return unsafeOriginalUrl;
    }

    private String checkUnsafeShortUrlRep(Optional<String> shortUrlOptional) {
        String shortUrlRep;
        if (shortUrlOptional.isPresent()) {
            shortUrlRep = shortUrlOptional.get();
            // TODO implement check for legit URLs
        } else {
            shortUrlRep = shortUrlRepGenerator.getNextShortUrlRep();
        }

        UrlMapping urlMapping = urlMappingRepository.findByShortUrlRep(shortUrlRep);
        if (urlMapping != null) {
            throw new IllegalArgumentException(ExceptionMessages.SHORT_URL_EXISTS);
        }
        return shortUrlRep;
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
}
