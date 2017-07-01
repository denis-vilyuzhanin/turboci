package turboci.agent;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.Map;

public class AgentLaucher implements Runnable {

	public AgentLaucher(Path jvmAgentJarLocation,
			            Map<String, Object> parameters, 
			            Instrumentation instrumentation) {
		
	}

	@Override
	public void run() {
		System.out.println("\tTurboCI agent has started\n");
	}
	
	
	
}
