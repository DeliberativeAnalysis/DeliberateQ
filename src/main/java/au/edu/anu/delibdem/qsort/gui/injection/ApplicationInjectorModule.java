package au.edu.anu.delibdem.qsort.gui.injection;

import com.google.inject.AbstractModule;

import au.edu.anu.delibdem.qsort.gui.Configuration;
import au.edu.anu.delibdem.qsort.gui.ConfigurationFromProperties;
import moten.david.util.event.EventManager;

public class ApplicationInjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EventManager.class).toInstance(EventManager.getInstance());
		bind(Configuration.class).to(ConfigurationFromProperties.class);
	}

}
