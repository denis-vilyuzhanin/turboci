package turboci.agent.jvm.instrumentation.asm;

import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import turboci.agent.jvm.instrumentation.CallbackCallArgumentValueGenerator;
import turboci.agent.jvm.instrumentation.CallbackDetails;
import turboci.agent.jvm.instrumentation.ClassUsageInstrumentation;

public class AsmClassUsageInstrumentation implements ClassUsageInstrumentation{

	private boolean dumpInstrumentedCodeToStdOut;
	
	@Override
	public byte[] instrument(byte[] originalCode, CallbackDetails callback) {
		StaticMethodCall callbackMethodCall = new StaticMethodCall(callback);
	    ClassReader cr = new ClassReader(originalCode);
	    ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
	    ClassVisitor finalVisitor = cw;
	    if (dumpInstrumentedCodeToStdOut) {
	    	PrintWriter pw = new PrintWriter(System.out);
	    	finalVisitor = new TraceClassVisitor(cw, pw);
	    }
	    cr.accept(new AddClassUsageCallbacksTransformation(finalVisitor, callbackMethodCall), 0);
        return cw.toByteArray();
	}

	
	static class AddClassUsageCallbacksTransformation extends ClassVisitor {

		private static final Pattern FIND_SLASH_PATTERN = Pattern.compile("[/]");
		private String className;
		private StaticMethodCall callbackMethodCall;
		
		public AddClassUsageCallbacksTransformation(ClassVisitor cv, StaticMethodCall callbackMethodCall) {
			super(Opcodes.ASM5, cv);
			this.callbackMethodCall = callbackMethodCall;
		}
		
		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			this.className = FIND_SLASH_PATTERN.matcher(name).replaceAll(".");
			super.visit(version, access, name, signature, superName, interfaces);
		}



		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			return new AddMethodCallbackMethodTransformer(className, name, callbackMethodCall, methodVisitor);
		}
		
	}
	
	static class AddMethodCallbackMethodTransformer extends MethodVisitor {

		private String className;
		private String methodName;
		private StaticMethodCall callbackMethodCall;
		
		public AddMethodCallbackMethodTransformer(String className,
				                                  String methodName, 
				                                  StaticMethodCall callbackMethodCall, 
				                                  MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
			this.callbackMethodCall = callbackMethodCall;
			this.methodName = methodName;
		}

		@Override
		public void visitCode() {
			Label usageCallbackLabel = new Label();
			mv.visitLabel(usageCallbackLabel);
			
			for(Object argument : callbackMethodCall.getArguments()) {
				Class<?> type = argument.getClass();
				if (CallbackCallArgumentValueGenerator.class.isAssignableFrom(type)) {
					CallbackCallArgumentValueGenerator generator = (CallbackCallArgumentValueGenerator) argument;
					argument = generator.generateNext(className, methodName);
					type = generator.getValueType();
				}
				if (Boolean.class.equals(argument.getClass())) {
					Boolean booleanValue = (Boolean) argument;
					mv.visitInsn(booleanValue ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
				} else {
					mv.visitLdcInsn(argument);
				}
			}
			
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
