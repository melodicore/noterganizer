package me.datafox.noterganizer.client.service.impl;

import me.datafox.noterganizer.api.dto.*;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.Response;
import me.datafox.noterganizer.client.service.ConnectionService;
import me.datafox.noterganizer.client.service.PopupService;
import me.datafox.noterganizer.client.service.RestService;

import java.util.Optional;

/**
 * REST service implementation.
 *
 * @author datafox
 */
@Component
public class RestServiceImpl implements RestService {
    private final PopupService popupService;

    private final ConnectionService connectionService;

    @Inject
    public RestServiceImpl(PopupService popupService,
                           ConnectionService connectionService) {
        this.popupService = popupService;
        this.connectionService = connectionService;
    }

    @Override
    public Optional<String> connect(String address) {
        return handleResponse(connectionService.connect(address));
    }

    @Override
    public Optional<String> login(String username, String password, boolean remember) {
        return handleResponse(connectionService.login(username, password, remember));
    }

    @Override
    public Optional<UserDto> getUser() {
        return handleResponse(connectionService.get("user", UserDto.class));
    }

    @Override
    public Optional<String> register(UserRegisterDto dto) {
        return handleResponse(connectionService.post(dto, "register", String.class));
    }

    @Override
    public Optional<String> changeUser(UserChangeDto dto) {
        return handleResponse(connectionService.post(dto, "change", String.class));
    }

    @Override
    public Optional<SpaceDto> getSpace(String uuid) {
        return handleResponse(connectionService.get("space/get?uuid=" + uuid, SpaceDto.class));
    }

    @Override
    public Optional<String> createSpace(SpaceCreateDto dto) {
        return handleResponse(connectionService.post(dto, "space/create", String.class));
    }

    @Override
    public Optional<String> removeSpace(String uuid) {
        return handleResponse(connectionService.delete("space/remove?uuid=" + uuid, String.class));
    }

    @Override
    public Optional<String> createNote(NoteCreateDto dto) {
        return handleResponse(connectionService.post(dto, "note/create", String.class));
    }

    @Override
    public Optional<String> changeNote(NoteChangeDto dto) {
        return handleResponse(connectionService.post(dto, "note/change", String.class));
    }

    @Override
    public Optional<String> moveNote(NoteMoveDto dto) {
        return handleResponse(connectionService.post(dto, "note/move", String.class));
    }

    @Override
    public Optional<String> removeNote(String uuid, boolean removeChildren) {
        return handleResponse(connectionService.delete(
                "note/remove?uuid=" + uuid + "&removeChildren=" + removeChildren, String.class));
    }

    private <T> Optional<T> handleResponse(Response<T> response) {
        if(response.isError()) {
            popupService.showInfoPopup("Application error",
                    "There was an application error:\n" + response.status());
        }

        return response.optional();
    }
}
