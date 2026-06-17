package io.github.plixo2.sodalite.category.locale;


import org.libsdl.sdl.SDL_Locale;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;


/// @sdlCategory CategoryLocale
public class Locale {
    private Locale() {}

    /// @sdlAPI SDL_GetPreferredLocales
    /// @sdlOther SDL_free
    public static List<LocaleEntry> preferredLocales() {
        try (var arena = Arena.ofConfined()) {
            var countSegment = arena.allocate(ValueLayout.JAVA_INT);
            var ptr = check(SDL_GetPreferredLocales(countSegment));

            try {
                var count = countSegment.get(ValueLayout.JAVA_INT, 0);

                var list = new ArrayList<LocaleEntry>(count);
                for (int i = 0; i < count; i++) {
                    var instance = SDL_Locale.asSlice(ptr, i);
                    var language = SDL_Locale.language(instance);
                    var country = SDL_Locale.country(instance);
                    var entry = new LocaleEntry(
                            language.getString(0),
                            getNullString(country)
                    );
                    list.add(entry);
                }
                return List.copyOf(list);
            } finally {
                SDL_free(ptr);
            }


        }


    }

}
