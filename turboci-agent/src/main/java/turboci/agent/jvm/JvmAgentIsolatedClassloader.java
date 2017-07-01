package turboci.agent.jvm;

import java.net.URL;
import java.net.URLClassLoader;

public class JvmAgentIsolatedClassloader extends URLClassLoader {

	public JvmAgentIsolatedClassloader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			// First, check if the class has already been loaded
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				if (c == null) {
					try {
						c = findClass(name);
					} catch (ClassNotFoundException e) {
					}
				}
				if (c == null && getParent() != null) {
					c = getParent().loadClass(name);
				}
			}
			if (resolve) {
				resolveClass(c);
			}
			return c;
		}
	}

}
