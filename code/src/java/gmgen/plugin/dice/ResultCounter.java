package gmgen.plugin.dice;

/**
 * Given a set of dice results, produces a single value for the "total"
 * Can be used to sum values, drop the lowest/highest/etc.
 */
@FunctionalInterface
interface ResultCounter
{
	int totalCount(int[] in);
}
