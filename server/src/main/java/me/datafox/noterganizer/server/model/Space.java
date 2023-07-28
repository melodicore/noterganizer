package me.datafox.noterganizer.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * Model for spaces. Contains a UUID, a reference to the owning user and
 * a reference to the root note.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Space {
    @MongoId
    private String uuid;

    @DBRef
    private AppUser user;

    @DBRef
    private Note root;
}
