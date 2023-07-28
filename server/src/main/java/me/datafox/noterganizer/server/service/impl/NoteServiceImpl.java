package me.datafox.noterganizer.server.service.impl;

import me.datafox.noterganizer.api.dto.NoteChangeDto;
import me.datafox.noterganizer.api.dto.NoteCreateDto;
import me.datafox.noterganizer.api.dto.NoteMoveDto;
import me.datafox.noterganizer.server.exception.*;
import me.datafox.noterganizer.server.model.Note;
import me.datafox.noterganizer.server.repository.NoteRepository;
import me.datafox.noterganizer.server.service.NoteService;
import me.datafox.noterganizer.server.service.UserService;
import me.datafox.noterganizer.server.service.UuidService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Note service implementation.
 *
 * @author datafox
 */
@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    private Logger logger;

    @Autowired
    private UuidService uuidService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public Note createRootNote(String title, Principal principal) {
        logger.info("Creating root note with title " + title);

        Note note = Note
                .builder()
                .uuid(uuidService.createUniqueUuid(noteRepository))
                .user(userService.getUserByPrincipal(principal))
                .title(title)
                .build();

        noteRepository.save(note);

        return note;
    }

    @Override
    public Note createChildNote(NoteCreateDto dto, Principal principal) {
        logger.info("Creating child note with title " + dto.getTitle() + " to parent with UUID " + dto.getParent().getUuid());

        Note parent = getNoteAndCheckPrincipal(dto.getParent().getUuid(), principal);

        Note note = Note
                .builder()
                .uuid(uuidService.createUniqueUuid(noteRepository))
                .user(userService.getUserByPrincipal(principal))
                .title(dto.getTitle())
                .build();

        parent.getChildren().add(note);

        noteRepository.saveAll(List.of(note, parent));

        return note;
    }

    @Override
    public void changeNote(NoteChangeDto dto, Principal principal) {
        logger.info("Changing content of note with UUID " + dto.getUuid());

        logger.debug("New title:   " + dto.getTitle());
        logger.debug("New content: " + dto.getContent());

        Note note = getNoteAndCheckPrincipal(dto.getUuid(), principal);

        note.setTitle(dto.getTitle());

        note.setContent(dto.getContent());

        noteRepository.save(note);
    }

    @Override
    public void moveNote(NoteMoveDto dto, Principal principal) {
        logger.info("Moving note with UUID " + dto.getUuid() + " to new parent with UUID " + dto.getParent().getUuid());

        Note note = getNoteAndCheckPrincipal(dto.getUuid(), principal);

        Optional<Note> optional = noteRepository.findByChildren(note);

        if(optional.isEmpty()) {
            logger.warn("The moved note is a root note, operation unsuccessful");
            throw new MoveRootNoteException();
        }

        Note oldParent = optional.get();

        Note newParent = getNoteAndCheckPrincipal(dto.getParent().getUuid(), principal);

        logger.debug("Recursively checking cyclic notes for note with UUID " + newParent);
        if(checkCyclicNotes(newParent, note)) {
            logger.warn("Moving the note would cause a cyclic dependency, operation unsuccessful");
            throw new CyclicNoteException();
        }

        if(!oldParent.equals(newParent)) {
            oldParent.getChildren().remove(note);

            newParent.getChildren().add(note);

            noteRepository.saveAll(List.of(oldParent, newParent));
        }
    }

    @Override
    public void removeNote(String uuid, boolean removeChildren, boolean calledFromSpace, Principal principal) {
        logger.info("Removing note with UUID " + uuid +
                (removeChildren ? " and all its children" : " and moving its children to its parent"));

        Note note = getNoteAndCheckPrincipal(uuid, principal);

        Optional<Note> parent = noteRepository.findByChildren(note);

        if(!calledFromSpace && parent.isEmpty()) {
            logger.warn("The note is a root note and the request was not made by removing a space, operation unsuccessful");
            throw new RemoveRootNoteException();
        }

        parent.ifPresent(value -> value.getChildren().remove(note));

        if(removeChildren || parent.isEmpty()) {
            recursiveRemoveNote(note);
        } else {
            removeNoteAndMoveChildren(note, parent.get());
        }
    }

    private void recursiveRemoveNote(Note note) {
        logger.debug("Recursively removing note with UUID " + note.getUuid());

        note.getChildren().forEach(this::recursiveRemoveNote);

        noteRepository.delete(note);
    }

    private void removeNoteAndMoveChildren(Note note, Note parent) {
        parent.getChildren().addAll(note.getChildren());

        noteRepository.save(parent);

        noteRepository.delete(note);
    }

    private Note getNoteAndCheckPrincipal(String uuid, Principal principal) {
        Note note = noteRepository
                .findById(uuid)
                .orElseThrow(NoteNotFoundException::new);

        if(!note.getUser()
                .getUsername()
                .equals(principal.getName())) {

            logger.warn("User " + principal.getName() + " attempted to access a note belonging to user " +
                    note.getUser().getUsername() + ", operation unsuccessful");

            throw new ForbiddenActionException();
        }

        return note;
    }

    private boolean checkCyclicNotes(Note checked, Note current) {
        logger.debug("Currently at note with UUID " + current.getUuid());

        if(current.getChildren().contains(checked)) return true;

        return current.getChildren().stream().anyMatch(note -> checkCyclicNotes(checked, note));
    }
}
