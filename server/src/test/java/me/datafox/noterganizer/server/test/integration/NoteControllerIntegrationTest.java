package me.datafox.noterganizer.server.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.datafox.noterganizer.api.dto.NoteChangeDto;
import me.datafox.noterganizer.api.dto.NoteCreateDto;
import me.datafox.noterganizer.api.dto.NoteHeaderDto;
import me.datafox.noterganizer.api.dto.NoteMoveDto;
import me.datafox.noterganizer.server.model.Note;
import me.datafox.noterganizer.server.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author datafox
 */
public class NoteControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    @WithMockUser
    public void createNote_valid() throws Exception {
        String result = performCreateNote("created child", PARENT_2_UUID)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Note note = assertNoteExistsAndGet(result);

        Note parent = assertNoteExistsAndGet(PARENT_2_UUID);

        assertEquals(1, parent.getChildren().size());

        assertTrue(parent.getChildren().contains(note));
    }

    @Test
    @WithMockUser
    public void createNote_unknownParentFail() throws Exception {
        performCreateNote("unknown child", "unknown-uuid")
                .andExpect(status().isNotFound());

        assertNoteDoesNotExist("unknown-uuid");
    }

    @Test
    @WithMockUser(DIFFERENT_USER_NAME)
    public void createNote_userFail() throws Exception {
        performCreateNote("created child", PARENT_2_UUID)
                .andExpect(status().isForbidden());

        Note parent = assertNoteExistsAndGet(PARENT_2_UUID);

        assertTrue(parent.getChildren().isEmpty());
    }

    @Test
    @WithMockUser
    public void changeNote_valid() throws Exception {
        performChangeNote(CHILD_UUID, "new content")
                .andExpect(status().isOk());

        Note note = assertNoteExistsAndGet(CHILD_UUID);

        assertEquals("new content", note.getContent());
    }

    @Test
    @WithMockUser(DIFFERENT_USER_NAME)
    public void changeNote_userFail() throws Exception {
        performChangeNote(CHILD_UUID, "new content")
                .andExpect(status().isForbidden());

        Note note = assertNoteExistsAndGet(CHILD_UUID);

        assertNotEquals("new content", note.getContent());
    }

    @Test
    @WithMockUser
    public void moveNote_valid() throws Exception {
        performMoveNote(CHILD_UUID, PARENT_2_UUID)
                .andExpect(status().isOk());

        Note parent1 = assertNoteExistsAndGet(PARENT_1_UUID);
        Note parent2 = assertNoteExistsAndGet(PARENT_2_UUID);

        assertTrue(parent1.getChildren().isEmpty());

        assertEquals(1, parent2.getChildren().size());
        assertEquals(CHILD_UUID, parent2.getChildren().get(0).getUuid());
    }

    @Test
    @WithMockUser
    public void moveNote_rootFail() throws Exception {
        performMoveNote(ROOT_UUID, PARENT_2_UUID)
                .andExpect(status().isBadRequest());

        Note parent = assertNoteExistsAndGet(PARENT_2_UUID);

        assertTrue(parent.getChildren().isEmpty());
    }

    @Test
    @WithMockUser
    public void moveNote_cyclicFail() throws Exception {
        performMoveNote(PARENT_1_UUID, SUB_CHILD_UUID)
                .andExpect(status().isBadRequest());

        Note parent = assertNoteExistsAndGet(SUB_CHILD_UUID);

        assertTrue(parent.getChildren().isEmpty());
    }

    @Test
    @WithMockUser(DIFFERENT_USER_NAME)
    public void moveNote_userFail() throws Exception {
        performMoveNote(CHILD_UUID, PARENT_2_UUID)
                .andExpect(status().isForbidden());

        Note parent = assertNoteExistsAndGet(PARENT_2_UUID);

        assertTrue(parent.getChildren().isEmpty());
    }

    @Test
    @WithMockUser
    public void removeNote_validWithChildren() throws Exception {
        performRemoveNote(PARENT_1_UUID, true)
                .andExpect(status().isOk());

        assertNoteDoesNotExist(PARENT_1_UUID);

        assertNoteDoesNotExist(SUB_CHILD_UUID);
    }

    @Test
    @WithMockUser
    public void removeNote_validWithoutChildren() throws Exception {
        performRemoveNote(PARENT_1_UUID, false)
                .andExpect(status().isOk());

        assertNoteDoesNotExist(PARENT_1_UUID);

        Note root = assertNoteExistsAndGet(ROOT_UUID);

        assertEquals(2, root.getChildren().size());

        assertEquals(CHILD_UUID, root.getChildren().get(1).getUuid());
    }

    @Test
    @WithMockUser
    public void removeNote_rootFail() throws Exception {
        performRemoveNote(ROOT_UUID, true)
                .andExpect(status().isBadRequest());

        assertNoteExistsAndGet(ROOT_UUID);
    }

    @Test
    @WithMockUser(DIFFERENT_USER_NAME)
    public void removeNote_userFail() throws Exception {
        performRemoveNote(PARENT_1_UUID, true)
                .andExpect(status().isForbidden());

        Note note = assertNoteExistsAndGet(PARENT_1_UUID);

        assertEquals(1, note.getChildren().size());
    }

    private ResultActions performCreateNote(String noteTitle, String parentUuid) throws Exception {
        return mvc.perform(
                post("/note/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(NoteCreateDto
                                .builder()
                                .title(noteTitle)
                                .parent(NoteHeaderDto
                                        .builder()
                                        .uuid(parentUuid)
                                        .build())
                                .build())));
    }

    private ResultActions performChangeNote(String noteUuid, String newContent) throws Exception {
        return mvc.perform(
                post("/note/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(NoteChangeDto
                                .builder()
                                .uuid(noteUuid)
                                .content(newContent)
                                .build())));
    }

    private ResultActions performMoveNote(String noteUuid, String parentUuid) throws Exception {
        return mvc.perform(
                post("/note/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(NoteMoveDto
                                .builder()
                                .uuid(noteUuid)
                                .parent(NoteHeaderDto
                                        .builder()
                                        .uuid(parentUuid)
                                        .build())
                                .build())));
    }

    private ResultActions performRemoveNote(String noteUuid, boolean removeChildren) throws Exception {
        return mvc.perform(
                delete("/note/remove" +
                        "?uuid=" + noteUuid +
                        "&removeChildren=" + removeChildren));
    }

    private Note assertNoteExistsAndGet(String noteUuid) {
        Optional<Note> note = noteRepository.findById(noteUuid);
        assertTrue(note.isPresent());
        return note.get();
    }

    private void assertNoteDoesNotExist(String noteUuid) {
        assertTrue(noteRepository.findById(noteUuid).isEmpty());
    }
}
