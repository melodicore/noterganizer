package me.datafox.noterganizer.server.test.integration;

import me.datafox.noterganizer.server.NoterganizerServer;
import me.datafox.noterganizer.server.model.AppUser;
import me.datafox.noterganizer.server.model.Note;
import me.datafox.noterganizer.server.model.Space;
import me.datafox.noterganizer.server.repository.NoteRepository;
import me.datafox.noterganizer.server.repository.SpaceRepository;
import me.datafox.noterganizer.server.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author datafox
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = NoterganizerServer.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public abstract class AbstractIntegrationTest {
    protected static final String USER_UUID = "fake-user-uuid";
    protected static final String USER_NAME = "user";
    protected static final String PASSWORD_RAW = "password";

    protected static final String DIFFERENT_USER_UUID = "fake-different-user-uuid";
    protected static final String DIFFERENT_USER_NAME = "different-user";

    protected static final String NON_EXISTENT_USER_NAME = "non-existent-user";

    protected static final String SUB_CHILD_UUID = "fake-sub-child-uuid";
    protected static final String SUB_CHILD_TITLE = "sub child";
    protected static final String SUB_CHILD_CONTENT = "sub child content";

    protected static final String CHILD_UUID = "fake-child-uuid";
    protected static final String CHILD_TITLE = "child";
    protected static final String CHILD_CONTENT = "child content";

    protected static final String PARENT_1_UUID = "fake-parent-1-uuid";
    protected static final String PARENT_1_TITLE = "parent 1";
    protected static final String PARENT_1_CONTENT = "parent 1 content";

    protected static final String PARENT_2_UUID = "fake-parent-2-uuid";
    protected static final String PARENT_2_TITLE = "parent 2";
    protected static final String PARENT_2_CONTENT = "parent 2 content";

    protected static final String ROOT_UUID = "fake-root-uuid";
    protected static final String ROOT_TITLE = "root";
    protected static final String ROOT_CONTENT = "root content";

    protected static final String SPACE_UUID = "fake-space-uuid";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        AppUser user = AppUser
                .builder()
                .uuid(USER_UUID)
                .username(USER_NAME)
                .password(passwordEncoder.encode(PASSWORD_RAW))
                .build();
        AppUser differentUser = AppUser
                .builder()
                .uuid(DIFFERENT_USER_UUID)
                .username(DIFFERENT_USER_NAME)
                .password(passwordEncoder.encode(PASSWORD_RAW))
                .build();
        Note subChild = Note
                .builder()
                .uuid(SUB_CHILD_UUID)
                .user(user)
                .title(SUB_CHILD_TITLE)
                .content(SUB_CHILD_CONTENT)
                .build();
        Note child = Note
                .builder()
                .uuid(CHILD_UUID)
                .user(user)
                .title(CHILD_TITLE)
                .content(CHILD_CONTENT)
                .children(new ArrayList<>(List.of(subChild)))
                .build();
        Note parent1 = Note
                .builder()
                .uuid(PARENT_1_UUID)
                .user(user)
                .title(PARENT_1_TITLE)
                .content(PARENT_1_CONTENT)
                .children(new ArrayList<>(List.of(child)))
                .build();
        Note parent2 = Note
                .builder()
                .uuid(PARENT_2_UUID)
                .user(user)
                .title(PARENT_2_TITLE)
                .content(PARENT_2_CONTENT)
                .build();
        Note root = Note
                .builder()
                .uuid(ROOT_UUID)
                .user(user)
                .title(ROOT_TITLE)
                .content(ROOT_CONTENT)
                .children(new ArrayList<>(List.of(parent1, parent2)))
                .build();
        Space space = Space
                .builder()
                .uuid(SPACE_UUID)
                .user(user)
                .root(root)
                .build();
        userRepository.save(user);
        userRepository.save(differentUser);
        noteRepository.save(subChild);
        noteRepository.save(child);
        noteRepository.save(parent1);
        noteRepository.save(parent2);
        noteRepository.save(root);
        spaceRepository.save(space);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        noteRepository.deleteAll();
        spaceRepository.deleteAll();
    }
}
