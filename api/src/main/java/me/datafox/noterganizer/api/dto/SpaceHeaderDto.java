package me.datafox.noterganizer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto object for referencing a space.
 * Sent from server to client.
 * Should only be present within {@link UserDto}.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpaceHeaderDto {
    private String uuid;

    private NoteHeaderDto root;
}
