package de.abat.assignment.UrlShortener.util;

public final class RestResponseMessages {
    private RestResponseMessages() {}

    public static final String TTL_NOT_AN_INTEGER = "Invalid value for parameter 'ttl': '%s'. Must be a positive integer.";
    public static final String ORIGINAL_URL_EXISTS = "Invalid value for parameter 'originalUrl': '%s'. URL is already mapped.";
    public static final String SHORT_URL_EXISTS = "Invalid value for parameter 'shortUrl': '%s'. URL is already mapped.";
}
