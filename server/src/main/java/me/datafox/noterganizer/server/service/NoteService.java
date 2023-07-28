package me.datafox.noterganizer.server.service;

import me.datafox.noterganizer.api.dto.NoteChangeDto;
import me.datafox.noterganizer.api.dto.NoteCreateDto;
import me.datafox.noterganizer.api.dto.NoteMoveDto;
import me.datafox.noterganizer.server.model.Note;

import java.security.Principal;

/**
 * The note service contains methods for creating, modifying and removing notes.
 *
 * @author datafox
 */
public interface NoteService {
    /**
     * @param title title for the note
     * @param principal principal for the creating user
     * @return the newly created note
     */
    Note createRootNote(String title, Principal principal);

    /**
     * @param dto DTO for note creation
     * @param principal principal for the creating user
     * @return the newly created note
     */
    Note createChildNote(NoteCreateDto dto, Principal principal);

    /**
     * @param dto DTO for note modification
     * @param principal principal for the modifying user
     */
    void changeNote(NoteChangeDto dto, Principal principal);

    /**
     * @param dto DTO for note moving
     * @param principal principal for the moving user
     */
    void moveNote(NoteMoveDto dto, Principal principal);

    /**
     * @param uuid UUID of the note to be removed
     * @param removeChildren if set to true, removes all child notes recursively, otherwise
     *                       moves orphaned child notes onto the removed note's parent
     * @param calledFromSpace should only be true if the note is a root note and its containing
     *                        space is removed at this time
     * @param principal principal for the removing user
     */
    void removeNote(String uuid, boolean removeChildren, boolean calledFromSpace, Principal principal);
}
