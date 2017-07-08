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
	    ClassReader cr = new ClassReader(originalCode);
	    ClassWriter cw = new ClassWriter(cr, 0);
	    ClassVisitor finalVisitor = cw;
	    if (dumpInstrumentedCodeToStdOut) {
	    	PrintWriter pw = new PrintWriter(System.out);
	    	finalVisitor = new TraceClassVisitor(cw, pw);
	    }
	    cr.accept(new AddClassUsageCallbacksTransformation(finalVisitor, callback), 0);
        return cw.toByteArray();
	}

	
	static class AddClassUsageCallbacksTransformation extends ClassVisitor {

		private CallbackDetails callbackDetails;
		
		public AddClassUsageCallbacksTransformation(ClassVisitor cv, CallbackDetails callbackDetails) {
			super(Opcodes.ASM5, cv);
			this.callbackDetails = callbackDetails;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return new AddMethodCallbackMethodTransformer(callbackDetails, methodVisitor);
		}
		
	}
	
	static class AddMethodCallbackMethodTransformer extends MethodVisitor {

		private CallbackDetails callbackDetails;
		
		public AddMethodCallbackMethodTransformer(CallbackDetails callbackDetails, MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
			this.callbackDetails = callbackDetails;
		}

		@Override
		public void visitCode() {
			Label usageCallbackLabel = new Label();
			mv.visitLabel(usageCallbackLabel);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
					           "turboci/agent/jvm/instrumentation/asm/ThreadCallbackHandler", 
					           "callback", 
					           "()V", 
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
