package me.datafox.noterganizer.client.service.impl;

import com.sandec.mdfx.MarkdownView;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.*;
import me.datafox.noterganizer.client.NoterganizerClient;
import me.datafox.noterganizer.client.factory.PopupFactory;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.PopupButton;
import me.datafox.noterganizer.client.model.PopupComponent;
import me.datafox.noterganizer.client.model.PopupData;
import me.datafox.noterganizer.client.model.UserSettings;
import me.datafox.noterganizer.client.service.PopupService;
import me.datafox.noterganizer.client.service.SettingsService;
import me.datafox.noterganizer.client.service.ValidationService;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Popup service implementation.
 *
 * @author datafox
 */
@Component
public class PopupServiceImpl implements PopupService {
    private final NoterganizerClient client;

    private final Logger logger;

    private final PopupFactory popupFactory;

    private final SettingsService settingsService;

    private final ValidationService validationService;

    @Inject
    public PopupServiceImpl(NoterganizerClient client,
                            Logger logger,
                            PopupFactory popupFactory,
                            SettingsService settingsService,
                            ValidationService validationService) {
        this.client = client;
        this.logger = logger;
        this.popupFactory = popupFactory;
        this.settingsService = settingsService;
        this.validationService = validationService;
    }

    @Override
    public void showInfoPopup(String title, String content) {
        logger.info("Showing info popup " + title + " with content " + content);

        popupFactory.buildPopup(PopupData.builder()
                .title(title)
                .text(content)
                .button(PopupButton.builder()
                        .button(new Button("Okay"))
                        .build())
                .build()).show();
    }

    @Override
    public void showTextPopup(String title, String content, Consumer<String> callback) {
        logger.info("Showing text popup " + title + " with content " + content);

        popupFactory.buildPopup(PopupData.builder()
                .title(title)
                .text(content)
                .component(PopupComponent.<TextField>builder()
                        .node(new TextField())
                        .converter(TextField::getText)
                        .build())
                .button(PopupButton.builder()
                        .button(new Button("Cancel"))
                        .sendCallback(false)
                        .build())
                .button(PopupButton.builder()
                        .button(new Button("Okay"))
                        .sendCallback(true)
                        .build())
                .callback(strings -> callback.accept(strings.get(0)))
                .build()).show();
    }

    @Override
    public void showConfirmPopup(String title, String content, Runnable callback) {
        logger.info("Showing confirm popup " + title + " with content " + content);

        popupFactory.buildPopup(PopupData.builder()
                .title(title)
                .text(content)
                .button(PopupButton.builder()
                        .button(new Button("No"))
                        .sendCallback(false)
                        .build())
                .button(PopupButton.builder()
                        .button(new Button("Yes"))
                        .sendCallback(true)
                        .build())
                .callback(strings -> callback.run())
                .build()).show();
    }

    @Override
    public void showCheckboxConfirmPopup(String title, String content, String checkbox, Consumer<Boolean> callback) {
        logger.info("Showing check box confirm popup " + title + " with content " + content + " and checkbox " + checkbox);

        popupFactory.buildPopup(PopupData.builder()
                .title(title)
                .text(content)
                .component(PopupComponent.<CheckBox>builder()
                        .node(new CheckBox(checkbox))
                        .converter(box -> String.valueOf(box.isSelected()))
                        .build())
                .button(PopupButton.builder()
                        .button(new Button("No"))
                        .sendCallback(false)
                        .build())
                .button(PopupButton.builder()
                        .button(new Button("Yes"))
                        .sendCallback(true)
                        .build())
                .callback(strings -> callback.accept(Boolean.parseBoolean(strings.get(0))))
                .build()).show();
    }

    @Override
    public void showSettingsPopup(Consumer<UserSettings> callback) {
        logger.info("Showing settings window");

        popupFactory.buildPopup(PopupData.builder()
                .title("Settings")
                .text("Settings")
                .component(PopupComponent.<CheckBox>builder()
                        .node(new CheckBox("Autoconnect"))
                        .converter(checkBox -> String.valueOf(checkBox.isSelected()))
                        .initializer(checkBox -> checkBox.setSelected(settingsService.getAutoConnect()))
                        .build())
                .component(PopupComponent.<CheckBox>builder()
                        .node(new CheckBox("Autologin"))
                        .converter(checkBox -> String.valueOf(checkBox.isSelected()))
                        .initializer(checkBox -> {
                            boolean autoLogin = settingsService.getCookie() != null;
                            checkBox.setSelected(autoLogin);
                            checkBox.setDisable(!autoLogin);
                        }).build())
                .button(PopupButton.builder()
                        .button(new Button("Cancel"))
                        .sendCallback(false)
                        .build())
                .button(PopupButton.builder()
                        .button(new Button("Save"))
                        .sendCallback(true)
                        .build())
                .callback(strings -> callback.accept(UserSettings.builder()
                                .autoConnect(Boolean.parseBoolean(strings.get(0)))
                                .autoLogin(Boolean.parseBoolean(strings.get(1))).build()))
                .build()).show();
    }

    @Override
    public void showPasswordPopup(BiConsumer<String,String> callback) {
        logger.info("Showing password change window");

        TextField[] passwordFieldFinal = new TextField[1];
        popupFactory.buildPopup(PopupData.builder()
                .title("Change password")
                .text("Old password")
                .height(250)
                .component(PopupComponent.<PasswordField>builder()
                        .node(new PasswordField())
                        .initializer(passwordField -> {
                            passwordFieldFinal[0] = passwordField;
                            passwordField.textProperty().addListener((observable, oldValue, newValue) ->
                                    passwordField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                            !validationService.validatePassword(newValue)));
                        })
                        .converter(PasswordField::getText)
                        .build())
                .component(PopupComponent.builder()
                        .node(new Label("New password"))
                        .build())
                .component(PopupComponent.<PasswordField>builder()
                        .node(new PasswordField())
                        .initializer(passwordField -> {
                            passwordFieldFinal[0] = passwordField;
                            passwordField.textProperty().addListener((observable, oldValue, newValue) ->
                                    passwordField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                            !validationService.validatePassword(newValue)));
                        })
                        .converter(PasswordField::getText)
                        .build())
                .component(PopupComponent.builder()
                        .node(new Label("Repeat new password"))
                        .build())
                .component(PopupComponent.<PasswordField>builder()
                        .node(new PasswordField())
                        .initializer(repeatField ->
                                repeatField.textProperty().addListener(((observable, oldValue, newValue) ->
                                        repeatField.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"),
                                                !validationService.validatePasswordRepeat(
                                                        passwordFieldFinal[0].getText(),
                                                        repeatField.getText())))))
                        .converter(PasswordField::getText)
                        .build())
                .button(PopupButton.builder()
                        .button(new Button("Cancel"))
                        .sendCallback(false)
                        .build())
                .button(PopupButton.builder()
                        .button(new Button("Save"))
                        .sendCallback(true)
                        .build())
                .callback(strings -> {
                    if(!validationService.validatePassword(strings.get(1))) {
                        showInfoPopup("Password error",
                                "Password must be at least 8 characters long");
                        return;
                    }

                    if(!validationService.validatePasswordRepeat(strings.get(1), strings.get(2))) {
                        showInfoPopup("Password error",
                                "Repeated password does not match the original");
                        return;
                    }
                    callback.accept(strings.get(0), strings.get(1));
                })
                .build()).show();
    }

    @Override
    public void showAboutPopup() {
        logger.info("Showing about window");

        popupFactory.buildPopup(PopupData.builder()
                .title("About Noterganizer")
                .width(480)
                .height(480)
                .component(PopupComponent.<MarkdownView>builder()
                        .node(new MarkdownView("""
                                # Noterganizer
                                                                
                                > Noterganizer is a simple application for writing and organizing notes in a tree-like structure. Notes are written with [Markdown](https://en.wikipedia.org/wiki/Markdown) support and are stored on a server.
                                                                
                                Developed by: [Lauri "datafox" Heino](https://datafox.me)""") {
                            @Override
                            protected List<String> getDefaultStylesheets() {
                                return List.of();
                            }
                            @Override
                            public void setLink(Node node, String link, String description) {
                                node.setOnMouseClicked(event -> client.getHostServices().showDocument(link));
                            }
                        }).build())
                .button(PopupButton.builder()
                        .button(new Button("Okay"))
                        .build())
                .build()).show();
    }
}
