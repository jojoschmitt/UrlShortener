package de.abat.assignment.UrlShortener.controller;

import de.abat.assignment.UrlShortener.service.UrlShortenerService;
import de.abat.assignment.UrlShortener.util.RestResponseMessages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UrlShortenerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Test
    public void testShortenUrlThin_Success() throws Exception {
        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"http://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/api/url/0"));
    }

    @Test
    public void testShortenUrlFull_Success() throws Exception {
        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"http://example.com\", \"shortUrlRep\":\"e3\", \"ttl\":\"3600\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/api/url/e3"));
    }

    @Test
    public void testShortenUrlOriginalUrlComplex_Success() throws Exception {
        String complexOriginalUrl = "https://www.amazon.de/dp/B0C2S2J7JP/ref=ods_gw_GW_DE_DE_AUCC_BAK_PD24LUTSTO_TSTON_SH/?_encoding=UTF8&pd_rd_w=uFgpX&content-id=amzn1.sym.5f6dd586-7f2a-40ea-afd8-1fa44f8f0f26&pf_rd_p=5f6dd586-7f2a-40ea-afd8-1fa44f8f0f26&pf_rd_r=VWVE3GEGPDVE20R2VH49&pd_rd_wg=8JKX3&pd_rd_r=99a007c5-6bfc-43be-b414-878fedfbbb32&ref_=pd_hp_d_hero_unk";
        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"%s\", \"shortUrlRep\":\"amznEchoSpot\"}", complexOriginalUrl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/api/url/amznEchoSpot"));
    }

    @Test
    public void testShortenUrlOriginalUrlMaxLen_Success() throws Exception {
        String originalUrl = "http:"+"0".repeat(2043);  // 5+2043=2048
        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"%s\", \"shortUrlRep\":\"max\"}", originalUrl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/api/url/max"));
    }

    @Test
    public void testShortenUrlOriginalUrlTooLong_Failure() throws Exception {
        String originalUrl = "http:"+"0".repeat(2044);  // 5+2044=2049
        String expectedErrorMessage = String.format(RestResponseMessages.ORIGINAL_URL_TOO_LONG, originalUrl);

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"%s\"}", originalUrl)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    @Test
    public void testShortenUrlOriginalUrlAlreadyExists_Failure() throws Exception {
        String originalUrl = "http:example.com";
        String expectedErrorMessage = String.format(RestResponseMessages.ORIGINAL_URL_EXISTS, originalUrl);

        // Initialize short URL mapping in the database.
        urlShortenerService.shortenUrl(originalUrl, Optional.empty(), Optional.empty());

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"%s\"}", originalUrl)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    @Test
    public void testShortenUrlOriginalUrlNotUrl_Failure() throws Exception {
        String originalUrl = "http//example.com";
        String expectedErrorMessage = String.format(RestResponseMessages.ORIGINAL_URL_MALFORMED, originalUrl);

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"%s\"}", originalUrl)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    @Test
    public void testShortenUrlWrongRequestParam_Failure() throws Exception {
        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalURL\":\"http://example.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShortenUrlShortUrlRepNotAlpha_Failure() throws Exception {
        String shortUrlRep = "super-rep";
        String expectedErrorMessage = String.format(RestResponseMessages.SHORT_URL_NOT_ALPHA, shortUrlRep);

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"http://example.com\", \"shortUrlRep\":\"%s\"}", shortUrlRep)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    @Test
    public void testShortenUrlTtlNotInteger_Failure() throws Exception {
        String ttl = "notAnInt";
        String expectedErrorMessage = String.format(RestResponseMessages.TTL_NOT_AN_INTEGER, ttl);

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"http://example.com\", \"ttl\":\"%s\"}", ttl)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    @Test
    public void testShortenUrlTtlPositive_Failure() throws Exception {
        String ttl = "-10";
        String expectedErrorMessage = String.format(RestResponseMessages.ONLY_POSITIVE_TTL, ttl);

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"http://example.com\", \"ttl\":\"%s\"}", ttl)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    @Test
    public void testShortenUrlTtlAboveLimit_Failure() throws Exception {
        String ttl = "5256001";
        String expectedErrorMessage = String.format(RestResponseMessages.TTL_LIMIT_EXCEEDED, ttl);

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"http://example.com\", \"ttl\":\"%s\"}", ttl)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    @Test
    public void testShortenUrlShortUrlAlreadyExists_Failure() throws Exception {
        String shortUrlRep = "alreadyExisting0000";
        String expectedErrorMessage = String.format(RestResponseMessages.SHORT_URL_EXISTS, shortUrlRep);

        // Initialize short URL mapping in the database.
        urlShortenerService.shortenUrl("http://one.com", Optional.of(shortUrlRep), Optional.empty());

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"originalUrl\":\"http://two.com\", \"shortUrlRep\":\"%s\"}", shortUrlRep)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    @Test
    public void testRedirectToOriginalUrlSuccess() throws Exception {
        String originalUrl = "http://example.com";
        String shortUrlRep = "exmpl";

        // Initialize short URL mapping in the database.
        urlShortenerService.shortenUrl(originalUrl, Optional.of(shortUrlRep), Optional.empty());

        mockMvc.perform(get("/api/url/" + shortUrlRep))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(originalUrl));
    }

    @Test
    public void testRedirectToOriginalUrlNotFound() throws Exception {
        mockMvc.perform(get("/api/url/nonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteShortenedUrl() throws Exception {
        String shortUrlRep = "exmpl";

        // Assume this setup is done somewhere to initialize the short URL in the database.
        urlShortenerService.shortenUrl("http://example.com", Optional.of(shortUrlRep), Optional.of("3600"));

        mockMvc.perform(delete("/api/url/" + shortUrlRep))
                .andExpect(status().isOk());

        // Verify the URL is deleted (redirect should now return 404)
        mockMvc.perform(get("/api/url/" + shortUrlRep))
                .andExpect(status().isNotFound());
    }
}
