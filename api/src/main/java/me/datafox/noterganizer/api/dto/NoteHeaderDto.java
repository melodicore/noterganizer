package me.datafox.noterganizer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto object for referencing a note.
 * Sent to both directions.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteHeaderDto {
    private String uuid;

    private String title;
}
