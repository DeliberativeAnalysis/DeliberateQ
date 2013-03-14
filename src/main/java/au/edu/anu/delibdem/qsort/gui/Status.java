package au.edu.anu.delibdem.qsort.gui;

import java.util.logging.Logger;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;

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
