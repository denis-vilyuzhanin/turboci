package turboci.agent.jvm.instrumentation;

public interface CallbackCallArgumentValueGenerator {

	Object generateNext(String className, String methodName);
	
	Class<?> getValueType();
}
