package me.datafox.noterganizer.api.dto;

import lombok.*;

import java.util.List;

/**
 * Dto object for sending full note trees.
 * Sent from server to client.
 * Should only be present within {@link SpaceDto}.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteDto {
    private String uuid;

    private String title;

    private String content;

    @Singular
    private List<NoteDto> children;
}
