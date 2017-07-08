package turboci.agent.jvm.instrumentation.test.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.ImmutableMap;

public class ClassUtils {

	
	public static byte[] loadClassByteCode(Class<?> clazz) {
		try (InputStream input = clazz.getResourceAsStream(clazz.getSimpleName() + ".class")) {
			return IOUtils.toByteArray(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Class<?> defineClass(String name, byte[] bytecode) {
		DynamicClassLoader classLoader = new DynamicClassLoader(ImmutableMap.of(name, bytecode));
		try {
			return classLoader.findClass(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	

}
