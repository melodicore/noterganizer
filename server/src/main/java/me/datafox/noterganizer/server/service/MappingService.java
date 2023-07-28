package me.datafox.noterganizer.server.service;

import me.datafox.noterganizer.api.dto.NoteHeaderDto;
import me.datafox.noterganizer.api.dto.SpaceDto;
import me.datafox.noterganizer.api.dto.UserDto;
import me.datafox.noterganizer.server.model.AppUser;
import me.datafox.noterganizer.server.model.Note;
import me.datafox.noterganizer.server.model.Space;

/**
 * The mapping service contains helper methods for mapping internal objects
 * into DTO objects.
 *
 * @author datafox
 */
public interface MappingService {
    /**
     * @param user user
     * @return DTO object for given user
     */
    UserDto mapToUserDto(AppUser user);

    /**
     * @param space space
     * @return DTO object for given space
     */
    SpaceDto mapToSpaceDto(Space space);

    /**
     * @param note note
     * @return header DTO object for given note
     */
    NoteHeaderDto mapToNoteHeaderDto(Note note);
}
