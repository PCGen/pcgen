package pcgen;

import junit.framework.TestCase;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.util.enumeration.Tab;
import pcgen.util.TestChecker;
import pcgen.util.testchecker.*;

/**
 * Test case base for PCGen.  This addresses a common bug with JUnit whereby
 * when a unit test throws an exception, and then <code>tearDown</code> will not
 * unwind correctly, the original exception from the unit test is buried by the
 * exception from <code>tearDown</code>.
 *
 * The solution is to override {@link #runBare()} and save the exception from
 * the unit test, rethrowing it after <code>tearDown</code> finishes.
 *
 * @author <a href="binkley@alumni.rice.edu">B. K Oxley (binkley)</a>
 * @version $Id$
 */
@SuppressWarnings("nls")
public abstract class PCGenTestCase extends TestCase
{
	/**
	 * Sets up some basic stuff that must be present for tests to work.
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final GameMode gamemode = new GameMode("3.5");
		gamemode.setBonusFeatLevels("3|3");
		gamemode.setAlignmentText("Alignment");
		gamemode.setTabName(Tab.ABILITIES, "Feats");
		SystemCollections.addToGameModeList(gamemode);
		SettingsHandler.setGame("3.5");
	}

	/**
	 * Constructs a new <code>PCGenTestCase</code>.
	 *
	 * @see TestCase#TestCase()
	 */
	public PCGenTestCase()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>PCGenTestCase</code> with the given <var>name</var>.
	 *
	 * @see TestCase#TestCase(String)
	 */
	public PCGenTestCase(final String name)
	{
		super(name);
	}

	/**
	 * Fixes {@link TestCase#runBare()} to not swallow a throwable from {@link
	 * #runTest()} if {@link #tearDown()} also throws.
	 *
	 * @throws Throwable
	 */
	@Override
	public void runBare() throws Throwable
	{
		setUp();

		Throwable thrown = null;

		try
		{
			runTest();

		}

		catch (final Throwable t)
		{
			thrown = t;

		}

		finally
		{
			try
			{
				tearDown();

			}

			finally
			{
				if (null != thrown)
				{
					// Replace any tear down exception with
					// unit test exception
					throw thrown;
				}
			}
		}
	}

	protected void is(Object something, TestChecker matches)
	{
		if (!matches.check(something))
		{
			StringBuffer message = new StringBuffer("\nExpected: ");
			matches.scribe(message);
			message.append("\nbut got: ").append(something).append('\n');
			fail(message.toString());
		}
	}

	protected void is(Object something, TestChecker matches, String testCase)
	{
		if (!matches.check(something))
		{
			StringBuffer message = new StringBuffer("\nExpected: ");
			matches.scribe(message);
			message.append("\nbut got: ").append(something);
			message.append(" \nIn test ").append(testCase);
			fail(message.toString());
		}
	}

	public CompareEqualString strEq(String s)
	{
		return new CompareEqualString(s);
	}

	public CompareEqualObject eq(Object operand)
	{
		return new CompareEqualObject(operand);
	}

	public CompareEqualBoolean eq(boolean bo)
	{
		return new CompareEqualBoolean(bo);
	}

	public CompareEqualByte eq(byte operand)
	{
		return new CompareEqualByte(operand);
	}

	public CompareEqualShort eq(short operand)
	{
		return new CompareEqualShort(operand);
	}

	public CompareEqualChar eq(char operand)
	{
		return new CompareEqualChar(operand);
	}

	public CompareEqualInt eq(int operand)
	{
		return new CompareEqualInt(operand);
	}

	public CompareEqualLong eq(long operand)
	{
		return new CompareEqualLong(operand);
	}

	public CompareEqualFloat eq(float operand)
	{
		return new CompareEqualFloat(operand);
	}

	public CompareEqualDouble eq(double operand)
	{
		return new CompareEqualDouble(operand);
	}

	public CompareDeadband eq(double operand, double error)
	{
		return new CompareDeadband(operand, error);
	}

	public CompareNull eqnull()
	{
		return new CompareNull();
	}

	public CompareSame same(Object operand)
	{
		return new CompareSame(operand);
	}

	public CompareSubstring stringContains(String substring)
	{
		return new CompareSubstring(substring);
	}

	public BoolNot not(TestChecker c)
	{
		return new BoolNot(c);
	}

	public BoolAnd and(TestChecker left, TestChecker right)
	{
		return new BoolAnd(left, right);
	}

	public BoolOr or(TestChecker left, TestChecker right)
	{
		return new BoolOr(left, right);
	}

	public BoolXor xor(TestChecker left, TestChecker right)
	{
		return new BoolXor(left, right);
	}

	public CompareGreaterThan gt(Comparable operand)
	{
		return new CompareGreaterThan(operand);
	}

	public CompareGreaterOrEqual ge(Comparable operand)
	{
		return new CompareGreaterOrEqual(operand);
	}

	public CompareLessThan lt(Comparable operand)
	{
		return new CompareLessThan(operand);
	}

	public CompareLessOrEqual le(Comparable operand)
	{
		return new CompareLessOrEqual(operand);
	}
}
