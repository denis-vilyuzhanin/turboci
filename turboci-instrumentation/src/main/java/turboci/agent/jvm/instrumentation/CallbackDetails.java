package turboci.agent.jvm.instrumentation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CallbackDetails {

	private String callbackClassName;
	private String methodName;
	private List<Object> arguments = Collections.emptyList();
	
	public String getCallbackClassName() {
		return callbackClassName;
	}
	
	public CallbackDetails setCallbackClassName(String callbackClassName) {
		this.callbackClassName = callbackClassName;
		return this;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public CallbackDetails setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}
	
	public List<Object> getArguments() {
		return arguments;
	}
	
	public CallbackDetails setArguments(List<Object> arguments) {
		this.arguments = arguments;
		return this;
	}
	
	public CallbackDetails setArguments(Object...arguments) {
		return setArguments(Arrays.asList(arguments));
	}
}
