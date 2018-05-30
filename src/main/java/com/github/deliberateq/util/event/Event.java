package com.github.deliberateq.util.event;

public final class Event {
    
    private final Object object;
    private final EventType type;

    public Event(Object o) {
        this(o, null);
    }

    public Event(Object o, EventType type) {
        this.object = o;
        this.type = type;
    }

    public Object getObject() {
        return this.object;
    }

    public EventType getType() {
        return this.type;
    }
}
