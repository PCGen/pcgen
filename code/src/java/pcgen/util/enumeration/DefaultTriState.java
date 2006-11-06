package pcgen.util.enumeration;

public enum DefaultTriState {
	YES {
		@Override
		public boolean booleanValue() {
			return true;
		}
	},

	NO {
		@Override
		public boolean booleanValue() {
			return false;
		}
	},

	DEFAULT {
		@Override
		public boolean booleanValue() {
			throw new IllegalResolutionException();
		}
	};

	public abstract boolean booleanValue();

	public static class IllegalResolutionException extends RuntimeException {
		// Just use the defaults
	}
}
