package io.github.plixo2.sodalite.category.events;


import java.util.Iterator;

public class EventIterator implements Iterator<EventOld> {

    private EventOld element;

    void start() {
        this.element = Events.pollEvent();
    }

    @Override
    public boolean hasNext() {
        return this.element != null;
    }

    @Override
    public EventOld next() {
        var event = this.element;
        this.element = Events.pollEvent();
        return event;
    }

}
