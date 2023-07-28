package me.datafox.noterganizer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto object for sending a full space.
 * Sent from server to client.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpaceDto {
    private String uuid;

    private NoteDto root;
}
