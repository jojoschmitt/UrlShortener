package de.abat.assignment.UrlShortener.service;

import de.abat.assignment.UrlShortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.PriorityQueue;

import static de.abat.assignment.UrlShortener.service.ShortUrlCoder.encode;

@Component
public class ShortUrlRepGenerator {

    private final PriorityQueue<Long> reusableIds = new PriorityQueue<>();

    @Autowired
    private UrlMappingRepository urlMappingRepository;


    public synchronized String getNextShortUrlRep() {
        List<Long> shortUrIds = urlMappingRepository.findAllShortUrlIds();
        Long smallestId = 0L;
        while (shortUrIds.contains(smallestId)) {
            smallestId++;
        }
        return encode(smallestId);
    }
}
