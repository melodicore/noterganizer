package me.datafox.noterganizer.server.service;

import me.datafox.noterganizer.api.dto.UserChangeDto;
import me.datafox.noterganizer.api.dto.UserDto;
import me.datafox.noterganizer.api.dto.UserRegisterDto;
import me.datafox.noterganizer.server.model.AppUser;

import java.security.Principal;

/**
 * The user service contains functions for fetching, creating and changing users.
 *
 * @author datafox
 */
public interface UserService {
    /**
     * @param principal principal for the user to be fetched
     * @return fetched user data
     */
    AppUser getUserByPrincipal(Principal principal);

    /**
     * @param principal principal for the user to be fetched
     * @return DTO object for fetched user
     */
    UserDto getUserDto(Principal principal);

    /**
     * @param dto DTO for user registration
     */
    void createUser(UserRegisterDto dto);

    /**
     * @param dto DTO for user modification
     * @param principal principal for the modifying user
     */
    void changeUser(UserChangeDto dto, Principal principal);
}
