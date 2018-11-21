package pcgen;

import java.math.BigDecimal;

import pcgen.core.GameMode;
import pcgen.core.LevelInfo;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.system.LoadInfo;
import pcgen.persistence.GameModeFileLoader;
import pcgen.util.TestChecker;
import pcgen.util.testchecker.CompareDeadband;
import pcgen.util.testchecker.CompareEqualBoolean;
import pcgen.util.testchecker.CompareEqualInt;
import pcgen.util.testchecker.CompareEqualObject;
import pcgen.util.testchecker.CompareEqualString;
import pcgen.util.testchecker.CompareGreaterThan;
import pcgen.util.testchecker.CompareNull;

import junit.framework.TestCase;

/**
 * Test case base for PCGen.  This addresses a common bug with JUnit whereby
 * when a unit test throws an exception, and then {@code tearDown} will not
 * unwind correctly, the original exception from the unit test is buried by the
 * exception from {@code tearDown}.
 *
 * The solution is to override {@link #runBare()} and save the exception from
 * the unit test, rethrowing it after {@code tearDown} finishes.
 *
 * @deprecated the described bug no longer exists on modern versions of junit
 */
@SuppressWarnings("nls")
@Deprecated
public abstract class PCGenTestCase extends TestCase
{
	/**
	 * Sets up some basic stuff that must be present for tests to work.
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
		gamemode.clearLoadContext();
		LoadInfo loadable =
				gamemode.getModeContext().getReferenceContext().constructNowIfNecessary(
					LoadInfo.class, gamemode.getName());
		loadable.addLoadScoreValue(0, BigDecimal.ONE);
		GameModeFileLoader.addDefaultTabInfo(gamemode);
		SystemCollections.addToGameModeList(gamemode);
		SettingsHandler.setGame("3.5");
	}

	/**
	 * Constructs a new {@code PCGenTestCase}.
	 *
	 * @see TestCase#TestCase()
	 */
	public PCGenTestCase()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new {@code PCGenTestCase} with the given <var>name</var>.
	 *
	 * @param name The name of the test case
     * @see TestCase#TestCase(String)
	 */
	public PCGenTestCase(final String name)
	{
		super(name);
	}

	protected void is(final Object something, final TestChecker matches)
	{
		if (!matches.check(something))
		{

			final StringBuilder message = new StringBuilder("\nExpected: ");
			matches.scribe(message);
			message.append("\nbut got: ").append(something).append('\n');
			fail(message.toString());
		}
	}

	protected void is(final Object something, final TestChecker matches, final String testCase)
	{
		if (!matches.check(something))
		{

			final StringBuilder message = new StringBuilder("\nExpected: ");
			matches.scribe(message);
			message.append("\nbut got: ").append(something);
			message.append(" \nIn test ").append(testCase);

			fail(message.toString());
		}
	}

	public static CompareEqualString strEq(final String s)
	{
		return new CompareEqualString(s);
	}

	public static CompareEqualObject eq(final Object operand)
	{
		return new CompareEqualObject(operand);
	}

	public static CompareEqualBoolean eq(final boolean bo)
	{
		return new CompareEqualBoolean(bo);
	}

	public static CompareEqualInt eq(final int operand)
	{
		return new CompareEqualInt(operand);
	}

	public static CompareDeadband eq(final double operand, final double error)
	{
		return new CompareDeadband(operand, error);
	}

	protected static CompareNull eqnull()
	{
		return new CompareNull();
	}

	protected static CompareGreaterThan gt(Comparable operand)
	{
		return new CompareGreaterThan(operand);
	}
}
