package de.abat.assignment.UrlShortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlCoder {

    static Logger logger = LoggerFactory.getLogger(ShortUrlCoder.class);

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = CHARACTERS.length();

    public static String encode(long id) {
        long savedId = id;
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(CHARACTERS.charAt((int) (id % BASE)));
            id /= BASE;
        }
        String shortUrlRep = !sb.isEmpty() ? sb.reverse().toString() : "0";
        logger.debug("Encoded '{}' to '{}'", savedId, shortUrlRep);
        return shortUrlRep;
    }

    public static long decode(String shortUrlRep) {
        long id = 0;
        for (char c : shortUrlRep.toCharArray()) {
            id = id * BASE + CHARACTERS.indexOf(c);
        }
        logger.debug("Decoded '{}' to '{}'", shortUrlRep, id);
        return id;
    }

    public static String toShortUrl(String shortUrlRep) {
        String shortUrl = "http://localhost:8080/api/url/" + shortUrlRep;
        logger.debug("Turning short URL representation {} to {}", shortUrlRep, shortUrl);
        return shortUrl;
    }
}
