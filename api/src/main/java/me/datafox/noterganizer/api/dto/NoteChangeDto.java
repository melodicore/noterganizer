package me.datafox.noterganizer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto object for changing a note's contents.
 * Sent from client to server.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteChangeDto {
    private String uuid;

    private String title;

    private String content;
}
