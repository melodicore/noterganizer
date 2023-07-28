package me.datafox.noterganizer.server.repository;

import me.datafox.noterganizer.server.model.AppUser;
import me.datafox.noterganizer.server.model.Space;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.util.Streamable;

/**
 * Repository for spaces.
 *
 * @author datafox
 */
public interface SpaceRepository extends MongoRepository<Space, String> {
    Streamable<Space> findByUser(AppUser user);
}
