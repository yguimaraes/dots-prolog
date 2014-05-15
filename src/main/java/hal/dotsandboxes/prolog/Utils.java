package hal.dotsandboxes.prolog;

import static com.google.common.io.Files.copy;
import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.newInputStreamSupplier;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import static com.google.common.base.Preconditions.*;

public final class Utils {
	private Utils() { throw new AssertionError(); }
	
	public static final URL DOTS_AND_BOXES_PROLOG_FILE = getResource(
			Utils.class,
			"/hal/dotsandboxes/decision/dots-and-boxes-minimax.pl");
	
	/**
	 * Extracts a resource to a temporary file.
	 * 
	 * @param resourceUrl The location of the resource to extract.
	 * @return A temporary file containing the extracted resource. The file is
	 *         set to be deleted when the JVM terminates (using 
	 *         {@link File#deleteOnExit()}).
	 *         
	 * @throws IOException
	 */
	public static File extractResourceToTempFile(URL resourceUrl) 
			throws IOException {
		File file = File.createTempFile("minimax-", ".pl");
		file.deleteOnExit();
		
		copy(newInputStreamSupplier(resourceUrl), file);
		return file;
	}
	
	/**
	 * Searches for a program on the system search path.
	 * 
	 * @param name The name of the program to search for. Should be a single 
	 *             word like "dir", "ls" etc.
	 * @return A File containing the absolute path to the program, or null if 
	 *         it's not found on the path. 
	 */
	public static File findProgramOnPath(String name) {
		checkNotNull(name);
		checkArgument(!name.contains(SystemUtils.FILE_SEPARATOR));
		
		// people trying to execute .com and other weird files are out of luck
		if(SystemUtils.IS_OS_WINDOWS && !name.endsWith(".exe"))
			name = name + ".exe";
		
		String[] paths = StringUtils.split(System.getenv("PATH"), 
				SystemUtils.PATH_SEPARATOR);
		
		for(String path : paths) {
			File program = new File(path, name);
			if(program.exists())
				return program;
		}
		
		return null;
	}
}
