package me.datafox.noterganizer.api.dto;

import lombok.*;

import java.util.List;

/**
 * Dto object for a user.
 * Sent from server to client.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;

    @Singular
    private List<SpaceHeaderDto> spaces;
}
