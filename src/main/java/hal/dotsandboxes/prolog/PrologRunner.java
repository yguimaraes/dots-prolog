package hal.dotsandboxes.prolog;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

import org.apache.commons.lang.StringEscapeUtils;

public final class PrologRunner {
    
        private final File mExecutable;

	public PrologRunner(File executable) {
		mExecutable = checkNotNull(executable);
	}
        
        public String execute(String goal, String prologFilePath) 
		throws PrologException {

		DefaultExecutor exec = new DefaultExecutor();
                
                goal=goal.replaceAll("\\s","");

		String cmd = String.format("\"%s\" %s",
				mExecutable.getAbsolutePath(),
				getArguments(goal, prologFilePath));

                //cmd = "\"/usr/bin/swipl\" -q -s \"/home/yago/Desktop/teste.pl\" -g \"main(4,4,[edge(2,2,down)-p1],2,[p1,p2])\"";
                
		CommandLine cmdLine = CommandLine.parse(cmd);		

		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();

		exec.setStreamHandler(new PumpStreamHandler(stdout, stderr));
		try {
			exec.execute(cmdLine);
		} 
		catch (ExecuteException e) {
			throw new PrologException("Error running prolog process:\n" + 
					stderr.toString(),
					e);
		}
		catch (IOException e) {
			throw new PrologException("IO Error occoured when running process.",
					e);
		}

		return stdout.toString();
	}
        
	protected String getArguments(String goal, String prologFilePath) {
		
		return String.format("-q -s \"%s\" -g \"%s\"", 
				StringEscapeUtils.escapeJava(prologFilePath),
				StringEscapeUtils.escapeJava(goal));
	}
        
        public static class PrologException extends Exception {
		private static final long serialVersionUID = 1L;

		public PrologException() {
			super();
		}

		public PrologException(String message, Throwable cause) {
			super(message, cause);
		}

		public PrologException(String message) {
			super(message);
		}

		public PrologException(Throwable cause) {
			super(cause);
		}

	}

}
