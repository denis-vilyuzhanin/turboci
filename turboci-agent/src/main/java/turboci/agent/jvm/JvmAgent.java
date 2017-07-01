package turboci.agent.jvm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

public class JvmAgent {
	
	private static final Logger LOG = Logger.getLogger(JvmAgent.class.getName());
	
	public static void premain(String agentArgs, Instrumentation instrumentation) {
		LOG.setLevel(Level.INFO);
		LOG.info("TurboCI JVM Agent");
		try {
			Path jarLocationPath = discoverThisClassJarLocation(JvmAgent.class);
			URL[] classpath = discoverAgentClasspath(jarLocationPath);
			
			//should we really close this loader ???
			URLClassLoader agentClassLoader = new URLClassLoader(classpath, JvmAgent.class.getClassLoader());
			
			Runnable laucher = createLaucherInstance(instrumentation, jarLocationPath, agentClassLoader);
			laucher.run();
		} catch (Throwable e) {
			LOG.log(Level.SEVERE, "TurboCI JVM agent failed", e); 
		}

	}

	private static Runnable createLaucherInstance(Instrumentation instrumentation, Path jarLocationPath,
			URLClassLoader agentClassLoader) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> laucherClass =  agentClassLoader.loadClass("turboci.agent.AgentLaucher");
		Runnable laucher = (Runnable) laucherClass.getConstructor(Path.class,
				                                                  Map.class, 
				                                                  Instrumentation.class)
		                                          .newInstance(jarLocationPath, 
		                                        		       Collections.emptyMap(), 
		                                        		       instrumentation);
		return laucher;
	}

	private static URL[] discoverAgentClasspath(Path jarLocationPath) throws IOException {
		Path turbociLibsPath = jarLocationPath.getParent();
		LOG.info(() -> "Libraries Folder: " + turbociLibsPath);
		
		URL[] classpath = buildAgentClasspath(turbociLibsPath, jarLocationPath);
		LOG.info(() -> "Classpath: " + Arrays.toString(classpath));
		return classpath;
	}
	
	private static URL[] buildAgentClasspath(Path librariesPath, Path jarLocationPath) throws IOException {
		List<URL> urls = new ArrayList<>();
		try (DirectoryStream<Path> jars = Files.newDirectoryStream(librariesPath, "*.jar")) {
			StreamSupport.stream(jars.spliterator(), false)
			             .filter(jarPath -> !jarPath.equals(jarLocationPath))
			             .map(JvmAgent::toURL)
			             .forEach(urls::add);
			
		}
		return urls.toArray(new URL[urls.size()]);
	}
	
	private static URL toURL(Path path) {
		try {
			return path.toUri().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This code is taken from <a href=
	 * "https://stackoverflow.com/questions/1983839/determine-which-jar-file-a-class-is-from">stackoverflow.com</a>.
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	private static Path discoverThisClassJarLocation(Class<?> context) throws IllegalStateException {

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
		
		int idx = uri.indexOf('!');
		// As far as I know, the if statement below can't ever trigger, so it's
		// more of a sanity check thing.
		if (idx == -1) {
			throw new IllegalStateException(
					"You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");
		}

		try {
			String fileName = URLDecoder.decode(uri.substring("jar:file:/".length(), idx),
					Charset.defaultCharset().name());
			Path jarLocation = Paths.get(fileName).toAbsolutePath();
			LOG.info(()->"JVM Agent jar location: " + jarLocation);
			return jarLocation;
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
