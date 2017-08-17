package plugin.jepcommands;

import org.nfunk.jep.ParseException;
import pcgen.util.PCGenCommand;

import java.util.Stack;

/**
 * Celing JEP Command. {@literal  eg. ceil(12.6) --> 13 }
 */
public class CeilCommand extends PCGenCommand
{

	/**
	 * Constructor
	 */
	public CeilCommand()
	{
		numberOfParameters = 1;
	}

	/**
	 * Gets the name of the function handled by this class.
	 * @return The name of the function.
	 */
    @Override
	public String getFunctionName()
	{
		return "CEIL";
	}

	/**
	 * Runs ceil on the inStack. The parameter is popped
	 * off the {@code inStack}, and the ceiling of it's value is
	 * pushed back to the top of {@code inStack}.
	 * @param inStack the jep stack
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
    @Override
	public void run(final Stack inStack) throws ParseException
	{
		// check the stack
		checkStack(inStack);

		// get the parameter from the stack
		final Object param = inStack.pop();

		// check whether the argument is of the right type
		if (param instanceof Double)
		{
			// calculate the result
			final double r = Math.ceil((Double) param);

			// push the result on the inStack
			inStack.push(r);
		}
		else
		{
			throw new ParseException("Invalid parameter type");
		}
	}
}
