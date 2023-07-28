package me.datafox.noterganizer.server.repository;

import me.datafox.noterganizer.server.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for notes.
 *
 * @author datafox
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    Optional<Note> findByChildren(Note child);
}
