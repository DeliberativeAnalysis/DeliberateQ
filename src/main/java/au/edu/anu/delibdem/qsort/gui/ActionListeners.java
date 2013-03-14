package au.edu.anu.delibdem.qsort.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import moten.david.util.event.EventType;

public class ActionListeners {

	public static ActionListener getNotifyActionListener(
			final EventType eventType) {
		return getNotifyActionListener(null, eventType);
	}

	public static ActionListener getNotifyActionListener(final Object object,
			final EventType eventType) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventManager.getInstance().notify(new Event(object, eventType));
			}
		};
	}

}
