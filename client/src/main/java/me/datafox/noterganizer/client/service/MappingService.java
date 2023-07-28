package me.datafox.noterganizer.client.service;

import me.datafox.noterganizer.api.dto.*;
import me.datafox.noterganizer.client.model.Note;
import me.datafox.noterganizer.client.model.Space;
import me.datafox.noterganizer.client.model.User;

/**
 * The mapping service contains helper methods for mapping between internal objects
 * and DTO objects.
 *
 * @author datafox
 */
public interface MappingService {
    /**
     * @param dto DTO object
     * @return user for given DTO object
     */
    User mapToUser(UserDto dto);

    /**
     * @param dto DTO object
     * @return space for given DTO object
     */
    Space mapToSpace(SpaceDto dto);

    /**
     * @param note note
     * @return header DTO object for given note
     */
    NoteHeaderDto mapToNoteHeaderDto(Note note);

    /**
     * @param note note
     * @return change DTO object for given note
     */
    NoteChangeDto mapToNoteChangeDto(Note note);

    /**
     * @param note note to be moved
     * @param newParent new parent note
     * @return move DTO object for given notes
     */
    NoteMoveDto mapToNoteMoveDto(Note note, Note newParent);
}
