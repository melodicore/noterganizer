package me.datafox.noterganizer.client.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import me.datafox.noterganizer.api.dto.UserChangeDto;
import me.datafox.noterganizer.client.factory.NoteTreeCellFactory;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.Context;
import me.datafox.noterganizer.client.model.Note;
import me.datafox.noterganizer.client.model.SpaceHeader;
import me.datafox.noterganizer.client.model.UserSettings;
import me.datafox.noterganizer.client.service.*;
import me.datafox.noterganizer.client.ui.Editor;
import org.apache.logging.log4j.Logger;

/**
 * JavaFx controller for the main view.
 *
 * @author datafox
 */
public class MainController {
    private final Context context;

    private final Logger logger;

    private final Editor editor;

    private final NoteTreeCellFactory noteTreeCellFactory;

    private final ConnectionService connectionService;

    private final NoteService noteService;

    private final PopupService popupService;

    private final RestService restService;

    private final SettingsService settingsService;

    private final UiService uiService;

    @FXML
    private MenuItem settings;

    @FXML
    private MenuItem changePassword;

    @FXML
    private MenuItem logout;

    @FXML
    private MenuItem exit;

    @FXML
    private Menu spaces;

    @FXML
    private Menu openSpace;

    @FXML
    private MenuItem createSpace;

    @FXML
    private MenuItem about;

    @FXML
    private TreeView<Note> notes;

    @FXML
    private GridPane content;

    @Inject
    public MainController(Context context,
                          Logger logger,
                          Editor editor,
                          NoteTreeCellFactory noteTreeCellFactory,
                          ConnectionService connectionService, NoteService noteService,
                          PopupService popupService,
                          RestService restService,
                          SettingsService settingsService,
                          UiService uiService) {
        this.context = context;
        this.logger = logger;
        this.editor = editor;
        this.noteTreeCellFactory = noteTreeCellFactory;
        this.connectionService = connectionService;
        this.noteService = noteService;
        this.popupService = popupService;
        this.restService = restService;
        this.settingsService = settingsService;
        this.uiService = uiService;
    }

    @FXML
    private void initialize() {
        logger.info("Setting up UI elements for main view");

        //Menu button actions
        spaces.setOnShowing(this::refreshSpaceMenu);
        settings.setOnAction(this::changeSettings);
        changePassword.setOnAction(this::changePassword);
        logout.setOnAction(this::logout);
        exit.setOnAction(event -> Platform.exit());
        createSpace.setOnAction(this::createSpace);
        about.setOnAction(event -> popupService.showAboutPopup());

        //Set note tree cell factory
        notes.setCellFactory(noteTreeCellFactory::build);

        //Add tree view to context
        context.setTreeView(notes);

        //Refresh content when selected note has changed
        context.noteProperty().addListener(this::refreshContent);
    }

    /**
     * Show settings popup.
     */
    private void changeSettings(ActionEvent ignored) {
        popupService.showSettingsPopup(this::changeSettings);
    }

    /**
     * Set auto-connect and auto-login settings.
     */
    private void changeSettings(UserSettings settings) {
        settingsService.setAutoConnect(settings.isAutoConnect());
        if(!settings.isAutoLogin()) {
            settingsService.removeCookie();
            connectionService.setRememberCookie(null);
        }
    }

    /**
     * Show password change popup.
     */
    private void changePassword(ActionEvent ignored) {
        popupService.showPasswordPopup(this::changePassword);
    }

    /**
     * Send password change request to server.
     */
    private void changePassword(String oldPassword, String newPassword) {
        restService.changeUser(UserChangeDto.of(oldPassword, newPassword));
    }

    /**
     * Clear context, disable auto-connect and auto-login, and switch to connect view.
     */
    private void logout(ActionEvent ignored) {
        context.clear();

        changeSettings(UserSettings
                .builder()
                .autoConnect(false)
                .autoLogin(false)
                .build());

        uiService.setScene("connect");
    }

    /**
     * Look up all spaces in the current context and create menu items for them.
     */
    private void refreshSpaceMenu(Event ignored) {
        openSpace.getItems().setAll(context.getUser().getSpaces()
                .stream()
                .map(this::createSpaceItem)
                .toList());
    }

    /**
     * @param space space
     * @return menu item for selecting the space
     */
    private MenuItem createSpaceItem(SpaceHeader space) {
        MenuItem item = new MenuItem(space.getName());

        item.setOnAction(event -> noteService.loadSpace(space.getUuid()));

        return item;
    }

    /**
     * Clear children from content pane and add the editor to it if the selected note is not null.
     *
     * @param note selected note, may be null
     */
    private void refreshContent(ObservableValue<? extends Note> ignored, Note ignored1, Note note) {
        logger.info("Refreshing editor to show selected note");

        content.getChildren().clear();

        if(note != null) {
            content.add(editor, 0, 0);
        }
    }

    /**
     * Show space creation popup and create a new space.
     */
    private void createSpace(ActionEvent ignored) {
        popupService.showTextPopup("Create a space",
                "Please enter a title for your new space:",
                noteService::createSpace);
    }
}
