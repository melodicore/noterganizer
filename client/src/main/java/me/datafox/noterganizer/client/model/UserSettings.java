package me.datafox.noterganizer.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSettings {
    @Builder.Default
    private boolean autoConnect = false;

    @Builder.Default
    private boolean autoLogin = false;
}
