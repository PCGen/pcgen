package gmgen.plugin.dice;

@FunctionalInterface
public interface ResultModifier
{


	/**
	 * Given a sequence of values, produces a new sequence of values
	 * This allows the modification of a value to be abstracted from the production of the original value.
	 * @param in sequence of original values
	 * @return sequence of values
	 */
	int[] resultAsModified(int[] in);

	static int[] modify(final ResultModifier... modifiers) {
		int[] result = new int[0];
		for(final ResultModifier rm: modifiers) {
			result = rm.resultAsModified(result);
		}
		return result;
	}
}
