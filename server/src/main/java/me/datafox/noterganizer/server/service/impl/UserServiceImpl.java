package me.datafox.noterganizer.server.service.impl;

import me.datafox.noterganizer.api.dto.UserChangeDto;
import me.datafox.noterganizer.api.dto.UserDto;
import me.datafox.noterganizer.api.dto.UserRegisterDto;
import me.datafox.noterganizer.server.exception.AuthorizationException;
import me.datafox.noterganizer.server.exception.ForbiddenActionException;
import me.datafox.noterganizer.server.exception.PasswordTooShortException;
import me.datafox.noterganizer.server.exception.UsernameTakenException;
import me.datafox.noterganizer.server.model.AppUser;
import me.datafox.noterganizer.server.repository.UserRepository;
import me.datafox.noterganizer.server.service.MappingService;
import me.datafox.noterganizer.server.service.UserService;
import me.datafox.noterganizer.server.service.UuidService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * User service implementation.
 *
 * @author datafox
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private Logger logger;

    @Autowired
    private UuidService uuidService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser getUserByPrincipal(Principal principal) {
        if(principal == null) {
            throw new ForbiddenActionException();
        }

        logger.info("Fetching user " + principal.getName());

        //Should never be empty
        return userRepository
                .findByUsername(principal.getName())
                .orElseThrow();
    }

    @Override
    public UserDto getUserDto(Principal principal) {
        AppUser user = getUserByPrincipal(principal);

        return mappingService.mapToUserDto(user);
    }

    @Override
    public void createUser(UserRegisterDto dto) {
        logger.info("Registering user " + dto.getUsername());

        if(userRepository.existsByUsername(dto.getUsername())) {
            logger.warn("Attempted to register user with already existing username " +
                    dto.getUsername() + ", operation unsuccessful");

            throw new UsernameTakenException();
        }

        if(dto.getPassword().length() < 8) {
            logger.warn("Attempted to register user with too short password, operation unsuccessful");

            throw new PasswordTooShortException();
        }

        AppUser user = AppUser
                .builder()
                .uuid(uuidService.createUniqueUuid(userRepository))
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Override
    public void changeUser(UserChangeDto dto, Principal principal) {
        logger.info("Changing password for user " + principal.getName());

        AppUser user = getUserByPrincipal(principal);

        if(!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            logger.warn("User provided wrong old password, operation unsuccessful");
            throw new AuthorizationException();
        }

        if(dto.getNewPassword().length() < 8) {
            logger.warn("User provided too short new password, operation unsuccessful");
            throw new PasswordTooShortException();
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);
    }
}
