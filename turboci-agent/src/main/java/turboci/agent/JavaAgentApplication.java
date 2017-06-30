package turboci.agent;

import java.lang.instrument.Instrumentation;

public class JavaAgentApplication {

	private JavaAgentApplication() {
	}
	
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("TurboCI Java Agent is running");
	}
}
