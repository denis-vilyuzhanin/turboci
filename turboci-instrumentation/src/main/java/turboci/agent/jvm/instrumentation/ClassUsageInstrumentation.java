package turboci.agent.jvm.instrumentation;

public interface ClassUsageInstrumentation {

	byte[] instrument(byte[] originalCode, CallbackDetails callback);
}
