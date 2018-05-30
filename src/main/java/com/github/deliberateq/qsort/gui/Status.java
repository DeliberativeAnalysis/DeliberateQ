package com.github.deliberateq.qsort.gui;

import java.util.logging.Logger;

import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;

public class Status {
	private static final Logger log = Logger.getLogger(Status.class.getName());

	public static void setStatus(String message) {
		log.info("status=" + message);
		EventManager.getInstance().notify(new Event(message, Events.STATUS));
	}

	public static void finish() {
		log.info("status cleared");
		EventManager.getInstance().notify(
				new Event(null, Events.STATUS_FINISHED));
	}

}
