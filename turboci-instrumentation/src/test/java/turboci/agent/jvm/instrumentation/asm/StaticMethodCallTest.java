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
	public void methodDesciption_voidWithoudParameters() {
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("()V", call.getDescription());
	}
	
	@Test
	public void methodDesciption_voidWithStringParameter() {
		//given
		callback.setArguments("value1");
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("(Ljava/lang/String;)V", call.getDescription());
	}
	
	@Test
	public void methodDesciption_voidWithIntParameter() {
		//given
		callback.setArguments(Integer.MAX_VALUE);
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("(I)V", call.getDescription());
	}
	
	@Test
	public void methodDesciption_voidWithLongParameter() {
		//given
		callback.setArguments(Long.MAX_VALUE);
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("(J)V", call.getDescription());
	}
	
	@Test
	public void methodDesciption_voidWithBooleanParameter() {
		//given
		callback.setArguments(Boolean.FALSE);
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("(Z)V", call.getDescription());
	}
	
	@Test
	public void methodDesciption_voidWithManyParameters() {
		//given
		callback.setArguments(Boolean.FALSE, Integer.MAX_VALUE, "value", Long.MAX_VALUE);
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertEquals("(ZILjava/lang/String;J)V", call.getDescription());
	}
	
	@Test
	public void methodArguments() {
		//given
		callback.setArguments(Boolean.FALSE, Integer.MAX_VALUE, "value", Long.MAX_VALUE);
		//when
		StaticMethodCall call = new StaticMethodCall(callback);	
		//then
		assertArrayEquals(new Object[] {Boolean.FALSE, Integer.MAX_VALUE, "value", Long.MAX_VALUE}, 
				          call.getArguments().toArray());

	}
}

