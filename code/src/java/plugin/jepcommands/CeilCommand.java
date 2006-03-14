package plugin.jepcommands;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.util.PCGenCommand;

/**
 * Celing JEP Command.  eg. ceil(12.6) --> 13
 */
public class CeilCommand extends PCGenCommand {
	
	/**
	 * Constructor
	 */
	public CeilCommand() {
		numberOfParameters = 1;
	}

	public String getFunctionName() {
		return "CEIL";
	}

	/**
	 * Runs ceil on the inStack. The parameter is popped
	 * off the <code>inStack</code>, and the ceiling of it's value is
	 * pushed back to the top of <code>inStack</code>.
	 * @param inStack
	 * @throws ParseException
	 */
	public void run(Stack inStack) throws ParseException {
		// check the stack
		checkStack(inStack);

		// get the parameter from the stack
		Object param = inStack.pop();

		// check whether the argument is of the right type
		if (param instanceof Double) {
			// calculate the result
			double r = Math.ceil(((Double) param).doubleValue());

			// push the result on the inStack
			inStack.push(new Double(r));
		}
		else {
			throw new ParseException("Invalid parameter type");
		}
	}
}

