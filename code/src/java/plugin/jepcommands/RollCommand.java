package plugin.jepcommands;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.util.PCGenCommand;
import pcgen.util.PJEP;

//
// eg. roll("10+d10")
//
public class RollCommand extends PCGenCommand {
	/**
	 * Constructor
	 */
	public RollCommand() {
		numberOfParameters = 1;
	}

	public String getFunctionName() {
		return "ROLL";
	}

	public boolean updateVariables(PJEP jep) {
		return false;
	}

	/**
	 * Is this command cacheable?
	 * @return true if cacheable, false if not.
	 */
	public boolean getCachable() {
		return false;
	}
	
	/**
	 * Runs getvar on the inStack. The parameter is popped
	 * off the <code>inStack</code>, and the variable's value is
	 * pushed back to the top of <code>inStack</code>.
	 * @param inStack
	 * @throws ParseException
	 */
	public void run(Stack inStack) throws ParseException {
		// check the stack
		checkStack(inStack);

		// get the parameter from the stack
		Object param1;

		//
		// have to do this in reverse order...this is a stack afterall
		//
		param1 = inStack.pop();
		Object result = null;

		if (param1 instanceof String) {
			result = new Integer(pcgen.core.RollingMethods.roll((String)param1));
			inStack.push(result);
		}
		else {
			throw new ParseException("Invalid parameter type");
		}
	}
}
