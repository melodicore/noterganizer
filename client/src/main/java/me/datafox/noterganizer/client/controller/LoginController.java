package me.datafox.noterganizer.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import me.datafox.noterganizer.api.dto.UserDto;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.Context;
import me.datafox.noterganizer.client.service.*;
import org.apache.logging.log4j.Logger;

import java.net.HttpCookie;
import java.util.Optional;

/**
 * JavaFx controller for the login view.
 *
 * @author datafox
 */
public class LoginController {
    private final Context context;

    private final Logger logger;

    private final ConnectionService connectionService;

    private final MappingService mappingService;

    private final PopupService popupService;

    private final RestService restService;

    private final SettingsService settingsService;

    private final UiService uiService;

    @FXML
    private MenuItem exit;

    @FXML
    private MenuItem about;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private CheckBox remember;

    @FXML
    private Button login;

    @FXML
    private Button register;

    @Inject
    public LoginController(Context context,
                           Logger logger,
                           ConnectionService connectionService,
                           MappingService mappingService,
                           PopupService popupService,
                           RestService restService,
                           SettingsService settingsService,
                           UiService uiService) {
        this.context = context;
        this.logger = logger;
        this.connectionService = connectionService;
        this.mappingService = mappingService;
        this.popupService = popupService;
        this.restService = restService;
        this.settingsService = settingsService;
        this.uiService = uiService;
    }

    @FXML
    private void initialize() {
        logger.info("Setting up UI elements for login view");

        //Menu button actions
        exit.setOnAction(event -> Platform.exit());
        about.setOnAction(event -> popupService.showAboutPopup());

        //Text field and button actions
        username.setOnAction(event -> password.requestFocus());
        password.setOnAction(this::login);
        login.setOnAction(this::login);
        register.setOnAction(this::register);

        //Get remember me cookie from settings and select the remember me check box if the cookie is present
        HttpCookie rememberCookie = settingsService.getCookie();
        remember.setSelected(rememberCookie != null);

        //Run focusing and auto-login actions after initialisation
        Platform.runLater(() -> {
            logger.info("Requesting focus for username text field");
            username.requestFocus();

            if(rememberCookie != null) {
                logger.info("Auto-login turned on, attempting to login");
                connectionService.setRememberCookie(rememberCookie);
                loginUser();
            }
        });
    }

    /**
     * Send login request to server and if successful, call loginUser.
     */
    private void login(ActionEvent ignored) {
        //Try to log in
        Optional<String> optional = restService.login(
                username.getText(),
                password.getText(),
                remember.isSelected());

        //Return if unsuccessful
        if(optional.isEmpty()) return;

        loginUser();
    }

    /**
     * Send user details request to server and if successful, save remember me settings,
     * save user details to context and switch to the main view.
     */
    private void loginUser() {
        Optional<UserDto> optional = restService.getUser();

        if(optional.isEmpty()) return;

        //If remember me check box is selected, save remember me cookie, otherwise remove any present in settings
        if(remember.isSelected()) {
            Optional<HttpCookie> cookie = connectionService.getRememberCookie();
            cookie.ifPresent(settingsService::setCookie);
        } else {
            settingsService.removeCookie();
        }

        context.setUser(mappingService.mapToUser(optional.get()));

        uiService.setScene("main");
    }

    /**
     * Switch to the register view.
     */
    private void register(ActionEvent ignored) {
        uiService.setScene("register");
    }
}
