package me.datafox.noterganizer.server.model;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashSet;
import java.util.Set;

/**
 * Model for the application user. Contains a UUID, a username,
 * a password and a set of roles. Roles are currently unused.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {
    @MongoId
    private String uuid;

    @Indexed(unique = true)
    private String username;

    private String password;

    @Builder.Default
    private Set<String> roles = new HashSet<>();
}
