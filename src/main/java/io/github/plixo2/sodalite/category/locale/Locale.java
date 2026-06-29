package io.github.plixo2.sodalite.category.locale;


import io.github.plixo2.sodalite.SDLException;
import org.libsdl.sdl.SDL_Locale;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.libsdl.sdl.SDL3_h.*;
import static io.github.plixo2.sodalite.Internal.*;


/// @sdlCategory CategoryLocale
public class Locale {
    private Locale() {}

    /// @sdlAPI SDL_GetPreferredLocales
    /// @sdlOther SDL_free
    public static List<LocaleEntry> preferredLocales() throws SDLException {
        try (var arena = Arena.ofConfined()) {
            var countSegment = arena.allocate(ValueLayout.JAVA_INT);
            var ptr = check(SDL_GetPreferredLocales(countSegment));

            try {
                var count = countSegment.get(ValueLayout.JAVA_INT, 0);

                var list = new ArrayList<LocaleEntry>(count);
                for (int i = 0; i < count; i++) {
                    var localePtr =
                            ptr.getAtIndex(ValueLayout.ADDRESS, i)
                               .reinterpret(SDL_Locale.sizeof());
                    var language = SDL_Locale.language(localePtr);
                    var country = SDL_Locale.country(localePtr);
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
