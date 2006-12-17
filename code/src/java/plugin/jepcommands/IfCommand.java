package plugin.jepcommands;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.util.PCGenCommand;

/**
 * <p>
 * If class; extends PostfixMathCommand. This class accepts three agruments.
 * The first is a number interpreted as a boolean. The other two may be any
 * supported classes. If the first argument != 0, the second argument is
 * returned. Otherwise, the third argument is returned.
 * </p>
 * <p>
 * So, given if(x,y,z), y is returned if x != 0, and z is returned
 * otherise.
 * </p>
 *
 * @author Ross M. Lodge
 *
 */
public class IfCommand extends PCGenCommand
{
	/**
	 * <p>
	 * Initializes the number of parameters to = 3, indicating three number
	 * of parameters.
	 * </p>
	 *
	 */
	public IfCommand()
	{
		super();
		numberOfParameters = 3;
	}

	public String getFunctionName()
	{
		return "IF";
	}

	/**
	 * <p>
	 * Evaluates the three parameters. The first may be a subclass of
	 * Number, or a Boolean. The second and third can be any supported type.
	 * If the first argument is true, the second argument is returned;
	 * otherwise, the third argument is returned.
	 * </p>
	 *
	 * @param stack
	 *            Stack of incoming arguments.
	 * @throws ParseException
	 */
	public void run(Stack stack) throws ParseException
	{
		// Check if stack is null
		if (null == stack)
		{
			throw new ParseException("Stack argument null");
		}

		Object result = null;

		boolean condition = false;

		Object param3 = stack.pop();
		Object param2 = stack.pop();
		Object param1 = stack.pop();

		if (param1 instanceof Number)
		{
			condition = (((Number) param1).doubleValue() != 0d);
		}
		else if (param1 instanceof Boolean)
		{
			condition = ((Boolean) param1).booleanValue();
		}
		else
		{
			throw new ParseException("Invalid parameter type for Parameter 1");
		}

		if (condition)
		{
			result = param2;
		}
		else
		{
			result = param3;
		}

		// push the result on the inStack
		stack.push(result);
	}
}
