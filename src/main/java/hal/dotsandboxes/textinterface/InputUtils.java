package hal.dotsandboxes.textinterface;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

public final class InputUtils {
	private InputUtils() { throw new AssertionError(); }
	
	public static String readLine(InputStream stream) {
		checkNotNull(stream);
		final BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		try {
			return reader.readLine();
		} 
		catch (IOException e) {
			System.err.println("Error: Failed to read input.");
			System.exit(2);
			throw new AssertionError();
		}
	}
	
	public static boolean askYesNoQuestion(InputStream is, boolean defaultResponse) {
		String answer = readLine(is);
		
		if(StringUtils.isEmpty(answer))
			return defaultResponse;
		return answer.startsWith("y");
	}
}
