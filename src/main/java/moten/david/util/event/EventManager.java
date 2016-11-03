package moten.david.util.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {
    private static EventManager instance;
    private Map<EventType, List<EventManagerListener>> listeners = new ConcurrentHashMap<EventType, List<EventManagerListener>>();

    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public synchronized void addListener(EventType eventType, EventManagerListener l) {
        if (this.listeners.get(eventType) == null) {
            this.listeners.put(eventType, new ArrayList<EventManagerListener>());
        }
        this.listeners.get(eventType).add(l);
    }

    public synchronized void removeListener(EventType eventType, EventManagerListener l) {
        this.listeners.get(eventType).remove(l);
    }

    public void notify(Event event) {
        if (this.listeners.get(event.getType()) == null) {
            return;
        }
        for (EventManagerListener l : this.listeners.get(event.getType())) {
            l.notify(event);
        }
    }
}
