package plugin.jepcommands;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessorEq;
import pcgen.core.VariableProcessorPC;
import pcgen.util.Logging;
import pcgen.util.PCGenCommand;

//
//eg. getvar("CL=Fighter")
//
public class GetVarCommand extends PCGenCommand {
	/**
	 * Constructor
	 */
	public GetVarCommand() {
		numberOfParameters = -1; // allow variable # of parameters
	}

	public String getFunctionName() {
		return "VAR";
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
		Object param2 = null;

		//
		// have to do this in reverse order...this is a stack afterall
		//
		if (curNumberOfParameters == 1) {
			param1 = inStack.pop();
		}
		else if (curNumberOfParameters == 2) {
			param2 = inStack.pop();
			param1 = inStack.pop();

			if (!(param2 instanceof Double)) {
				throw new ParseException("Invalid parameter type");
			}
		}
		else {
			throw new ParseException("Invalid parameter count");
		}

		Object result = null;

		if (param1 instanceof String) {
			if (parent instanceof PlayerCharacter) {
				PlayerCharacter character = (PlayerCharacter)parent;
				result = getVariableForCharacter(character, param1);
			}
			else if (parent instanceof Equipment) {
				boolean bPrimary = true;

				if (param2 != null) {
					bPrimary = (((Double) param2).intValue() != 0);
				}

				result = ((Equipment) parent).getVariableValue((String) param1, "", bPrimary, null);
			}
			else if (parent instanceof VariableProcessorPC) {
				VariableProcessorPC vpc = (VariableProcessorPC) parent;
				// check to see if this is just a variable
				result = vpc.lookupVariable((String)param1, variableSource, null);
				if (result == null) {
					result = vpc.getVariableValue(null, (String)param1, variableSource, 0);
				}
			}
			else if (parent instanceof VariableProcessorEq) {
				VariableProcessorEq veq = (VariableProcessorEq) parent;
				result = veq.getVariableValue(null, (String)param1, variableSource, 0);
			}
			else if (parent == null) {
				Logging.errorPrint("Ignored request for var " + String.valueOf(param1) + " with no parent.");
			}

			if (result == null) {
				throw new ParseException("Error retreiving variable:" + param1);
			}

			inStack.push(result);
		}
		else {
			throw new ParseException("Invalid parameter type");
		}
	}

	protected Object getVariableForCharacter(PlayerCharacter character, Object param1) {
		Object result = character.getVariableValue((String) param1, variableSource);
		return result;
	}
}
