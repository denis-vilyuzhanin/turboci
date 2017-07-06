package turboci.agent.jvm.instrumentation.asm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import turboci.agent.jvm.instrumentation.CallbackDetails;

public class AsmClassUsageInstrumentationTest {

	private CallbackDetails callbackDetails;
	private AsmClassUsageInstrumentation instrumentation;
	
	@BeforeEach
	public void createInstrumentation() {
		instrumentation = new AsmClassUsageInstrumentation();
	}
	
	@BeforeEach
	public void createCallbackDetails() {
		callbackDetails = new CallbackDetails();
	}
	
	@BeforeEach
	public void resetCallbackHandler() {
		ThreadCallbackHandler.reset();
	}

	@Test
	public void handleMethodCall() {

	}
}
