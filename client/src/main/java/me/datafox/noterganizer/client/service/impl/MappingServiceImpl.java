package me.datafox.noterganizer.client.service.impl;

import me.datafox.noterganizer.api.dto.*;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.model.Note;
import me.datafox.noterganizer.client.model.Space;
import me.datafox.noterganizer.client.model.SpaceHeader;
import me.datafox.noterganizer.client.model.User;
import me.datafox.noterganizer.client.service.MappingService;

/**
 * Mapping service implementation
 *
 * @author datafox
 */
@Component
public class MappingServiceImpl implements MappingService {
    @Override
    public User mapToUser(UserDto dto) {
        return User.builder()
                .username(dto.getUsername())
                .spaces(dto.getSpaces()
                        .stream()
                        .map(this::mapToSpaceHeader)
                        .toList())
                .build();
    }

    @Override
    public Space mapToSpace(SpaceDto dto) {
        return Space.builder()
                .uuid(dto.getUuid())
                .root(mapToNote(dto.getRoot(), null))
                .build();
    }

    @Override
    public NoteHeaderDto mapToNoteHeaderDto(Note note) {
        return NoteHeaderDto.builder()
                .uuid(note.getUuid())
                .title(note.getTitle())
                .build();
    }

    @Override
    public NoteChangeDto mapToNoteChangeDto(Note note) {
        return NoteChangeDto.builder()
                .uuid(note.getUuid())
                .title(note.getTitle())
                .content(note.getContent())
                .build();
    }

    @Override
    public NoteMoveDto mapToNoteMoveDto(Note note, Note newParent) {
        return NoteMoveDto.builder()
                .uuid(note.getUuid())
                .parent(mapToNoteHeaderDto(newParent))
                .build();
    }

    private SpaceHeader mapToSpaceHeader(SpaceHeaderDto dto) {
        return SpaceHeader.builder()
                .uuid(dto.getUuid())
                .name(dto.getRoot().getTitle())
                .build();
    }

    private Note mapToNote(NoteDto dto, Note parent) {
        Note note = Note.builder()
                .uuid(dto.getUuid())
                .title(dto.getTitle())
                .content(dto.getContent())
                .parent(parent)
                .build();

        note.getChildren().addAll(dto.getChildren().stream().map(child -> mapToNote(child, note)).toList());

        return note;
    }
}
