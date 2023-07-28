package me.datafox.noterganizer.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ControllerAdvice for all application-specific exceptions.
 *
 * @author datafox
 */
@RestControllerAdvice
public class AppControllerAdvice {
    @ExceptionHandler(value = AuthorizationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public String handleAuthorizationException(AuthorizationException exception) {
        return "The password provided is not your current password";
    }

    @ExceptionHandler(value = CyclicNoteException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleCyclicNoteException(CyclicNoteException exception) {
        return "Cannot move note to be its own descendant";
    }

    @ExceptionHandler(value = ForbiddenActionException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public String handleForbiddenActionException(ForbiddenActionException exception) {
        return "You do not have permissions to perform this action";
    }

    @ExceptionHandler(value = MoveRootNoteException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleMoveRootNoteException(MoveRootNoteException exception) {
        return "Cannot move root note";
    }

    @ExceptionHandler(value = NoteNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNoteNotFoundException(NoteNotFoundException exception) {
        return "No note exists with specified uuid";
    }

    @ExceptionHandler(value = PasswordTooShortException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handlePasswordTooShortException(PasswordTooShortException exception) {
        return "The password must be at least 8 characters long";
    }

    @ExceptionHandler(value = RemoveRootNoteException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleRemoveRootNoteException(RemoveRootNoteException exception) {
        return "Cannot remove root note";
    }

    @ExceptionHandler(value = UsernameTakenException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public String handleUsernameTakenException(UsernameTakenException exception) {
        return "Username already taken";
    }

    @ExceptionHandler(value = SpaceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleSpaceNotFoundException(SpaceNotFoundException exception) {
        return "No space exists with specified uuid";
    }
}
