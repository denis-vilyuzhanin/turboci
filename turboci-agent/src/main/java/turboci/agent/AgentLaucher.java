package turboci.agent;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Guice;
import com.google.inject.Injector;

import turboci.agent.modules.InstrumentationModule;


public class AgentLaucher implements Runnable {

	private Path jvmAgentJarLocation;
	private Map<String, Object> parameters;
	private Instrumentation instrumentation;
	
	public AgentLaucher(Path jvmAgentJarLocation,
			            Map<String, Object> parameters, 
			            Instrumentation instrumentation) {
		this.jvmAgentJarLocation = jvmAgentJarLocation;
		this.parameters = parameters;
		this.instrumentation = instrumentation;
		
	}

	@Override
	public void run() {
		Guice.createInjector(new InstrumentationModule(instrumentation));
		System.out.println("TurboCI Agent has started");
	}
	
	
	
}
