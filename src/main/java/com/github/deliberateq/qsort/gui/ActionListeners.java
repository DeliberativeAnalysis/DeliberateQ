package com.github.deliberateq.qsort.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;
import com.github.deliberateq.util.event.EventType;

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
