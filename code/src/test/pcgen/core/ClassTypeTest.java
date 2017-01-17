package pcgen.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@code ClassTypeTest} <strong>needs documentation</strong>.
 *
 * @author <a href="binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
public class ClassTypeTest
{

	/**
	 * test clone
	 * @throws Exception
	 */
	@Test
	public void testClone() throws Exception
	{
		final ClassType expected = new ClassType();
		final ClassType actual = expected.clone();

		Assert.assertEquals(expected.getCRFormula(), actual.getCRFormula());
		Assert.assertEquals(expected.getXPPenalty(), actual.getXPPenalty());
		Assert.assertEquals(expected.isMonster(), actual.isMonster());
	}
}
