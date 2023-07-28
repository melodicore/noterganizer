package me.datafox.noterganizer.client.factory.impl;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.datafox.noterganizer.client.factory.PopupFactory;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.PopupButton;
import me.datafox.noterganizer.client.model.PopupComponent;
import me.datafox.noterganizer.client.model.PopupData;
import me.datafox.noterganizer.client.service.UiService;

import static me.datafox.noterganizer.client.ClientConstants.*;

/**
 * Implementation of {@link PopupFactory}.
 *
 * @author datafox
 */
@Component
public class PopupFactoryImpl implements PopupFactory {
    private final UiService uiService;

    @Inject
    public PopupFactoryImpl(UiService uiService) {
        this.uiService = uiService;
    }

    @Override
    public Stage buildPopup(PopupData data) {
        Stage parent = uiService.getStage();

        Stage stage = new Stage();

        Scene scene = new Scene(createRoot(data, stage));

        scene.getStylesheets().add("fx.css");

        stage.setTitle(data.getTitle());
        stage.setWidth(data.getWidth());
        stage.setHeight(data.getHeight());

        stage.setScene(scene);

        stage.setOnShown(event -> {
            stage.setX(parent.getX() + (parent.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(parent.getY() + (parent.getHeight() / 2) - (stage.getHeight() / 2));
        });

        return stage;
    }

    private GridPane createRoot(PopupData data, Stage stage) {
        GridPane root = new GridPane();

        root.getRowConstraints().setAll(ALWAYS_ROW);
        root.getColumnConstraints().setAll(ALWAYS_COLUMN);

        root.getStyleClass().addAll("grid-pane", "root", "background");

        root.addRow(0, createInner(data, stage));

        return root;
    }

    private GridPane createInner(PopupData data, Stage stage) {
        GridPane inner = new GridPane();

        inner.getColumnConstraints().setAll(ALWAYS_COLUMN);
        inner.setAlignment(Pos.TOP_LEFT);

        inner.getStyleClass().addAll("grid-pane", "foreground", "large-border");

        int row = 0;

        if(data.getText() != null && !data.getText().isBlank()) {
            inner.getRowConstraints().add(NEVER_ROW);
            inner.add(createLabel(data), 0, row++);
        }

        inner.getRowConstraints().add(ALWAYS_ROW);

        if(!data.getComponents().isEmpty()) {
            inner.getRowConstraints().add(NEVER_ROW);
            inner.add(createComponents(data, stage), 0, row++);
        }
        inner.add(createButtons(data, stage), 0, row);

        return inner;
    }

    private Label createLabel(PopupData data) {
        Label text = new Label(data.getText());

        text.setWrapText(true);

        return text;
    }

    private VBox createComponents(PopupData data, Stage stage) {
        VBox components = new VBox();

        components.getStyleClass().addAll("vbox");

        setTextFieldComponentActions(data, stage);

        components.getChildren().addAll(data.getComponents().stream().map(PopupComponent::getNode).toList());

        return components;
    }

    private void setTextFieldComponentActions(PopupData data, Stage stage) {
        TextField last = null;

        for(int i = data.getComponents().size() - 1; i >= 0; i--) {
            if(data.getComponents().get(i).getNode() instanceof TextField field) {
                if(last == null) {
                    field.setOnAction(event -> {
                        stage.hide();
                        callback(data);
                    });
                } else {
                    TextField finalLast = last;
                    field.setOnAction(event -> finalLast.requestFocus());
                }
                last = field;
            }
        }
    }

    private HBox createButtons(PopupData data, Stage stage) {
        HBox buttons = new HBox();

        buttons.getStyleClass().addAll("inner-hbox", "align-right");

        buttons.getChildren().addAll(
                data.getButtons()
                        .stream()
                        .peek(button -> button.getButton().setOnAction(event -> {
                            stage.hide();
                            if(button.isSendCallback()) callback(data);
                        }))
                        .map(PopupButton::getButton)
                        .toList());

        return buttons;
    }

    private void callback(PopupData data) {
        Platform.runLater(() -> data.getCallback().accept(
            data.getComponents()
                    .stream()
                    .filter(component -> component.getConverter() != null)
                    .map(PopupComponent::convert)
                    .toList()));
    }
}
