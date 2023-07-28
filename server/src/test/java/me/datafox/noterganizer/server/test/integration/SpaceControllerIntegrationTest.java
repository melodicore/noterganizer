package me.datafox.noterganizer.server.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.datafox.noterganizer.api.dto.SpaceCreateDto;
import me.datafox.noterganizer.api.dto.SpaceDto;
import me.datafox.noterganizer.server.model.Space;
import me.datafox.noterganizer.server.repository.NoteRepository;
import me.datafox.noterganizer.server.repository.SpaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author datafox
 */
public class SpaceControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Test
    @WithMockUser
    public void getSpace_valid() throws Exception {
        String result = performGetSpace(SPACE_UUID)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SpaceDto space = objectMapper.readValue(result, SpaceDto.class);

        assertEquals(SPACE_UUID, space.getUuid());
        assertEquals(ROOT_UUID, space.getRoot().getUuid());
    }

    @Test
    @WithMockUser(DIFFERENT_USER_NAME)
    public void getSpace_userFail() throws Exception {
        performGetSpace(SPACE_UUID)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void createSpace_valid() throws Exception {
        String result = performCreateSpace("new space")
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Space space = assertSpaceExistsAndGet(result);

        assertEquals("new space", space.getRoot().getTitle());
    }

    @Test
    @WithMockUser
    public void removeSpace_valid() throws Exception {
        performRemoveSpace(SPACE_UUID)
                .andExpect(status().isOk());

        assertSpaceDoesNotExist(SPACE_UUID);
        assertNoteDoesNotExist(ROOT_UUID);
        assertNoteDoesNotExist(SUB_CHILD_UUID);
    }

    @Test
    @WithMockUser(DIFFERENT_USER_NAME)
    public void removeSpace_userFail() throws Exception {
        performRemoveSpace(SPACE_UUID)
                .andExpect(status().isForbidden());

        assertSpaceExistsAndGet(SPACE_UUID);
    }

    private ResultActions performGetSpace(String spaceUuid) throws Exception {
        return mvc.perform(get("/space/get?uuid=" + spaceUuid));
    }

    private ResultActions performCreateSpace(String spaceName) throws Exception {
        return mvc.perform(
                post("/space/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SpaceCreateDto
                                .builder()
                                .name(spaceName)
                                .build())));
    }

    private ResultActions performRemoveSpace(String spaceUuid) throws Exception {
        return mvc.perform(delete("/space/remove?uuid=" + spaceUuid));
    }

    private Space assertSpaceExistsAndGet(String spaceUuid) {
        Optional<Space> space = spaceRepository.findById(spaceUuid);
        assertTrue(space.isPresent());
        return space.get();
    }

    private void assertSpaceDoesNotExist(String spaceUuid) {
        assertTrue(spaceRepository.findById(spaceUuid).isEmpty());
    }

    private void assertNoteDoesNotExist(String noteUuid) {
        assertTrue(noteRepository.findById(noteUuid).isEmpty());
    }
}
