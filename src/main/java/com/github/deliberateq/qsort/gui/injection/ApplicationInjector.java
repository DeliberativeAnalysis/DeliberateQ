package com.github.deliberateq.qsort.gui.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ApplicationInjector {

	private static Injector injector;

	public synchronized static Injector getInjector() {
		if (injector == null)
			injector = Guice.createInjector(new ApplicationInjectorModule());
		return injector;
	}

}
