package turboci.agent.jvm.instrumentation.asm;

import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import turboci.agent.jvm.instrumentation.CallbackDetails;
import turboci.agent.jvm.instrumentation.ClassUsageInstrumentation;

public class AsmClassUsageInstrumentation implements ClassUsageInstrumentation{

	private boolean dumpInstrumentedCodeToStdOut;
	
	@Override
	public byte[] instrument(byte[] originalCode, CallbackDetails callback) {
		StaticMethodCall callbackMethodCall = new StaticMethodCall(callback);
	    ClassReader cr = new ClassReader(originalCode);
	    ClassWriter cw = new ClassWriter(cr, 0);
	    ClassVisitor finalVisitor = cw;
	    if (dumpInstrumentedCodeToStdOut) {
	    	PrintWriter pw = new PrintWriter(System.out);
	    	finalVisitor = new TraceClassVisitor(cw, pw);
	    }
	    cr.accept(new AddClassUsageCallbacksTransformation(finalVisitor, callbackMethodCall), 0);
        return cw.toByteArray();
	}

	
	static class AddClassUsageCallbacksTransformation extends ClassVisitor {

		private StaticMethodCall callbackMethodCall;
		
		public AddClassUsageCallbacksTransformation(ClassVisitor cv, StaticMethodCall callbackMethodCall) {
			super(Opcodes.ASM5, cv);
			this.callbackMethodCall = callbackMethodCall;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return new AddMethodCallbackMethodTransformer(callbackMethodCall, methodVisitor);
		}
		
	}
	
	static class AddMethodCallbackMethodTransformer extends MethodVisitor {

		private StaticMethodCall callbackMethodCall;
		
		public AddMethodCallbackMethodTransformer(StaticMethodCall callbackMethodCall, MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
			this.callbackMethodCall = callbackMethodCall;
		}

		@Override
		public void visitCode() {
			Label usageCallbackLabel = new Label();
			mv.visitLabel(usageCallbackLabel);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
					           callbackMethodCall.getClassName(), 
					           callbackMethodCall.getMethodName(), 
					           callbackMethodCall.getDescription(), 
					           false);
			super.visitCode();
		}
	}

	public boolean isDumpInstrumentedCodeToStdOut() {
		return dumpInstrumentedCodeToStdOut;
	}

	public void setDumpInstrumentedCodeToStdOut(boolean dumpInstrumentedCodeToStdOut) {
		this.dumpInstrumentedCodeToStdOut = dumpInstrumentedCodeToStdOut;
	}
	
	
}
