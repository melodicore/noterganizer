package me.datafox.noterganizer.server.service;

import me.datafox.noterganizer.api.dto.SpaceCreateDto;
import me.datafox.noterganizer.api.dto.SpaceDto;

import java.security.Principal;

/**
 * The space service contains methods for fetching, creating and removing spaces.
 *
 * @author datafox
 */
public interface SpaceService {
    /**
     * @param uuid UUID of the space to be fetched
     * @param principal principal for the fetching user
     * @return DTO object for fetched space
     */
    SpaceDto getSpaceDto(String uuid, Principal principal);

    /**
     * @param dto DTO for space creation
     * @param principal principal for the creating user
     * @return UUID of the newly created space
     */
    String createSpace(SpaceCreateDto dto, Principal principal);

    /**
     * @param uuid UUID of the space to be removed
     * @param principal principal for the removing user
     */
    void removeSpace(String uuid, Principal principal);
}
