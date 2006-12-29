package plugin.jepcommands;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.util.PCGenCommand;

/**
 * JEP command for floor
 * 
 * eg. floor(12.6) --> 12
 */
public class FloorCommand extends PCGenCommand
{

	/** Constructor */
	public FloorCommand()
	{
		numberOfParameters = 1;
	}

	public String getFunctionName()
	{
		return "FLOOR";
	}

	/**
	 * Runs floor on the inStack. The parameter is popped
	 * off the <code>inStack</code>, and the floor of its value is
	 * pushed back to the top of <code>inStack</code>.
	 * @param inStack
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
	public void run(Stack inStack) throws ParseException
	{
		// check the stack
		checkStack(inStack);

		// get the parameter from the stack
		Object param = inStack.pop();

		// check whether the argument is of the right type
		if (param instanceof Double)
		{
			// calculate the result
			double r = Math.floor(((Double) param).doubleValue());

			// push the result on the inStack
			inStack.push(new Double(r));
		}
		else
		{
			throw new ParseException("Invalid parameter type");
		}
	}
}
