package de.abat.assignment.UrlShortener.service;

import de.abat.assignment.UrlShortener.entity.UrlMapping;
import de.abat.assignment.UrlShortener.repository.UrlMappingRepository;
import de.abat.assignment.UrlShortener.util.ExceptionMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlShortenerService {

    Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private ShortUrlRepGenerator shortUrlRepGenerator;

    /**
     * Checks the validity of user inputs, shortens the original URL, stores it in the database,
     * and returns the shortened URL.
     * @param unsafeOriginalUrl User input, original URL to be shortened.
     * @param unsafeShortUrlRep Optional user input, desired short URL representation.
     * @param unsafeTtl Optional user input, time to live in minutes. -1: On expiration.
     * @return The string of the full shorted URL that redirects the user to the original URL.
     * @throws NumberFormatException in case the provided TTL is not an integer.
     * @throws IllegalArgumentException in case any of the user inputs is malformed.
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
        logger.debug("Saved short URL representation {} to database", shortUrlRep);

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
        // URL validation
        try {
            new URL(unsafeOriginalUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(ExceptionMessages.ORIGINAL_URL_MALFORMED);
        }
        return unsafeOriginalUrl;
    }

    private String checkUnsafeShortUrlRep(Optional<String> shortUrlOptional) {
        String shortUrlRep;
        if (shortUrlOptional.isPresent()) {
            shortUrlRep = shortUrlOptional.get();
            // Check if short URL is alphanumeric
            if (!shortUrlRep.matches("^[0-9a-zA-Z]+$")) {
                throw new IllegalArgumentException(ExceptionMessages.SHORT_URL_NOT_ALPHA);
            }
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
