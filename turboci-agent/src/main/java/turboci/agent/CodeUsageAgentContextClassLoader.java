package turboci.agent;

public class CodeUsageAgentContextClassLoader extends ClassLoader {

	private String jarLocation;
	
	public CodeUsageAgentContextClassLoader(ClassLoader parent, String jarLocation) {
		super(parent);

		this.jarLocation = jarLocation;
		System.out.println("Jar location: " + jarLocation);
	}

	
}
