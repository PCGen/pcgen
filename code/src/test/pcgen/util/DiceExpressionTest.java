package pcgen.util;

import junit.framework.TestCase;

/**
 * {@code DiceExpressionTest} tests {@link DiceExpression}.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Id: DiceExpressionTest.java,v 1.6 2005/10/26 09:56:05 karianna Exp $
 * @since Jul 17, 2005 10:53:44 AM
 */
public class DiceExpressionTest
		extends TestCase
{
	/**
	 * Tests multiplication <cite>v.</cite> exponentiation.
	 * @throws Exception
	 */
	public void testRollDiceForMultipyVExponentiate()
		throws Exception {
		new DiceExpression("1*2");

		assertEquals(new DiceExpression("1*2").rollDice(),
			2 * new DiceExpression("1**2").rollDice());
	}

	/**
	 * Tests <cite>d100</cite> dice expression.
	 * @throws Exception
	 */
	public void testRollDiceForPercentageAsNumber()
		throws Exception {
		final DiceExpression dice = new DiceExpression("d100");

		assertTrue(dice.rollDice() > 0);
		assertTrue(dice.rollDice() <= 100);
	}

	/**
	 * Tests <cite>d%</cite> dice expression.
	 * @throws Exception
	 */
	public void testRollDiceForPercentageAsSymbol()
		throws Exception {
		final DiceExpression dice = new DiceExpression("d%");

		assertTrue(dice.rollDice() > 0);
		assertTrue(dice.rollDice() <= 100);
	}
	
	/**
	 * test max roll 1d12
	 * @throws Exception
	 */
	public void testRollForMax1() throws Exception
	{
		TestRollExpression("maxroll(1d12)", 12);
	}

	/**
	 * test max roll 1d12 + 1d4
	 * @throws Exception
	 */
	public void testRollForMax2() throws Exception
	{
		TestRollExpression("maxroll(1d12)+maxroll(1d4)", 16);
	}

	/**
	 * test max roll 1d12 + 1d4 (alternative)
	 * @throws Exception
	 */
	public void testRollForMax3() throws Exception
	{
		TestRollExpression("maxroll(1d12+1d4)", 16);
	}

	/**
	 * test max roll 1d12+1d4+2
	 * @throws Exception
	 */
	public void testRollForMax4() throws Exception
	{
		TestRollExpression("maxroll(1d12+1d4+2)", 18);
	}
	
	private void TestRollExpression(final String rollExpr, final int expectedValue) throws Exception
	{
		final DiceExpression dice = new DiceExpression(rollExpr);
		for(int i = 0; i < 100; ++i)
		{
			final int dieRoll = dice.rollDice();
			assertTrue(rollExpr +
							" returned " +
							Integer.toString(dieRoll) +
							" (expected " + 
							Integer.toString(expectedValue) +
							") on attempt " +
							Integer.toString(i + 1) + " of 100",
						dieRoll == expectedValue);
		}
	}
}
