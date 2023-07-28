package me.datafox.noterganizer.server.service.impl;

import me.datafox.noterganizer.server.service.UuidService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * UUID service implementation.
 *
 * @author datafox
 */
@Service
public class UuidServiceImpl implements UuidService {
    @Override
    public String createUniqueUuid(MongoRepository<?,String> repository) {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while(repository.existsById(uuid));
        return uuid;
    }
}
