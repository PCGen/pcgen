package pcgen;

import java.math.BigDecimal;

import junit.framework.TestCase;
import pcgen.core.GameMode;
import pcgen.core.LevelInfo;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.system.LoadInfo;
import pcgen.persistence.lst.LstSystemLoader;
import pcgen.util.Logging;
import pcgen.util.TestChecker;
import pcgen.util.testchecker.BoolAnd;
import pcgen.util.testchecker.BoolNot;
import pcgen.util.testchecker.BoolOr;
import pcgen.util.testchecker.BoolXor;
import pcgen.util.testchecker.CompareDeadband;
import pcgen.util.testchecker.CompareEqualBoolean;
import pcgen.util.testchecker.CompareEqualByte;
import pcgen.util.testchecker.CompareEqualChar;
import pcgen.util.testchecker.CompareEqualDouble;
import pcgen.util.testchecker.CompareEqualFloat;
import pcgen.util.testchecker.CompareEqualIgnoreCaseString;
import pcgen.util.testchecker.CompareEqualInt;
import pcgen.util.testchecker.CompareEqualLong;
import pcgen.util.testchecker.CompareEqualObject;
import pcgen.util.testchecker.CompareEqualShort;
import pcgen.util.testchecker.CompareEqualString;
import pcgen.util.testchecker.CompareGreaterOrEqual;
import pcgen.util.testchecker.CompareGreaterThan;
import pcgen.util.testchecker.CompareLessOrEqual;
import pcgen.util.testchecker.CompareLessThan;
import pcgen.util.testchecker.CompareNull;
import pcgen.util.testchecker.CompareSame;
import pcgen.util.testchecker.CompareSubstring;

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
	protected boolean verbose = false;
	protected int     count   = 0;
	protected int     errors  = 0;

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
		gamemode.addLevelInfo("Normal", new LevelInfo());
		gamemode.addXPTableName("Normal");
		gamemode.setDefaultXPTableName("Normal");
		LoadInfo loadable =
				gamemode.getModeContext().ref.constructNowIfNecessary(
					LoadInfo.class, gamemode.getName());
		loadable.addLoadScoreValue(0, BigDecimal.ONE);
		LstSystemLoader.addDefaultTabInfo(gamemode);
		SystemCollections.addToGameModeList(gamemode);
		SettingsHandler.setGame("3.5");
		count  = 0;
		errors = 0;
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
	 * @param name The name of the test case
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

			if (verbose && 0 != errors)
			{
				Logging.errorPrint("Failed " + errors + " of " + count + " interruptable tests");
				fail("Failed " + errors + " of " + count + " interruptable tests");
			}
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

	protected void is(final Object something, final TestChecker matches)
	{
        count += 1;

		if (verbose)
		{
			if (matches.check(something))
			{
				Logging.errorPrint("OK - unlabelled test case");
			}
			else
			{
				Logging.errorPrint("\n!!! Not OK !!! - unlabelled test case");

				final StringBuffer message = new StringBuffer("  Expected: ");
				matches.scribe(message);
				message.append("\n  but got: ").append(something).append('\n');

				Logging.errorPrint(message.toString());
                errors += 1;
			}
		}
		else if (!matches.check(something))
		{

			final StringBuffer message = new StringBuffer("\nExpected: ");
			matches.scribe(message);
			message.append("\nbut got: ").append(something).append('\n');
			fail(message.toString());
		}
	}

	protected void is(final Object something, final TestChecker matches, final String testCase)
	{
        count += 1;

		if (verbose)
		{
			if (matches.check(something))
			{
				Logging.errorPrint("OK - " + testCase);
			}
			else
			{
				Logging.errorPrint("\n!!! Not OK !!! - " + testCase);

				final StringBuffer message = new StringBuffer("  Expected: ");
				matches.scribe(message);
				message.append("\n  but got: ").append(something).append("\n");
				
				Logging.errorPrint(message.toString());
                errors += 1;
			}
		}
		else if (!matches.check(something))
		{

			final StringBuffer message = new StringBuffer("\nExpected: ");
			matches.scribe(message);
			message.append("\nbut got: ").append(something);
			message.append(" \nIn test ").append(testCase);

			fail(message.toString());
		}
	}

	public CompareEqualString strEq(final String s)
	{
		return new CompareEqualString(s);
	}

	public CompareEqualIgnoreCaseString strEqIC(final String s)
	{
		return new CompareEqualIgnoreCaseString(s);
	}

	public CompareEqualObject eq(final Object operand)
	{
		return new CompareEqualObject(operand);
	}

	public CompareEqualBoolean eq(final boolean bo)
	{
		return new CompareEqualBoolean(bo);
	}

	public CompareEqualByte eq(final byte operand)
	{
		return new CompareEqualByte(operand);
	}

	public CompareEqualShort eq(final short operand)
	{
		return new CompareEqualShort(operand);
	}

	public CompareEqualChar eq(final char operand)
	{
		return new CompareEqualChar(operand);
	}

	public CompareEqualInt eq(final int operand)
	{
		return new CompareEqualInt(operand);
	}

	public CompareEqualLong eq(final long operand)
	{
		return new CompareEqualLong(operand);
	}

	public CompareEqualFloat eq(final float operand)
	{
		return new CompareEqualFloat(operand);
	}

	public CompareEqualDouble eq(final double operand)
	{
		return new CompareEqualDouble(operand);
	}

	public CompareDeadband eq(final double operand, final double error)
	{
		return new CompareDeadband(operand, error);
	}

	public CompareNull eqnull()
	{
		return new CompareNull();
	}

	public CompareSame same(final Object operand)
	{
		return new CompareSame(operand);
	}

	public CompareSubstring stringContains(final String substring)
	{
		return new CompareSubstring(substring);
	}

	public BoolNot not(final TestChecker c)
	{
		return new BoolNot(c);
	}

	public BoolAnd and(final TestChecker left, final TestChecker right)
	{
		return new BoolAnd(left, right);
	}

	public BoolOr or(final TestChecker left, final TestChecker right)
	{
		return new BoolOr(left, right);
	}

	public BoolXor xor(final TestChecker left, final TestChecker right)
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
