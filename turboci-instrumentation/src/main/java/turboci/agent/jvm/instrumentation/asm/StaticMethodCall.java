package turboci.agent.jvm.instrumentation.asm;

import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

import turboci.agent.jvm.instrumentation.CallbackDetails;

class StaticMethodCall {
	
	private static final Map<String, String> TYPES = 
			ImmutableMap.of("java.lang.Void", "V");

	private static final Pattern CONVERT_CLASS_NAME_PATTERN = Pattern.compile("[.]");
	private String className;
	private String methodName;
	private String description;
	
	
	public StaticMethodCall(CallbackDetails callback) {
		this.className = CONVERT_CLASS_NAME_PATTERN.matcher(callback.getCallbackClassName())
				                                   .replaceAll("/");
		this.methodName = callback.getMethodName();
		this.description = createDescription(callback);
	}
	
	private String createDescription(CallbackDetails callback) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(")");
		
		builder.append("V");
		return builder.toString();
	}


	public String getClassName() {
		return className;
	}


	public String getMethodName() {
		return methodName;
	}


	public String getDescription() {
		return description;
	}
	
	
}
