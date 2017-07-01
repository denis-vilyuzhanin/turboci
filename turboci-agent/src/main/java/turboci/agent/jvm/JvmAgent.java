package turboci.agent.jvm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import turboci.agent.CodeUsageAgentContextClassLoader;
import turboci.agent.JavaAgentApplication;

public class JvmAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.print("TurboCI JVM Agent ....");
		try {

			String jarLocation = discoverThisClassJarLocation(JvmAgent.class);
			CodeUsageAgentContextClassLoader contextClassLoader = new CodeUsageAgentContextClassLoader(
					JavaAgentApplication.class.getClassLoader(), jarLocation);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	/**
	 * This code is taken from <a href=
	 * "https://stackoverflow.com/questions/1983839/determine-which-jar-file-a-class-is-from">stackoverflow.com</a>.
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	private static String discoverThisClassJarLocation(Class<?> context) throws IllegalStateException {

		String classFileName = toClassFileName(context.getName());

		String uri = context.getResource(classFileName).toString();

		if (uri.startsWith("file:")) {
			throw new IllegalStateException("This class has been loaded from a directory and not from a jar file.");
		}

		if (!uri.startsWith("jar:file:")) {
			int idx = uri.indexOf(':');
			String protocol = idx == -1 ? "(unknown)" : uri.substring(0, idx);
			throw new IllegalStateException("This class has been loaded remotely via the " + protocol
					+ " protocol. Only loading from a jar on the local file system is supported.");
		}
		System.out.println("[" + uri + "]");
		int idx = uri.indexOf('!');
		// As far as I know, the if statement below can't ever trigger, so it's
		// more of a sanity check thing.
		if (idx == -1) {
			throw new IllegalStateException(
					"You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");
		}

		try {
			String fileName = URLDecoder.decode(uri.substring("jar:file:".length(), idx),
					Charset.defaultCharset().name());
			return new File(fileName).getAbsolutePath();
		} catch (UnsupportedEncodingException e) {
			throw new InternalError("default charset doesn't exist. Your VM is borked.");
		}
	}

	/**
	 * rawName is something like package.name.ContainingClass$ClassName. We need
	 * to turn this into ContainingClass$ClassName.class.
	 */
	private static String toClassFileName(String rawName) {
		String classFileName;

		int idx = rawName.lastIndexOf('.');
		classFileName = (idx == -1 ? rawName : rawName.substring(idx + 1)) + ".class";

		return classFileName;
	}

}
