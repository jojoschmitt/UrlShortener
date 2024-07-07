package de.abat.assignment.UrlShortener.service;

import de.abat.assignment.UrlShortener.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CleanupService {

    Logger logger = LoggerFactory.getLogger(CleanupService.class);

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)  // Run every 60 seconds
    public void cleanupExpiredMappings() {
        logger.debug("Cleaning up expired short URLs");
        LocalDateTime now = LocalDateTime.now();
        urlMappingRepository.deleteByExpiration(now);
    }
}
