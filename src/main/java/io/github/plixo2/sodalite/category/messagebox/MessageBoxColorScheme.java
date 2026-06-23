package io.github.plixo2.sodalite.category.messagebox;


import lombok.*;
import org.libsdl.sdl.SDL_MessageBoxColorScheme;

import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_MessageBoxColorScheme
@Setter
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor(staticName = "of")
public class MessageBoxColorScheme {

    private MessageBoxColor background;
    private MessageBoxColor text;
    private MessageBoxColor buttonBorder;
    private MessageBoxColor buttonBackground;
    private MessageBoxColor buttonSelected;


    void put(MemorySegment segment) {
        this.background.put(SDL_MessageBoxColorScheme.colors(segment, 0));
        this.text.put(SDL_MessageBoxColorScheme.colors(segment, 1));
        this.buttonBorder.put(SDL_MessageBoxColorScheme.colors(segment, 2));
        this.buttonBackground.put(SDL_MessageBoxColorScheme.colors(segment, 3));
        this.buttonSelected.put(SDL_MessageBoxColorScheme.colors(segment, 4));
    }

}
