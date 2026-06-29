package io.github.plixo2.sodalite.category.video;

import io.github.plixo2.sodalite.SDLException;
import io.github.plixo2.sodalite.category.properties.PropertyGroup;
import io.github.plixo2.sodalite.category.rect.Rect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/// @sdlAPI SDL_DisplayID
@Getter
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class DisplayID {
    private final int value;

    public PropertyGroup getProperties() throws SDLException {
        return Video.getDisplayProperties(this);
    }

    public String getDisplayName() throws SDLException {
        return Video.getDisplayName(this);
    }

    public Rect getDisplayBounds() throws SDLException {
        return Video.getDisplayBounds(this);
    }

    public Rect getUsableBounds() throws SDLException {
        return Video.getDisplayUsableBounds(this);
    }

    public DisplayOrientation getNaturalOrientation() {
        return Video.getNaturalDisplayOrientation(this);
    }

    public DisplayOrientation getCurrentOrientation() {
        return Video.getCurrentDisplayOrientation(this);
    }

    public float getContentScale() {
        return Video.getDisplayContentScale(this);
    }

    public List<DisplayMode> getFullscreenDisplayModes() throws SDLException {
        return Video.getFullscreenDisplayModes(this);
    }

    public DisplayMode getClosestFullscreenDisplayMode(
            int width,
            int height,
            float refreshRate,
            boolean includeHighDensityModes
    ) throws SDLException {
        return Video.getClosestFullscreenDisplayMode(this, width, height, refreshRate, includeHighDensityModes);
    }

    public DisplayMode getDesktopDisplayMode() throws SDLException {
        return Video.getDesktopDisplayMode(this);
    }

    public DisplayMode getCurrentDisplayMode() throws SDLException {
        return Video.getCurrentDisplayMode(this);
    }


}
