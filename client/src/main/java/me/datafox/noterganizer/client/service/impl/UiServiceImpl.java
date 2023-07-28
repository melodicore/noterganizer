package me.datafox.noterganizer.client.service.impl;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import me.datafox.noterganizer.client.factory.UiFactory;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.UiData;
import me.datafox.noterganizer.client.model.WindowSettings;
import me.datafox.noterganizer.client.service.SettingsService;
import me.datafox.noterganizer.client.service.UiService;
import org.apache.logging.log4j.Logger;

/**
 * UI service implementation.
 *
 * @author datafox
 */
@Component
public class UiServiceImpl implements UiService {
    private final Logger logger;

    private final UiFactory uiFactory;

    private final SettingsService settingsService;

    @Getter
    private Stage stage;

    @Inject
    public UiServiceImpl(Logger logger,
                         UiFactory uiFactory,
                         SettingsService settingsService) {
        this.logger = logger;
        this.uiFactory = uiFactory;
        this.settingsService = settingsService;
    }

    @Override
    public void setStage(Stage stage) {
        logger.info("The stage has been set");

        this.stage = stage;

        setStageSize();

        stage.addEventFilter(WindowEvent.WINDOW_HIDING, event -> saveStageSize());
    }

    @Override
    public void setScene(String name) {
        if(stage == null) {
            logger.error("Attempted to set scene while the stage is not set. This should never happen");
            throw new RuntimeException();
        }

        UiData<?> data = uiFactory.buildUi(name);

        if(stage.getScene() == null) {
            stage.setScene(new Scene(data.getUi(), 640, 480));
        } else {
            stage.getScene().setRoot(data.getUi());
        }
    }


    private void setStageSize() {
        WindowSettings windowSettings = settingsService.getWindowSettings();

        if(windowSettings == null) return;

        if(windowSettings.isMaximized()) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(windowSettings.getWidth());
            stage.setHeight(windowSettings.getHeight());

            stage.setX(windowSettings.getX());
            stage.setY(windowSettings.getY());
        }
    }

    private void saveStageSize() {
        settingsService.setWindowSettings(WindowSettings
                .builder()
                .maximized(stage.isMaximized())
                .width(stage.getWidth())
                .height(stage.getHeight())
                .x(stage.getX())
                .y(stage.getY())
                .build());
    }
}
