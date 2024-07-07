package de.abat.assignment.UrlShortener.service;

import org.springframework.stereotype.Component;

@Component
public class ShortUrlCoder {

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = CHARACTERS.length();

    public static String encode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(CHARACTERS.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return !sb.isEmpty() ? sb.reverse().toString() : "0";
    }

    public static long decode(String shortUrlRep) {
        long id = 0;
        for (char c : shortUrlRep.toCharArray()) {
            id = id * BASE + CHARACTERS.indexOf(c);
        }
        return id;
    }

    public static String toShortUrl(String shortUrlRep) {
        return "http://localhost:8080/api/url/" + shortUrlRep;
    }
}
