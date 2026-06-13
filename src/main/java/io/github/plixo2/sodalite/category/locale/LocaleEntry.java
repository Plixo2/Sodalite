package io.github.plixo2.sodalite.category.locale;


import org.jetbrains.annotations.Nullable;

/// @sdlAPI SDL_Locale
public record LocaleEntry(
        String language,
        @Nullable String country
) {}

