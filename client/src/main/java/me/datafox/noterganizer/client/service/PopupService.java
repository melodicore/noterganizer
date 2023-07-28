package me.datafox.noterganizer.client.service;

import me.datafox.noterganizer.client.model.UserSettings;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The popup service handles generating popups by using the
 * {@link me.datafox.noterganizer.client.factory.PopupFactory}.
 *
 * @author datafox
 */
public interface PopupService {
    /**
     * An info popup has text and a confirm button.
     *
     * @param title title
     * @param content content
     */
    void showInfoPopup(String title, String content);

    /**
     * A text popup has a text field for inputting data.
     *
     * @param title title
     * @param content content
     * @param callback called with the text field's contents if the user confirms
     */
    void showTextPopup(String title, String content, Consumer<String> callback);

    /**
     * A confirmation popup has a cancel and confirm button.
     *
     * @param title title
     * @param content content
     * @param callback called if the user confirms
     */
    void showConfirmPopup(String title, String content, Runnable callback);

    /**
     * A checkbox confirm popup has a checkbox.
     *
     * @param title title
     * @param content content
     * @param checkbox checkbox
     * @param callback called with the checkbox's status if the user confirms
     */
    void showCheckboxConfirmPopup(String title, String content, String checkbox, Consumer<Boolean> callback);

    /**
     * The settings popup has user settings.
     *
     * @param callback called with the selected settings if the user confirms
     */
    void showSettingsPopup(Consumer<UserSettings> callback);

    /**
     * The password popup has two text fields for the user's old and new passwords.
     *
     * @param callback called with the inputted passwords if the user confirms
     */
    void showPasswordPopup(BiConsumer<String, String> callback);

    /**
     * The about popup has information about the application.
     */
    void showAboutPopup();
}
