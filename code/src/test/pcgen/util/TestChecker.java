package pcgen.util;

/**
 * Class provides two methods for test checks
 */
public abstract class TestChecker
{
	/**
	 * Perform the check that this class represents
	 *
	 * @param obj the object to be checked
	 * @return {@code true} if the check passes for <var>obj</var>,
	 *	       {@code false} if it doesn't.
	 */
	public abstract boolean check(Object obj);

	/**
	 * Appends a description of the check that this class will perform to the StringBuilder passed in.
	 *
	 * @param buffer The buffer that the description is appended to.
	 * @return The buffer that was passed in.
	 */
	public abstract StringBuilder scribe(StringBuilder buffer);
}
