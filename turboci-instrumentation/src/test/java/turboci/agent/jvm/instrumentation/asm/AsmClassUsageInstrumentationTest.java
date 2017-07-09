package turboci.agent.jvm.instrumentation.asm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import turboci.agent.jvm.instrumentation.CallbackDetails;
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
				              .setMethodName("callback");
	}
	
	@BeforeEach
	public void resetCallbackHandler() {
		ThreadCallbackHandler.reset();
	}

	@Test
	public void instrumentingClassInitializers() throws Exception {
		//given
		byte[] byteCode = ClassUtils.loadClassByteCode(AnyClass.class);
		//when
		byte[] instrumentedByteCode = instrumentation.instrument(byteCode, callbackDetails);
		//then
		assertThatClassInitializerIsInstrumented(instrumentedByteCode);
 	}

	
	@Test
	public void instrumentingConstructors() throws Exception {
		//given
		byte[] byteCode = ClassUtils.loadClassByteCode(AnyClass.class);
		//when
		byte[] instrumentedByteCode = instrumentation.instrument(byteCode, callbackDetails);
		//then
		Class<?> simpleBeanClass = ClassUtils.defineClass(AnyClass.class.getName(), instrumentedByteCode);
		assertThatConstructorIsInstrumented(simpleBeanClass);
 	}
	
	@Test
	public void instrumentingAllMethods() throws Exception {
		//given
		byte[] byteCode = ClassUtils.loadClassByteCode(AnyClass.class);
		//when
		byte[] instrumentedByteCode = instrumentation.instrument(byteCode, callbackDetails);
		//then
		Class<?> simpleBeanClass = ClassUtils.defineClass(AnyClass.class.getName(), instrumentedByteCode);
		assertThatConstructorIsInstrumented(simpleBeanClass);
		assertThatAllMethodsAreInstrumented(simpleBeanClass);
	}

	private void assertThatConstructorIsInstrumented(Class<?> simpleBeanClass)
			throws InstantiationException, IllegalAccessException {
		ThreadCallbackHandler.reset();
		Object object = simpleBeanClass.newInstance();
		assertTrue(ThreadCallbackHandler.isInvoked(), "Constructor isn't instrumented");
	}

	private void assertThatAllMethodsAreInstrumented(Class<?> instrumentedClass) throws Exception {
		Object object = instrumentedClass.newInstance();
		for(Method method : instrumentedClass.getDeclaredMethods()) {
			ThreadCallbackHandler.reset();
			method.invoke(object);
			assertTrue(ThreadCallbackHandler.isInvoked(), "Method " + method.getName() + " isn't instrumented");
		}
	}
	
	private void assertThatClassInitializerIsInstrumented(byte[] instrumentedByteCode) throws Exception {
		ThreadCallbackHandler.reset();
		Class<?> instrumentedClass = ClassUtils.defineClass(AnyClass.class.getName(), instrumentedByteCode);
		//Trigger class initialization because loadClass
		instrumentedClass.getDeclaredField("STATIC_FIELD").get(null);
		assertTrue(ThreadCallbackHandler.isInvoked(), "Class initializer isn't instrumented");
	}
	
	public static class AnyClass {

		public static int STATIC_FIELD;
		static {
			STATIC_FIELD = 10;
		}
		
		private String value;

		public String method1() {
			//ThreadCallbackHandler.callback();
			return value;
		}
		
		public void method2() {
		}
		
	}

		
}
