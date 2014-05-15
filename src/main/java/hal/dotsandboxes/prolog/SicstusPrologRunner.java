package hal.dotsandboxes.prolog;

import java.io.File;

import org.apache.commons.lang.StringEscapeUtils;

public final class SicstusPrologRunner 
	extends AbstractExternalProcessPrologRunner {

	public SicstusPrologRunner(File executable) {
		super(executable);
	}

	@Override
	protected String getArguments(String goal, String prologFilePath) {
		
		return String.format(" -l \"%s\" --goal \"%s.\"", 
				prologFilePath,
				StringEscapeUtils.escapeJava(goal));
	}

}
