package de.abat.assignment.UrlShortener.controller;

import de.abat.assignment.UrlShortener.service.UrlShortenerService;
import de.abat.assignment.UrlShortener.util.ExceptionMessages;
import de.abat.assignment.UrlShortener.util.RestResponseMessages;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/url")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping(value = "/shorten", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> requestBody) {
        String originalUrl = requestBody.get("originalUrl");
        String shortUrlRepStr = requestBody.get("shortUrlRep");
        Optional<String> shortUrlRepOptional = Optional.ofNullable(shortUrlRepStr);
        String ttlStr = requestBody.get("ttl");
        Optional<String> ttlOptional = Optional.ofNullable(ttlStr);

        try {
            String shortUrl = urlShortenerService.shortenUrl(originalUrl, shortUrlRepOptional, ttlOptional);
            ShortUrlResponse shortUrlResponse = new ShortUrlResponse(shortUrl);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(shortUrlResponse);
        } catch (NumberFormatException e) {
            String message = String.format(RestResponseMessages.TTL_NOT_AN_INTEGER, ttlStr);
            return badRequest(message);
        } catch (IllegalArgumentException e) {
            String exceptionMessage = e.getMessage();
            if (exceptionMessage.contains(ExceptionMessages.ONLY_POSITIVE_TTL)) {
                String message = String.format(RestResponseMessages.TTL_NOT_AN_INTEGER, ttlStr);
                return badRequest(message);
            }
            if (exceptionMessage.contains(ExceptionMessages.ORIGINAL_URL_EXISTS)) {
                String message = String.format(RestResponseMessages.ORIGINAL_URL_EXISTS, originalUrl);
                return badRequest(message);
            }
            if (exceptionMessage.contains(ExceptionMessages.SHORT_URL_EXISTS)) {
                String message = String.format(RestResponseMessages.SHORT_URL_EXISTS, shortUrlRepStr);
                return badRequest(message);
            }
            return badRequest(ttlStr);
        }
    }

    private ResponseEntity<ErrorResponse> badRequest(String responseMessage) {
        ErrorResponse errorResponse = new ErrorResponse(responseMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    private static class ShortUrlResponse {

        private String shortUrl;

        public ShortUrlResponse(String shortUrl) {
            this.shortUrl = shortUrl;
        }

        public String getShortUrl() {
            return shortUrl;
        }

        public void setShortUrl(String shortUrl) {
            this.shortUrl = shortUrl;
        }
    }

    private static class ErrorResponse {
        private String errorMessage;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    @GetMapping("/{shortUrlRep}")
    public void redirectToOriginalUrl(@PathVariable String shortUrlRep, HttpServletResponse response) throws IOException {
        String originalUrl = urlShortenerService.getOriginalUrl(shortUrlRep);
        if (originalUrl != null) {
            response.sendRedirect(originalUrl);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @DeleteMapping("/{shortUrlRep}")
    public void deleteShortenedUrl(@PathVariable String shortUrlRep) {
        urlShortenerService.deleteShortenedUrl(shortUrlRep);
    }

}
