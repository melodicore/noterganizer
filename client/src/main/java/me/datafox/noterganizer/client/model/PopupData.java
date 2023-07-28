package me.datafox.noterganizer.client.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.function.Consumer;

import static me.datafox.noterganizer.client.ClientConstants.POPUP_HEIGHT;
import static me.datafox.noterganizer.client.ClientConstants.POPUP_WIDTH;

/**
 * Data for defining popups.
 *
 * @author datafox
 */
@Data
@Builder
public class PopupData {
    private String title;

    private String text;

    @Builder.Default
    private double width = POPUP_WIDTH;

    @Builder.Default
    private double height = POPUP_HEIGHT;

    @Singular
    private List<PopupComponent<?>> components;

    @Singular
    private List<PopupButton> buttons;

    private Consumer<List<String>> callback;
}
