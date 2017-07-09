package turboci.agent.jvm.instrumentation;

import java.util.ArrayList;
import java.util.List;

public class MethodIdValueGenerator implements CallbackCallArgumentValueGenerator {

	private static final Class<?> TYPE = Integer.class;
	
	private List<String> methods = new ArrayList<>();
	
	@Override
	public Object generateNext(String className, String methodName) {
		int index = methods.size();
		methods.add(methodName);
		return Integer.valueOf(index);
	}

	@Override
	public Class<?> getValueType() {
		return TYPE;
	}

	public List<String> getMethods() {
		return methods;
	}
	

	
}
