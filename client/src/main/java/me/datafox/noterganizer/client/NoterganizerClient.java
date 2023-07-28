package me.datafox.noterganizer.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.stage.Stage;
import me.datafox.noterganizer.client.injection.Injector;
import me.datafox.noterganizer.client.injection.PerComponentFactory;
import me.datafox.noterganizer.client.model.Context;
import me.datafox.noterganizer.client.serialization.HttpCookieAdapter;
import me.datafox.noterganizer.client.service.UiService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.http.HttpClient;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Noterganizer is a JavaFx-based software package for writing notes with
 * the Markup formatting language, and organize them in tree-like structures.
 *
 * @author datafox
 */
public class NoterganizerClient extends Application {
    @Override
    public void start(Stage stage) {
        Configurator.setRootLevel(Level.DEBUG);

        Injector injector = Injector
                .builder()
                .beans(instantiateExternalBeans())
                .factories(instantiateFactories())
                .build();

        UiService uiService = injector.getBean(UiService.class);

        uiService.setStage(stage);
        uiService.setScene("connect");

        stage.setTitle("Noterganizer");

        stage.show();
    }

    private Collection<Object> instantiateExternalBeans() {
        HttpClient client = HttpClient
                .newBuilder()
                .cookieHandler(new CookieManager())
                .build();

        Context context = new Context();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(HttpCookie.class, new HttpCookieAdapter())
                .create();

        Preferences preferences = Preferences
                .userRoot()
                .node("noterganizer");

        return List.of(this, client, context, gson, preferences);
    }

    private Collection<PerComponentFactory<?>> instantiateFactories() {
        PerComponentFactory<Logger> loggerFactory = new PerComponentFactory<>() {
            @Override
            public Class<Logger> getType() {
                return Logger.class;
            }

            @Override
            public Logger apply(Class<?> aClass) {
                return LogManager.getLogger(aClass);
            }
        };
        return List.of(loggerFactory);
    }
}
