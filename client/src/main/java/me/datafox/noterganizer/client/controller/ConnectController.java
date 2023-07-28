package me.datafox.noterganizer.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.service.PopupService;
import me.datafox.noterganizer.client.service.RestService;
import me.datafox.noterganizer.client.service.SettingsService;
import me.datafox.noterganizer.client.service.UiService;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * JavaFx controller for the server connection view.
 *
 * @author datafox
 */
public class ConnectController {
    private final Logger logger;

    private final PopupService popupService;

    private final RestService restService;

    private final SettingsService settingsService;

    private final UiService uiService;

    @FXML
    private MenuItem exit;

    @FXML
    private MenuItem about;

    @FXML
    private TextField server;

    @FXML
    private CheckBox autoConnect;

    @FXML
    private Button connect;

    @Inject
    public ConnectController(Logger logger,
                             PopupService popupService,
                             RestService restService,
                             SettingsService settingsService,
                             UiService uiService) {
        this.logger = logger;
        this.popupService = popupService;
        this.restService = restService;
        this.settingsService = settingsService;
        this.uiService = uiService;
    }

    @FXML
    private void initialize() {
        logger.info("Setting up UI elements for connection view");

        //Menu button actions
        exit.setOnAction(event -> Platform.exit());
        about.setOnAction(event -> popupService.showAboutPopup());

        //Text field and button actions
        server.setOnAction(this::connect);
        connect.setOnAction(this::connect);

        //Get cached server address from settings
        server.setText(settingsService.getAddress());

        //Select the auto-connect check box according to settings
        autoConnect.setSelected(settingsService.getAutoConnect());

        //Run focusing and auto-connect actions after initialisation
        Platform.runLater(() -> {
            logger.info("Requesting focus for server text field");
            server.requestFocus();

            if(settingsService.getAutoConnect() && !server.getText().isBlank()) {
                logger.info("Auto-connect turned on, attempting to connect");
                connect(null);
            }
        });
    }

    /**
     * Send connect request to server and if successful, change auto-connect settings
     * and switch to the login view.
     */
    private void connect(ActionEvent ignored) {
        Optional<String> optional = restService.connect(server.getText());

        if(optional.isEmpty()) return;

        settingsService.setAddress(server.getText());
        settingsService.setAutoConnect(autoConnect.isSelected());

        uiService.setScene("login");
    }
}
