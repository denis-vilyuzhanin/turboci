package turboci.agent.jvm.instrumentation;

import java.util.List;

public class CallbackDetails {

	private String callbackClassName;
	private String methodName;
	private boolean isStatic;
	private List<Object> arguments;
	
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
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public CallbackDetails setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		return this;
	}
	
	public List<Object> getArguments() {
		return arguments;
	}
	public CallbackDetails setArguments(List<Object> arguments) {
		this.arguments = arguments;
		return this;
	}
	
	
}
