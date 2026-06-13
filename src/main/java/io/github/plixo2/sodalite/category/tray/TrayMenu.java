package io.github.plixo2.sodalite.category.tray;


import java.lang.foreign.MemorySegment;
import java.util.Objects;

/// @sdlAPI SDL_TrayMenu
public class TrayMenu {

    final TrayObject object;
    private final MemorySegment segment;

    TrayMenu(TrayObject object, MemorySegment segment) {
        this.object = object;
        this.segment = segment;
    }

    public TrayEntry addButton(
            String label
    ) {
        return addButton(label, false);
    }
    public TrayEntry addButton(
            String label,
            boolean disabled
    ) {
        return Tray.insertTrayEntryAt(
                this,
                -1,
                Objects.requireNonNull(label),
                TrayEntryFlags.BUTTON
                        | (disabled ? TrayEntryFlags.DISABLED : 0)
        );
    }

    public TrayEntry addCheckbox(
            String label
    ) {
        return addCheckbox(label, false, false);
    }
    public TrayEntry addCheckbox(
            String label,
            boolean disabled,
            boolean checked
    ) {
        return Tray.insertTrayEntryAt(
                this,
                -1,
                Objects.requireNonNull(label),
                TrayEntryFlags.CHECKBOX
                        | (checked ? TrayEntryFlags.CHECKED : 0)
                        | (disabled ? TrayEntryFlags.DISABLED : 0)
        );
    }

    public TrayMenu addSubmenu(
            String label
    ) {
        var entry = Tray.insertTrayEntryAt(
                this,
                -1,
                Objects.requireNonNull(label),
                TrayEntryFlags.SUBMENU
        );
        return Tray.createTraySubmenu(entry);
    }


    public void addSeparator() {
        Tray.insertTrayEntryAt(
                this,
                -1,
                null,
                TrayEntryFlags.BUTTON
        );
    }



    public MemorySegment segment() {
        this.object.ensureNotReleased();
        return this.segment;
    }

}
