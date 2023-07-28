package me.datafox.noterganizer.client.controller;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import me.datafox.noterganizer.api.dto.UserRegisterDto;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.service.PopupService;
import me.datafox.noterganizer.client.service.RestService;
import me.datafox.noterganizer.client.service.UiService;
import me.datafox.noterganizer.client.service.ValidationService;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * JavaFx controller for the registration view.
 *
 * @author datafox
 */
public class RegisterController {
    private final Logger logger;

    private final PopupService popupService;

    private final RestService restService;

    private final UiService uiService;

    private final ValidationService validationService;

    @FXML
    private MenuItem exit;

    @FXML
    private MenuItem about;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField passwordRepeat;

    @FXML
    private Button register;

    @FXML
    private Button back;

    @Inject
    public RegisterController(Logger logger,
                              PopupService popupService,
                              RestService restService,
                              UiService uiService,
                              ValidationService validationService) {
        this.logger = logger;
        this.popupService = popupService;
        this.restService = restService;
        this.uiService = uiService;
        this.validationService = validationService;
    }

    @FXML
    private void initialize() {
        logger.info("Setting up UI elements for registration view");

        //Menu button actions
        exit.setOnAction(event -> Platform.exit());
        about.setOnAction(event -> popupService.showAboutPopup());

        //Text field and button actions
        username.setOnAction(event -> password.requestFocus());
        password.setOnAction(event -> passwordRepeat.requestFocus());
        passwordRepeat.setOnAction(this::register);
        register.setOnAction(this::register);
        back.setOnAction(this::back);

        //Validation pseudo class listeners
        username.textProperty().addListener((observable, oldValue, newValue) ->
                username.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                        !validationService.validateUsername(newValue)));

        password.textProperty().addListener((observable, oldValue, newValue) ->
                password.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                        !validationService.validatePassword(newValue)));

        passwordRepeat.textProperty().addListener((observable, oldValue, newValue) ->
                passwordRepeat.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                        !validationService.validatePasswordRepeat(password.getText(), newValue)));

        //Run focusing after initialisation
        Platform.runLater(() -> {
            logger.info("Requesting focus for username text field");
            username.requestFocus();
        });
    }

    /**
     * Validate fields and send a user registration request, and if successful switch to the login view.
     */
    private void register(ActionEvent ignored) {
        //If fields are not valid, show a popup and return
        if(!validationService.validateUsername(username.getText())) {
            logger.info("Username validation failed");

            popupService.showInfoPopup("Registration error",
                    "Username must not be empty");
            return;
        }

        if(!validationService.validatePassword(password.getText())) {
            logger.info("Password validation failed");

            popupService.showInfoPopup("Registration error",
                    "Password must be at least 8 characters long");
            return;
        }

        if(!validationService.validatePasswordRepeat(password.getText(), passwordRepeat.getText())) {
            logger.info("Password repeat validation failed");

            popupService.showInfoPopup("Registration error",
                    "Repeated password does not match the original");
            return;
        }

        //Build user registration dto object
        UserRegisterDto dto = UserRegisterDto
                .builder()
                .username(username.getText())
                .password(password.getText())
                .build();

        Optional<String> optional = restService.register(dto);

        if(optional.isEmpty()) return;

        uiService.setScene("login");
    }

    /**
     * Switch to the login view.
     */
    private void back(ActionEvent ignored) {
        uiService.setScene("login");
    }
}
