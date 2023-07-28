package me.datafox.noterganizer.server.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.datafox.noterganizer.api.dto.UserChangeDto;
import me.datafox.noterganizer.api.dto.UserDto;
import me.datafox.noterganizer.api.dto.UserRegisterDto;
import me.datafox.noterganizer.server.model.AppUser;
import me.datafox.noterganizer.server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author datafox
 */
public class UserControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser
    public void getUser_valid() throws Exception {
        String result = mvc.perform(
                get("/user"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserDto user = objectMapper.readValue(result, UserDto.class);

        assertEquals(1, user.getSpaces().size());

        assertEquals(SPACE_UUID, user.getSpaces().get(0).getUuid());
    }

    @Test
    public void registerUser_valid() throws Exception {
        performRegisterUser(NON_EXISTENT_USER_NAME, PASSWORD_RAW)
                .andExpect(status().isOk());

        AppUser user = assertUserExistsAndGet(DIFFERENT_USER_NAME);

        assertTrue(passwordEncoder.matches(PASSWORD_RAW, user.getPassword()));
    }

    @Test
    public void registerUser_usernameFail() throws Exception {
        performRegisterUser(USER_NAME, PASSWORD_RAW)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    public void changeUser_valid() throws Exception{
        performChangeUser(PASSWORD_RAW, "newPassword")
                .andExpect(status().isOk());

        AppUser user = assertUserExistsAndGet(USER_NAME);

        assertTrue(passwordEncoder.matches("newPassword", user.getPassword()));
    }

    @Test
    @WithMockUser
    public void changeUser_oldPasswordFail() throws Exception{
        performChangeUser("wrongPassword", "newPassword")
                .andExpect(status().isUnauthorized());

        AppUser user = assertUserExistsAndGet(USER_NAME);

        assertTrue(passwordEncoder.matches(PASSWORD_RAW, user.getPassword()));
    }

    @Test
    @WithMockUser
    public void changeUser_newPasswordFail() throws Exception{
        performChangeUser(PASSWORD_RAW, "short")
                .andExpect(status().isBadRequest());

        AppUser user = assertUserExistsAndGet(USER_NAME);

        assertTrue(passwordEncoder.matches(PASSWORD_RAW, user.getPassword()));
    }

    private ResultActions performRegisterUser(String username, String password) throws Exception {
        return mvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserRegisterDto
                                .builder()
                                .username(username)
                                .password(password)
                                .build())));
    }

    private ResultActions performChangeUser(String oldPassword, String newPassword) throws Exception {
        return mvc.perform(
                post("/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserChangeDto
                                .builder()
                                .oldPassword(oldPassword)
                                .newPassword(newPassword)
                                .build())));
    }

    private AppUser assertUserExistsAndGet(String username) {
        Optional<AppUser> user = userRepository.findByUsername(username);
        assertTrue(user.isPresent());
        return user.get();
    }
}
