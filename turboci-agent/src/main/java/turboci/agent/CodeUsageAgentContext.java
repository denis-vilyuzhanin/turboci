package turboci.agent;

import java.lang.instrument.Instrumentation;

public class CodeUsageAgentContext {

	public void init(String agentArgs, Instrumentation inst) {
		System.out.println("TurboCI Java Agent is running");
	}
}
