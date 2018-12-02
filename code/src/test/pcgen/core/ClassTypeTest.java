package pcgen.core;

import junit.framework.TestCase;

/**
 * {@code ClassTypeTest} <strong>needs documentation</strong>.
 */
public class ClassTypeTest extends TestCase
{
	/**
	 * Constructs a new {@code ClassTypeTest}.
	 */
	public ClassTypeTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new {@code ClassTypeTest} with the given <var>name</var>.
	 *
	 * @param name the test case name
	 */
	public ClassTypeTest(final String name)
	{
		super(name);
	}

	/**
	 * test clone.
	 */
	public void testClone()
	{
		final ClassType expected = new ClassType();
		final ClassType actual = expected.clone();

		assertEquals(expected.getCRFormula(), actual.getCRFormula());
		assertEquals(expected.getXPPenalty(), actual.getXPPenalty());
		assertEquals(expected.isMonster(), actual.isMonster());
	}
}
