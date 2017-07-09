package turboci.agent.jvm.instrumentation.asm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import turboci.agent.jvm.instrumentation.CallbackDetails;

@RunWith(JUnitPlatform.class)
public class StaticMethodCallTest {

	CallbackDetails callback;
	
	@BeforeEach
	public void givenCallbackDetails() {
		callback = new CallbackDetails()
				      .setCallbackClassName("turboci.test.AnyClassName")
				      .setMethodName("anyMethodName");
	}
	
	@Test
	public void convertClassName() {
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("turboci/test/AnyClassName", call.getClassName());
	}
	
	@Test
	public void methodName() {
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("anyMethodName", call.getMethodName());
	}
	
	@Test
	public void MethodDesciption_voidWithoudParameters() {
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("()V", call.getDescription());
	}
}
