package turboci.agent.jvm.instrumentation.asm.samples;

import turboci.agent.jvm.instrumentation.asm.ThreadCallbackHandler;

public class SimpleBean {

	private String value;

	public String getValue() {
		//ThreadCallbackHandler.callback();
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
