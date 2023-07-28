package me.datafox.noterganizer.server.service.impl;

import me.datafox.noterganizer.api.dto.*;
import me.datafox.noterganizer.server.exception.ForbiddenActionException;
import me.datafox.noterganizer.server.exception.SpaceNotFoundException;
import me.datafox.noterganizer.server.model.Space;
import me.datafox.noterganizer.server.repository.SpaceRepository;
import me.datafox.noterganizer.server.service.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Space service implementation.
 *
 * @author datafox
 */
@Service
public class SpaceServiceImpl implements SpaceService {
    @Autowired
    private Logger logger;

    @Autowired
    private UuidService uuidService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private SpaceRepository spaceRepository;

    @Override
    public SpaceDto getSpaceDto(String uuid, Principal principal) {
        logger.info("Fetching space with UUID " + uuid);

        Space space = getSpaceAndCheckPrincipal(uuid, principal);

        return mappingService.mapToSpaceDto(space);
    }

    @Override
    public String createSpace(SpaceCreateDto dto, Principal principal) {
        logger.info("Creating space with name " + dto.getName());

        Space space = Space
                .builder()
                .uuid(uuidService.createUniqueUuid(spaceRepository))
                .user(userService.getUserByPrincipal(principal))
                .root(noteService.createRootNote(dto.getName(), principal))
                .build();

        spaceRepository.save(space);

        return space.getUuid();
    }

    @Override
    public void removeSpace(String uuid, Principal principal) {
        logger.info("Removing space with UUID " + uuid);

        Space space = getSpaceAndCheckPrincipal(uuid, principal);

        noteService.removeNote(space.getRoot().getUuid(), true, true, principal);

        spaceRepository.delete(space);
    }

    private Space getSpaceAndCheckPrincipal(String uuid, Principal principal) {
        Space space = spaceRepository
                .findById(uuid)
                .orElseThrow(SpaceNotFoundException::new);

        if(!space.getUser()
                .getUsername()
                .equals(principal.getName())) {

            logger.warn("User " + principal.getName() + " attempted to access a space belonging to user " +
                    space.getUser().getUsername() + ", operation unsuccessful");

            throw new ForbiddenActionException();
        }

        return space;
    }
}
