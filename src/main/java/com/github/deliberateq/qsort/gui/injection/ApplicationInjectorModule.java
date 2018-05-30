package com.github.deliberateq.qsort.gui.injection;

import com.github.deliberateq.qsort.gui.Configuration;
import com.github.deliberateq.qsort.gui.ConfigurationFromProperties;
import com.github.deliberateq.util.event.EventManager;
import com.google.inject.AbstractModule;

public class ApplicationInjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EventManager.class).toInstance(EventManager.getInstance());
		bind(Configuration.class).to(ConfigurationFromProperties.class);
	}

}
