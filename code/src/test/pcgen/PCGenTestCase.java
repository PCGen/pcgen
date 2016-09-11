package pcgen;

import pcgen.util.TestChecker;
import pcgen.util.testchecker.BoolNot;
import pcgen.util.testchecker.CompareDeadband;
import pcgen.util.testchecker.CompareEqualBoolean;
import pcgen.util.testchecker.CompareEqualInt;
import pcgen.util.testchecker.CompareEqualObject;
import pcgen.util.testchecker.CompareEqualString;
import pcgen.util.testchecker.CompareGreaterThan;
import pcgen.util.testchecker.CompareNull;

import junit.framework.TestCase;
import org.jetbrains.annotations.Contract;

/**
 * Test case base for PCGen.  This addresses a common bug with JUnit whereby
 * when a unit test throws an exception, and then {@code tearDown} will not
 * unwind correctly, the original exception from the unit test is buried by the
 * exception from {@code tearDown}.
 *
 * The solution is to override {@link #runBare()} and save the exception from
 * the unit test, rethrowing it after {@code tearDown} finishes.
 *
 * @author <a href="binkley@alumni.rice.edu">B. K Oxley (binkley)</a>
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
	}

	/**
	 * Constructs a new {@code PCGenTestCase}.
	 *
	 * @see TestCase#TestCase()
	 */
	protected PCGenTestCase()
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

	public static void is(final Object something, final TestChecker matches)
	{
		if (!matches.check(something))
		{

			final StringBuilder message = new StringBuilder("\nExpected: ");
			matches.scribe(message);
			message.append("\nbut got: ").append(something).append('\n');
			fail(message.toString());
		}
	}

	public static void is(final Object something, final TestChecker matches, final String testCase)
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

	@Contract("_ -> !null")
	public static CompareEqualString strEq(final String s)
	{
		return new CompareEqualString(s);
	}

	@Contract("_ -> !null")
	public static CompareEqualObject eq(final Object operand)
	{
		return new CompareEqualObject(operand);
	}

	@Contract("_ -> !null")
	public static CompareEqualBoolean eq(final boolean bo)
	{
		return new CompareEqualBoolean(bo);
	}

	@Contract("_ -> !null")
	public static CompareEqualInt eq(final int operand)
	{
		return new CompareEqualInt(operand);
	}

	@Contract("_, _ -> !null")
	public static CompareDeadband eq(final double operand, final double error)
	{
		return new CompareDeadband(operand, error);
	}

	@Contract(" -> !null")
	public static CompareNull eqnull()
	{
		return new CompareNull();
	}

	@Contract("_ -> !null")
	public static BoolNot not(final TestChecker c)
	{
		return new BoolNot(c);
	}

	@Contract("_ -> !null")
	public static CompareGreaterThan gt(Comparable operand)
	{
		return new CompareGreaterThan(operand);
	}
}
