package turboci.agent.jvm.instrumentation.asm;

public class ThreadCallbackHandler {

	private static final ThreadLocal<Boolean> wasInvoked = ThreadLocal.withInitial(() -> Boolean.FALSE);
	
	public static void reset() {
		wasInvoked.set(false);
	}
	
	public static void callback() {
		wasInvoked.set(true);
	}
}
