package turboci.agent.jvm.instrumentation.asm;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import turboci.agent.jvm.instrumentation.CallbackCallArgumentValueGenerator;
import turboci.agent.jvm.instrumentation.CallbackDetails;
import turboci.agent.jvm.instrumentation.test.utils.ClassUtils;

@RunWith(JUnitPlatform.class)
public class AsmClassUsageInstrumentationTest {

	@Mock
	CallbackCallArgumentValueGenerator argumentsGenerator;
	
	private CallbackDetails callbackDetails;
	private AsmClassUsageInstrumentation instrumentation;

	byte[] instrumentedByteCode;

	Class<?> instrumentedClass;

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	@BeforeEach
	public void createInstrumentation() {
		instrumentation = new AsmClassUsageInstrumentation();
		//instrumentation.setDumpInstrumentedCodeToStdOut(true);

	}

	@BeforeEach
	public void resetCallbackHandler() {
		ThreadCallbackHandler.reset();
	}

	@Nested
	class CallbackWithoutArguments {
		
		@BeforeEach
		public void whenCodeIsInstrumented() {
			//init callback details
			callbackDetails = new CallbackDetails().setCallbackClassName(ThreadCallbackHandler.class.getName())
					.setMethodName("callback");
			
			byte[] byteCode = ClassUtils.loadClassByteCode(AnyClass.class);
			//instrument the code
			instrumentedByteCode = instrumentation.instrument(byteCode, callbackDetails);
			//load instrumented class
			instrumentedClass = ClassUtils.defineClass(AnyClass.class.getName(), instrumentedByteCode);
		}

		@Test
		public void instrumentingClassInitializers() throws Exception {
			assertThatClassInitializerIsInstrumented(instrumentedByteCode);
		}

		@Test
		public void instrumentingConstructors() throws Exception {
			assertThatConstructorIsInstrumented(instrumentedClass);
		}

		@Test
		public void instrumentingAllMethods() throws Exception {
			assertThatConstructorIsInstrumented(instrumentedClass);
			assertThatAllMethodsAreInstrumented(instrumentedClass);
		}

	}

	@Nested
	class CallbackWithArguments {

		@BeforeEach
		public void whenCodeIsInstrumented() {
			//init
			callbackDetails = new CallbackDetails()
					.setCallbackClassName(ThreadCallbackHandler.class.getName())
					.setMethodName("callbackWithArguments")
					.setArguments(Arrays.asList(new Long(10), 
							                    new Integer(20), 
							                    new String("stringValue"), 
							                    Boolean.TRUE));
			
			byte[] byteCode = ClassUtils.loadClassByteCode(AnyClass.class);
			//instrument
			instrumentedByteCode = instrumentation.instrument(byteCode, callbackDetails);
			//load instrumented class
			instrumentedClass = ClassUtils.defineClass(AnyClass.class.getName(), instrumentedByteCode);
		}
		
		@Test
		public void instrumentingConstructors() throws Exception {
			assertThatConstructorIsInstrumented(instrumentedClass, 
					"long:10", "int:20", "String:stringValue", "boolean:true");
		}
		
	}
	
	@Nested
	class WithGeneratedArguments {
		
		@BeforeEach
		public void whenCodeIsInstrumentedWithGeneratedArgument() {
			Mockito.when(argumentsGenerator.getValueType()).thenReturn((Class)String.class);
			Mockito.when(argumentsGenerator.generateNext(anyString(), eq("<clinit>")))
			       .thenReturn("<clinit>");
			Mockito.when(argumentsGenerator.generateNext(anyString(), eq("<init>")))
		       .thenReturn("<init>");
			Mockito.when(argumentsGenerator.generateNext(anyString(), eq("method1")))
		       .thenReturn("method1");
			Mockito.when(argumentsGenerator.generateNext(anyString(), eq("method2")))
		       .thenReturn("method2");
			
			callbackDetails = new CallbackDetails()
					.setCallbackClassName(ThreadCallbackHandler.class.getName())
					.setMethodName("callbackWithArgument")
					.setArguments(argumentsGenerator);
			
			byte[] byteCode = ClassUtils.loadClassByteCode(AnyClass.class);
			//instrument
			instrumentedByteCode = instrumentation.instrument(byteCode, callbackDetails);
			//load instrumented class
			instrumentedClass = ClassUtils.defineClass(AnyClass.class.getName(), instrumentedByteCode);
		}
		
		@Test
		public void allCallbacksAreMadeWithGeneratedValues() throws Exception {
			assertThatMethodIsInstrumented(instrumentedClass, "method1", "String:method1");
			assertThatMethodIsInstrumented(instrumentedClass, "method2", "String:method2");
		}
		
		@Test
		public void instrumentingConstructorWithGeneratedValue() throws Exception {
			assertThatConstructorIsInstrumented(instrumentedClass, "String:<init>");
		}
	}

	private void assertThatConstructorIsInstrumented(Class<?> simpleBeanClass, String...expectedArguments)
			throws InstantiationException, IllegalAccessException {
		ThreadCallbackHandler.reset();
		Object object = simpleBeanClass.newInstance();
		assertTrue(ThreadCallbackHandler.isInvoked(), "Constructor isn't instrumented");
		assertCllbackWasMadeWithArguments(expectedArguments);
	}

	private void assertCllbackWasMadeWithArguments(String... expectedArguments) {
		if(expectedArguments.length > 0) {
			List<String> actualArguments = ThreadCallbackHandler.getArguments();
			assertArrayEquals(expectedArguments, actualArguments.toArray());
		} else {
			assertTrue(ThreadCallbackHandler.getArguments().isEmpty());
		}
	}

	private void assertThatAllMethodsAreInstrumented(Class<?> instrumentedClass) throws Exception {
		Object object = instrumentedClass.newInstance();
		for (Method method : instrumentedClass.getDeclaredMethods()) {
			ThreadCallbackHandler.reset();
			method.invoke(object);
			assertTrue(ThreadCallbackHandler.isInvoked(), "Method " + method.getName() + " isn't instrumented");
			
		}
	}
	
	private void assertThatMethodIsInstrumented(Class<?> instrumentedClass, String methodName, String... expectedArguments) throws Exception {
		Object object = instrumentedClass.newInstance();
		Method method = instrumentedClass.getDeclaredMethod(methodName);
		ThreadCallbackHandler.reset();
		method.invoke(object);
		assertTrue(ThreadCallbackHandler.isInvoked(), "Method " + method.getName() + " isn't instrumented");
		assertCllbackWasMadeWithArguments(expectedArguments);
	}

	private void assertThatClassInitializerIsInstrumented(byte[] instrumentedByteCode, String...expectedArguments) throws Exception {
		ThreadCallbackHandler.reset();
		Class<?> instrumentedClass = ClassUtils.defineClass(AnyClass.class.getName(), instrumentedByteCode);
		// Trigger class initialization because loadClass
		instrumentedClass.getDeclaredField("STATIC_FIELD").get(null);
		assertTrue(ThreadCallbackHandler.isInvoked(), "Class initializer isn't instrumented");
		assertCllbackWasMadeWithArguments(expectedArguments);
	}

	public static class AnyClass {

		public static int STATIC_FIELD;
		static {
			STATIC_FIELD = 10;
 		}

		private String value;

		public String method1() {
			return value;
		}

		public void method2() {
		}

	}

	public static class ThreadCallbackHandler {

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

		public static void callbackWithArguments(long l, int i, String str, boolean b) {
			isInvoked.set(true);
			arguments.set(Arrays.asList("long:" + l,
					                    "int:" + i,
					                    "String:" + str,
					                    "boolean:" + b));
		}
		public static void callbackWithArgument(String str) {
			isInvoked.set(true);
			arguments.set(Arrays.asList("String:" + str));
		}

		public static boolean isInvoked() {
			return isInvoked.get();
		}
		
		public static List<String> getArguments() {
			return arguments.get();
		}
	}

}
