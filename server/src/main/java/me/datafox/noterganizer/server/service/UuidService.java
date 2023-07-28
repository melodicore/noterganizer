package me.datafox.noterganizer.server.service;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The UUID service contains a helper function for creating unique UUIDs
 * for a given repository.
 *
 * @author datafox
 */
public interface UuidService {
    /**
     * @param repository repository to be checked
     * @return a unique UUID not associated with any entry in the repository.
     */
    String createUniqueUuid(MongoRepository<?,String> repository);
}
