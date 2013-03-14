package au.edu.anu.delibdem.qsort.gui.injection;

import moten.david.util.event.EventManager;
import au.edu.anu.delibdem.qsort.gui.Configuration;
import au.edu.anu.delibdem.qsort.gui.ConfigurationFromProperties;
import au.edu.anu.delibdem.qsort.gui.MainFrame;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ApplicationInjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EventManager.class).toInstance(EventManager.getInstance());
		bind(Configuration.class).to(ConfigurationFromProperties.class).in(
				Scopes.SINGLETON);
		bind(MainFrame.class).in(Scopes.SINGLETON);
	}

}
