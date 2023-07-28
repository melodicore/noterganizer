package me.datafox.noterganizer.client.service;

import me.datafox.noterganizer.api.dto.*;

import java.util.Optional;

/**
 * The REST service contains higher-level helper methods for accessing the
 * {@link ConnectionService}. It also displays a popup with an error message
 * if any operation was not successful.
 *
 * @author datafox
 */
public interface RestService {
    /**
     * @param address address to connect to
     * @return response status if successful or empty otherwise
     */
    Optional<String> connect(String address);

    /**
     * @param username username
     * @param password password
     * @param remember if true, a remember me HTTP cookie will be obtained
     * @return response status if successful or empty otherwise
     */
    Optional<String> login(String username, String password, boolean remember);

    /**
     * @return user DTO object if successful or empty otherwise
     */
    Optional<UserDto> getUser();

    /**
     * @param dto user registration DTO object
     * @return response status if successful or empty otherwise
     */
    Optional<String> register(UserRegisterDto dto);

    /**
     * @param dto user modification DTO object
     * @return response status if successful or empty otherwise
     */
    Optional<String> changeUser(UserChangeDto dto);

    /**
     * @param uuid UUID of space to be fetched
     * @return space DTO object if successful or empty otherwise
     */
    Optional<SpaceDto> getSpace(String uuid);

    /**
     * @param dto space creation DTO object
     * @return response status if successful or empty otherwise
     */
    Optional<String> createSpace(SpaceCreateDto dto);

    /**
     * @param uuid UUID of space to be removed
     * @return response status if successful or empty otherwise
     */
    Optional<String> removeSpace(String uuid);

    /**
     * @param dto note creation DTO object
     * @return response status if successful or empty otherwise
     */
    Optional<String> createNote(NoteCreateDto dto);

    /**
     * @param dto note modification DTO object
     * @return response status if successful or empty otherwise
     */
    Optional<String> changeNote(NoteChangeDto dto);

    /**
     * @param dto note move DTO object
     * @return response status if successful or empty otherwise
     */
    Optional<String> moveNote(NoteMoveDto dto);

    /**
     * @param uuid UUID of note to be removed
     * @param removeChildren if set to true, removes all child notes recursively, otherwise
     *                       moves orphaned child notes onto the removed note's parent
     * @return response status if successful or empty otherwise
     */
    Optional<String> removeNote(String uuid, boolean removeChildren);
}
