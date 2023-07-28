package me.datafox.noterganizer.server.controller;

import me.datafox.noterganizer.api.dto.NoteChangeDto;
import me.datafox.noterganizer.api.dto.NoteCreateDto;
import me.datafox.noterganizer.api.dto.NoteMoveDto;
import me.datafox.noterganizer.server.model.Note;
import me.datafox.noterganizer.server.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for all note-related API calls.
 *
 * @author datafox
 */
@RestController
public class NoteController {
    @Autowired
    private NoteService noteService;

    @PostMapping("/note/create")
    public ResponseEntity<String> createNote(@RequestBody NoteCreateDto dto,
                                              Principal principal) {

        Note note = noteService.createChildNote(dto, principal);

        return ResponseEntity.ok(note.getUuid());
    }

    @PostMapping("/note/change")
    public ResponseEntity<String> changeNote(@RequestBody NoteChangeDto dto,
                                             Principal principal) {

        noteService.changeNote(dto, principal);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/note/move")
    public ResponseEntity<String> moveNote(@RequestBody NoteMoveDto dto,
                                           Principal principal) {

        noteService.moveNote(dto, principal);

        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/note/remove")
    public ResponseEntity<String> removeSpace(@RequestParam String uuid,
                                              @RequestParam boolean removeChildren,
                                              Principal principal) {

        noteService.removeNote(uuid, removeChildren, false, principal);

        return ResponseEntity.ok("success");
    }
}
