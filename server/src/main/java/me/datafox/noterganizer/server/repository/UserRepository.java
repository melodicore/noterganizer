package me.datafox.noterganizer.server.repository;

import me.datafox.noterganizer.server.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for users.
 *
 * @author datafox
 */
@Repository
public interface UserRepository extends MongoRepository<AppUser, String> {
    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);
}
