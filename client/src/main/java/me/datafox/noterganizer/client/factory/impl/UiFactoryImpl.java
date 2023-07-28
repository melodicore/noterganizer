package me.datafox.noterganizer.client.factory.impl;

import javafx.fxml.FXMLLoader;
import me.datafox.noterganizer.client.factory.UiFactory;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.injection.Injector;
import me.datafox.noterganizer.client.model.UiData;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Implementation of {@link UiFactory}. {@link Injector} is used to achieve
 * arbitrary dependency injection in JavaFx controller classes.
 *
 * @author datafox
 */
@Component
public class UiFactoryImpl implements UiFactory {
    private final Injector injector;

    private final Logger logger;

    @Inject
    public UiFactoryImpl(Injector injector,
                         Logger logger) {
        this.injector = injector;
        this.logger = logger;
    }

    /**
     * Initializes a view based on the given name. An FXML file with the
     * given name must exist within resources, and the file must reference
     * a controller class that can be initialized with {@link Injector}.
     * The controller class must not be annotated with {@link Component} as
     * it is initialized per call and can may be initialized multiple times.
     *
     * @param name name of the view
     * @return root node and controller
     */
    @Override
    public <T> UiData<T> buildUi(String name) {
        logger.info("Loading view " + name);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + name + ".fxml"));

        loader.setControllerFactory(injector::newInstance);

        try {
            return UiData.<T>builder()
                    .ui(loader.load())
                    .controller(loader.getController())
                    .build();
        } catch(IOException e) {
            logger.error("Could not load view " + name, e);
            throw new RuntimeException(e);
        }
    }
}
