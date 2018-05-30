package com.github.deliberateq.util.math.gui;

import java.util.ArrayList;
import java.util.List;

public class SelectionManager {

	private static SelectionManager instance = null;

	private List<SelectionListener> listeners = new ArrayList<SelectionListener>();

	public synchronized static SelectionManager getInstance() {
		if (instance == null)
			instance = new SelectionManager();
		return instance;
	}

	public synchronized void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Keeping in mind the desire to decouple from the source of the event, a
	 * listener might want to simply check the description string and/or use the
	 * object rather than cast the event to a particular subclass of the event.
	 * 
	 * @param description
	 * @param event
	 * @param object
	 */
	public synchronized void selectionChanged(SelectionManagerEvent event) {
		for (SelectionListener l : listeners)
			l.selectionChanged(event);
	}

	public interface SelectionManagerEvent {
		String getDescription();

		Object getObject();
	}

}
