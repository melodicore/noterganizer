package me.datafox.noterganizer.server.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for notes. Contains a UUID, reference to the owning user, a title,
 * textual content and a list of references to child notes.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    @MongoId
    private String uuid;

    @DBRef
    private AppUser user;

    private String title;

    @Builder.Default
    private String content = "";

    @DBRef
    @Builder.Default
    private List<Note> children = new ArrayList<>();
}
