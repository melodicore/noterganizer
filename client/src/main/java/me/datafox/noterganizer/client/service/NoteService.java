package me.datafox.noterganizer.client.service;

import me.datafox.noterganizer.client.model.Note;

/**
 * The note service contains methods for all note-related operations.
 *
 * @author datafox
 */
public interface NoteService {
    /**
     * @param title title for the space to be created
     */
    void createSpace(String title);

    /**
     * @param uuid UUID of the space to be loaded
     */
    void loadSpace(String uuid);

    /**
     * @param uuid UUID of the space to be removed
     */
    void removeSpace(String uuid);

    /**
     * @param parent parent of the note to be created
     * @param title title for the note to be created
     */
    void createNote(Note parent, String title);

    /**
     * @param uuid UUID of the note to be loaded
     */
    void openNote(String uuid);

    /**
     * @param note note to be renamed
     * @param title new title for the note
     */
    void renameNote(Note note, String title);

    /**
     * @param note note to be saved
     * @param force if true, a save request is sent to the server
     *              even if the note's content has not changed
     */
    void saveNote(Note note, boolean force);

    /**
     * @param note note to be moved
     * @param newParent new parent note
     * @return true if the move was successful
     */
    boolean moveNote(Note note, Note newParent);

    /**
     * @param note note to be removed
     * @param removeChildren if set to true, removes all child notes recursively, otherwise
     *                       moves orphaned child notes onto the removed note's parent
     */
    void removeNote(Note note, boolean removeChildren);
}
