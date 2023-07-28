package me.datafox.noterganizer.server.service.impl;

import me.datafox.noterganizer.api.dto.*;
import me.datafox.noterganizer.server.model.AppUser;
import me.datafox.noterganizer.server.model.Note;
import me.datafox.noterganizer.server.model.Space;
import me.datafox.noterganizer.server.repository.SpaceRepository;
import me.datafox.noterganizer.server.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Mapping service implementation.
 *
 * @author datafox
 */
@Service
public class MappingServiceImpl implements MappingService {
    @Autowired
    private SpaceRepository spaceRepository;

    @Override
    public UserDto mapToUserDto(AppUser user) {
        UserDto.UserDtoBuilder builder = UserDto
                .builder()
                .username(user.getUsername());

        spaceRepository
                .findByUser(user)
                .map(this::mapToSpaceHeaderDto)
                .forEach(builder::space);

        return builder.build();
    }

    @Override
    public SpaceDto mapToSpaceDto(Space space) {
        return SpaceDto
                .builder()
                .uuid(space.getUuid())
                .root(mapToNoteDto(space.getRoot()))
                .build();
    }

    @Override
    public NoteHeaderDto mapToNoteHeaderDto(Note note) {
        return NoteHeaderDto.builder()
                .uuid(note.getUuid())
                .title(note.getTitle())
                .build();
    }

    private SpaceHeaderDto mapToSpaceHeaderDto(Space space) {
        return SpaceHeaderDto
                .builder()
                .uuid(space.getUuid())
                .root(mapToNoteHeaderDto(space.getRoot()))
                .build();
    }

    private NoteDto mapToNoteDto(Note note) {
        NoteDto.NoteDtoBuilder builder = NoteDto
                .builder()
                .uuid(note.getUuid())
                .title(note.getTitle())
                .content(note.getContent());

        note.getChildren()
                .stream()
                .map(this::mapToNoteDto)
                .forEach(builder::child);

        return builder.build();
    }
}
