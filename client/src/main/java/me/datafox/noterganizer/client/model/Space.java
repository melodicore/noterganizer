package me.datafox.noterganizer.client.model;

import lombok.Builder;
import lombok.Data;

/**
 * Contains all space data.
 *
 * @author datafox
 */
@Data
@Builder
public class Space {
    private String uuid;

    private Note root;
}
