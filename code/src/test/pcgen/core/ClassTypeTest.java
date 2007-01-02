package pcgen.core;

import pcgen.PCGenTestCase;

/**
 * <code>ClassTypeTest</code> <strong>needs documentation</strong>.
 *
 * @author <a href="binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Id$
 */
public class ClassTypeTest extends PCGenTestCase
{
	/**
	 * Constructs a new <code>ClassTypeTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public ClassTypeTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>ClassTypeTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public ClassTypeTest(final String name)
	{
		super(name);
	}

	/**
	 * test clone
	 * @throws Exception
	 */
	public void testClone() throws Exception
	{
		final ClassType expected = new ClassType();
		final ClassType actual = (ClassType) expected.clone();

		assertEquals(expected.getCRFormula(), actual.getCRFormula());
		assertEquals(expected.getXPPenalty(), actual.getXPPenalty());
		assertEquals(expected.isMonster(), actual.isMonster());
	}
}
