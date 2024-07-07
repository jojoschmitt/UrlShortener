package de.abat.assignment.UrlShortener.util;

public final class ExceptionMessages {
    private ExceptionMessages() {}

    public static final String ORIGINAL_URL_EXISTS = "Original URL already exists.";
    public static final String ORIGINAL_URL_MALFORMED = "Malformed URL.";
    public static final String SHORT_URL_EXISTS = "Short URL already exists.";
    public static final String SHORT_URL_NOT_ALPHA = "Provided short URL representation is not alphanumeric.";
    public static final String ONLY_POSITIVE_TTL = "Only positive TTLs allowed.";
    public static final String TTL_LIMIT_EXCEEDED = "TTL must not exceed 10 years.";
}
