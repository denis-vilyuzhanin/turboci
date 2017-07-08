package turboci.agent.jvm.instrumentation.asm;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import turboci.agent.jvm.instrumentation.CallbackDetails;
import turboci.agent.jvm.instrumentation.asm.samples.SimpleBean;
import turboci.agent.jvm.instrumentation.test.utils.ClassUtils;

@RunWith(JUnitPlatform.class)
public class AsmClassUsageInstrumentationTest {
	
	private CallbackDetails callbackDetails;
	private AsmClassUsageInstrumentation instrumentation;
	
	@BeforeEach
	public void createInstrumentation() {
		instrumentation = new AsmClassUsageInstrumentation();
		instrumentation.setDumpInstrumentedCodeToStdOut(true);
	}
	
	@BeforeEach
	public void createCallbackDetails() {
		callbackDetails = new CallbackDetails()
				              .setCallbackClassName(ThreadCallbackHandler.class.getName())
				              .setStatic(true)
				              .setMethodName("callback");
	}
	
	@BeforeEach
	public void resetCallbackHandler() {
		ThreadCallbackHandler.reset();
	}

	@Test
	public void handleMethodCall() throws Exception {
		//given
		byte[] byteCode = ClassUtils.loadClassByteCode(SimpleBean.class);
		//when
		byte[] instrumentedByteCode = instrumentation.instrument(byteCode, callbackDetails);
		//then
		Class<?> simpleBeanClass = ClassUtils.defineClass(SimpleBean.class.getName(), instrumentedByteCode);
		Method method = simpleBeanClass.getMethod("getValue");
		method.invoke(simpleBeanClass.newInstance());
		assertTrue(ThreadCallbackHandler.isInvoked());
	}
	
	
}
