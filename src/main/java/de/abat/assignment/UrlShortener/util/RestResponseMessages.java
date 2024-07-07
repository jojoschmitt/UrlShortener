package de.abat.assignment.UrlShortener.util;

public final class RestResponseMessages {
    private RestResponseMessages() {}

    public static final String ORIGINAL_URL_EXISTS = "Invalid value for parameter 'originalUrl': '%s'. URL is already mapped.";
    public static final String ORIGINAL_URL_MALFORMED = "Invalid value for parameter 'originalUrl': '%s'. URL is malformed.";
    public static final String SHORT_URL_EXISTS = "Invalid value for parameter 'shortUrlRep': '%s'. URL is already mapped.";
    public static final String SHORT_URL_NOT_ALPHA = "Invalid value for parameter 'shortUrlRep': '%s'. URL is not alphanumeric.";
    public static final String TTL_NOT_AN_INTEGER = "Invalid value for parameter 'ttl': '%s'. Must be a positive integer.";
    public static final String ONLY_POSITIVE_TTL = "Invalid value for parameter 'ttl': '%s'. Must be a positive integer.";
    public static final String TTL_LIMIT_EXCEEDED = "Invalid value for parameter 'ttl': '%s'. Must not exceed 5256000 (10 years).";
}
