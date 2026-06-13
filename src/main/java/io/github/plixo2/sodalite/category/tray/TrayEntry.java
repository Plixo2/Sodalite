package io.github.plixo2.sodalite.category.tray;


import java.lang.foreign.MemorySegment;

/// @sdlAPI SDL_TrayEntry
public class TrayEntry {

    final TrayObject object;
    private final MemorySegment segment;

    private boolean manuallyReleased = false;

    TrayEntry(TrayObject object, MemorySegment segment) {
        this.object = object;
        this.segment = segment;
    }

    public void label(String label) {
        Tray.setTrayEntryLabel(this, label);
    }
    public void enabled(boolean enabled) {
        Tray.setTrayEntryEnabled(this, enabled);
    }
    public void checked(boolean checked) {
        Tray.setTrayEntryChecked(this, checked);
    }

    public String label() {
        return Tray.getTrayEntryLabel(this);
    }
    public boolean enabled() {
        return Tray.isTrayEntryEnabled(this);
    }
    public boolean checked() {
        return Tray.isTrayEntryChecked(this);
    }

    public void setCallback(TrayCallback callback) {
        Tray.setTrayEntryCallback(this, callback);
    }

    public void remove() {
        Tray.removeTrayEntry(this);
        this.manuallyReleased = true;
    }


    public MemorySegment segment() {
        this.object.ensureNotReleased();
        if (this.manuallyReleased) {
            throw new IllegalStateException("TrayEntry has been manually released");
        }
        return this.segment;
    }



}
