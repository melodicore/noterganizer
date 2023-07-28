package me.datafox.noterganizer.client.ui;

import com.sandec.mdfx.MarkdownView;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import me.datafox.noterganizer.client.NoterganizerClient;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.Context;
import me.datafox.noterganizer.client.model.Note;
import me.datafox.noterganizer.client.service.NoteService;
import me.datafox.noterganizer.client.service.PopupService;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static me.datafox.noterganizer.client.ClientConstants.*;

/**
 * @author datafox
 */
@Component
public class Editor extends GridPane {
    private final Context context;

    private final Logger logger;

    private final NoteService noteService;

    private final NoterganizerClient client;

    private final PopupService popupService;

    private final TextArea source;

    private final TitledPane collapse;

    private final MarkdownView render;

    @Inject
    public Editor(Context context,
                  Logger logger,
                  NoteService noteService,
                  NoterganizerClient client,
                  PopupService popupService) {
        super();
        this.context = context;
        this.logger = logger;
        this.noteService = noteService;
        this.client = client;
        this.popupService = popupService;

        logger.info("Initializing the editor");

        source = createSource();
        collapse = createEditorCollapse();
        render = new EditorMarkdownView();

        ScrollPane scroll = createRenderScroll();

        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);

        context.noteProperty().addListener(this::noteChanged);

        getRowConstraints().setAll(NEVER_ROW, ALWAYS_ROW);
        getColumnConstraints().setAll(ALWAYS_COLUMN);

        addRow(0, collapse);
        addRow(1, scroll);
    }

    private TextArea createSource() {
        TextArea source = new TextArea();

        source.setWrapText(true);

        source.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                noteService.saveNote(context.getNote(), false);
            }
        });

        return source;
    }

    private TitledPane createEditorCollapse() {
        TitledPane collapse = new TitledPane("Editor", createEditorGridPane());

        collapse.setAlignment(Pos.BOTTOM_CENTER);

        return collapse;
    }

    private GridPane createEditorGridPane() {
        GridPane editor = new GridPane();

        editor.getStyleClass().addAll("inner-grid-pane", "small-border");
        editor.minHeightProperty().bind(Bindings.divide(heightProperty(), 2.5));
        editor.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2));

        editor.getRowConstraints().setAll(NEVER_ROW, ALWAYS_ROW);
        editor.getColumnConstraints().setAll(ALWAYS_COLUMN, NEVER_COLUMN);

        editor.add(createEditorButtons(), 0, 0, 2, 1);
        editor.add(source, 0, 1, 2, 1);

        return editor;
    }

    private HBox createEditorButtons() {
        HBox buttons = new HBox();

        buttons.getStyleClass().add("hbox");

        Button link = createLinkButton();

        buttons.getChildren().addAll(link);

        return buttons;
    }

    private Button createLinkButton() {
        Button button = new Button("Add Link");

        ContextMenu menu = createLinkMenu();

        button.setOnMouseClicked(event -> menu.show(button, event.getScreenX(), event.getScreenY()));

        return button;
    }

    private ContextMenu createLinkMenu() {
        ContextMenu menu = new ContextMenu();

        context.getNoteTreeChangeSubscription().addListener(() ->
                menu.getItems().setAll(createExternalLinkMenuItem(), createLinkMenuItem(context.getSpace().getRoot())));

        return menu;
    }

    private MenuItem createLinkMenuItem(Note note) {
        MenuItem item = new MenuItem();

        item.setOnAction(event -> addInternalLink(note));

        if(!note.getChildren().isEmpty()) {
            Menu menu = new Menu();

            item.setText("[this]");

            menu.getItems().setAll(note.getChildren().stream().map(this::createLinkMenuItem).toList());

            menu.getItems().sort(Comparator.comparing(MenuItem::getText));

            menu.getItems().add(0, item);

            item = menu;
        }

        item.textProperty().bind(note.titleProperty());

        return item;
    }


    private MenuItem createExternalLinkMenuItem() {
        MenuItem item = new MenuItem("External");

        item.setOnAction(event -> addExternalLink());

        return item;
    }

    private void addInternalLink(Note note) {
        addLink(note.getTitle(), "note://" + note.getUuid());
    }

    private void addExternalLink() {
        popupService.showTextPopup("Add link",
                "Please enter link address:",
                address -> addLink("", address));
    }

    private void addLink(String text, String address) {
        String title = source.getSelectedText();

        if(title.isEmpty()) {
            title = text;
        }

        String link = "[" + title + "](" + address + ")";

        int position = source.getCaretPosition();

        source.replaceText(source.getSelection(), link);

        if(title.isEmpty()) {
            source.positionCaret(position + 1);
        } else {
            source.positionCaret(position + link.length());
        }
    }

    private ScrollPane createRenderScroll() {
        ScrollPane scroll = new ScrollPane(render);

        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scroll.setFitToWidth(true);

        return scroll;
    }

    private void noteChanged(ObservableValue<? extends Note> ignored, Note oldValue, Note newValue) {
        if(oldValue != null) {
            source.textProperty().unbindBidirectional(oldValue.contentProperty());
            render.mdStringProperty().unbindBidirectional(oldValue.contentProperty());
            collapse.textProperty().unbind();
        }
        if(newValue != null) {
            source.textProperty().bindBidirectional(newValue.contentProperty());
            render.mdStringProperty().bindBidirectional(newValue.contentProperty());
            collapse.textProperty().bind(newValue.titleProperty());
        }
    }

    private class EditorMarkdownView extends MarkdownView {
        @Override
        protected List<String> getDefaultStylesheets() {
            return List.of();
        }

        @Override
        public void setLink(Node node, String link, String description) {
            node.setOnMouseClicked(event -> openLink(link));
        }

        private void openLink(String link) {
            logger.info("Opening link " + link);

            if(link.startsWith("note://")) {
                logger.debug("Link points to a note, opening the note");
                noteService.openNote(link.replaceFirst(Pattern.quote("note://"), ""));
            } else {
                logger.debug("Link does not point to a note, opening as a regular link");
                client.getHostServices().showDocument(link);
            }
        }
    }
}
