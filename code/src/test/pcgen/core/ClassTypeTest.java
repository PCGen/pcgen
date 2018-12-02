package pcgen.core;

import junit.framework.TestCase;

/**
 * {@code ClassTypeTest} <strong>needs documentation</strong>.
 */
public class ClassTypeTest extends TestCase
{
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
