package me.datafox.noterganizer.client.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Data for the user.
 *
 * @author datafox
 */
@Data
@Builder(toBuilder = true)
public class User {
    private String username;

    @Singular
    private List<SpaceHeader> spaces;
}
