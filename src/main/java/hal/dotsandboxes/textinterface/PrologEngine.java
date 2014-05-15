package hal.dotsandboxes.textinterface;

public enum PrologEngine {
	sicstus("sicstus"), swi("swipl");
	
	private final String mCmd;
	
	private PrologEngine(String cmd) {
		mCmd = cmd;
	}
	
	public String getCmd() {
		return mCmd;
	}
}
