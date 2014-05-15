package hal.dotsandboxes.prolog;

import java.io.File;

import org.apache.commons.lang.StringEscapeUtils;

public final class SwiPrologRunner 
	extends AbstractExternalProcessPrologRunner {

	public SwiPrologRunner(File executable) {
		super(executable);
	}

	@Override
	protected String getArguments(String goal, String prologFilePath) {
		
		return String.format("-q -s \"%s\" -g \"%s\"", 
				StringEscapeUtils.escapeJava(prologFilePath),
				StringEscapeUtils.escapeJava(goal));
	}

}
