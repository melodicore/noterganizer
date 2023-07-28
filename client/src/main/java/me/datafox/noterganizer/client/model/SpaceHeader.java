package me.datafox.noterganizer.client.model;

import lombok.Builder;
import lombok.Data;

/**
 * Contains only a space's UUID and name.
 *
 * @author datafox
 */
@Data
@Builder
public class SpaceHeader {
    private String uuid;

    private String name;
}
