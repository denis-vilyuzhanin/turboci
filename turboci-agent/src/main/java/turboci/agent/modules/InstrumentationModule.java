package turboci.agent.modules;

import java.lang.instrument.Instrumentation;

import com.google.inject.AbstractModule;

public class InstrumentationModule  extends AbstractModule {

	private ClassLoader rootClassLoader;
	private Instrumentation instrumentation;
	
	
	public InstrumentationModule(ClassLoader rootClassLoader, Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	@Override
	protected void configure() {
		
		
	}

}
