package me.datafox.noterganizer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto object for changing a user's password.
 * Sent from client to server.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChangeDto {
    private String oldPassword;

    private String newPassword;

    public static UserChangeDto of(String oldPassword, String newPassword) {
        return builder()
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
    }
}
