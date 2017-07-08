package turboci.agent.jvm.instrumentation.test.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicClassLoader extends ClassLoader {

	private Map<String, byte[]> bytecodes = new ConcurrentHashMap<>();
	
	public DynamicClassLoader(Map<String, byte[]> bytecodes) {
		this.bytecodes.putAll(bytecodes);
	}
	
	public DynamicClassLoader(ClassLoader parent, Map<String, byte[]> bytecodes) {
		super(parent);
		this.bytecodes.putAll(bytecodes);
	}
	
	public DynamicClassLoader() {
	}

	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public void addClass(String name, byte[] code) {
		bytecodes.putIfAbsent(name, code);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] code = this.bytecodes.remove(name);
		if (code != null) {
			return defineClass(name, code, 0, code.length);
		}
		return super.findClass(name);
	}

	
	
}
