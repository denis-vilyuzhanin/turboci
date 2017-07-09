package turboci.agent.jvm.instrumentation.asm;

public class ThreadCallbackHandler {

	private static final ThreadLocal<Boolean> isInvoked = ThreadLocal.withInitial(() -> Boolean.FALSE);
	
	public static void reset() {
		isInvoked.set(false);
	}
	
	public static void callback() {
		System.out.println("Callback: "+ Thread.currentThread().toString());
		isInvoked.set(true);
	}
	
	public static boolean isInvoked() {
		return isInvoked.get();
	}
}
