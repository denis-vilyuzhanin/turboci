package turboci.agent.jvm.instrumentation.asm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ThreadCallbackHandler {

	private static final ThreadLocal<Boolean> isInvoked = ThreadLocal.withInitial(() -> Boolean.FALSE);
	private static final ThreadLocal<List<String>> arguments = ThreadLocal.withInitial(() -> null);
	
	public static void reset() {
		isInvoked.set(false);
		arguments.set(null);
	}
	
	public static void callback() {
		isInvoked.set(true);
		arguments.set(Collections.emptyList());
	}
	
	public static void callbackWithArguments(String str) {
		isInvoked.set(true);
		arguments.set(Arrays.asList("String:" + str));
	}
	
	public static boolean isInvoked() {
		return isInvoked.get();
	}
}
