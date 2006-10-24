package pcgen.util.enumeration;

public enum DefaultTriState {
	YES { public boolean booleanValue() { return true;  } },
	
	NO  { public boolean booleanValue() { return false; } },
	
	DEFAULT { public boolean booleanValue() { throw new IllegalResolutionException(); } };
	
	public abstract boolean booleanValue();
	
	public static class IllegalResolutionException extends RuntimeException {
		//Just use the defaults
	}
}
