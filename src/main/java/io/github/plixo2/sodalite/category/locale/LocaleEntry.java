package io.github.plixo2.sodalite.category.locale;


import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

/// @sdlAPI SDL_Locale
public record LocaleEntry(
        String language,
        @Nullable String country
) {
    public Locale toLocale() {
        return Locale.of(this.language, Objects.requireNonNullElse(this.country, ""));
    }

}

