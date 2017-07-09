package turboci.agent.jvm.instrumentation.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

import turboci.agent.jvm.instrumentation.CallbackCallArgumentValueGenerator;
import turboci.agent.jvm.instrumentation.CallbackDetails;

class StaticMethodCall {
	
	private static final Map<String, String> TYPES = 
			ImmutableMap.of("java.lang.Void", "V",
					        "java.lang.String", "Ljava/lang/String;",
					        "java.lang.Integer", "I",
					        "java.lang.Long", "J",
					        "java.lang.Boolean", "Z");

	private static final Pattern CONVERT_CLASS_NAME_PATTERN = Pattern.compile("[.]");
	private String className;
	private String methodName;
	private String description;
	private List<Object> arguments;
	
	public StaticMethodCall(CallbackDetails callback) {
		this.className = CONVERT_CLASS_NAME_PATTERN.matcher(callback.getCallbackClassName())
				                                   .replaceAll("/");
		this.methodName = callback.getMethodName();
		this.description = createDescription(callback);
		this.arguments = Collections.unmodifiableList(new ArrayList<>(callback.getArguments()));
	}
	
	private String createDescription(CallbackDetails callback) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for(Object argument : callback.getArguments()) {
			Class<?> argumentType = determinArgumentType(argument);
			String typeDescription = TYPES.get(argumentType.getName());
			if (typeDescription == null) {
				throw new IllegalArgumentException("Unsupported argument type: " + typeDescription);
			}
			builder.append(typeDescription);
		}
		builder.append(")");
		
		builder.append("V");
		return builder.toString();
	}

	private Class<?> determinArgumentType(Object argument) {
		Class<?> argumentType = argument.getClass();
		if (argument instanceof CallbackCallArgumentValueGenerator) {
			argumentType = ((CallbackCallArgumentValueGenerator) argument).getValueType();
		}
		return argumentType;
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

	public List<Object> getArguments() {
		return arguments;
	}
	
	
	
}
