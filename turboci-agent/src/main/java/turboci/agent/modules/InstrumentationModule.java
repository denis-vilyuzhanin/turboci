package turboci.agent.modules;

import java.lang.instrument.Instrumentation;

import com.google.inject.AbstractModule;

public class InstrumentationModule  extends AbstractModule {

	private Instrumentation instrumentation;
	
	
	public InstrumentationModule(Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	@Override
	protected void configure() {
		
		
	}

}
