package plugin.jepcommands;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessor;
import pcgen.util.PCGenCommand;
import pcgen.util.PJEP;

/**
 * JEP command for class level (cl)
 *
 * eg. cl("Fighter")
 * eg. cl("Fighter", 21)
 * eg. cl()
 */
public class ClassLevelCommand extends PCGenCommand {
	
	/**
	 * Constructor
	 */
	public ClassLevelCommand() {
		numberOfParameters = -1;
	}

	public String getFunctionName() {
		return "CLASSLEVEL";
	}

	public boolean updateVariables(PJEP jep) {
		boolean updated = false;
		if (jep.removeVariable("CL") != null) {
			updated = true;
		}

		String src = variableSource;
		if ((src == null) || !src.startsWith("CLASS:")) {
			return updated;
		}
		src = src.substring(6);

		PlayerCharacter pc = null;
		if (parent instanceof VariableProcessor) {
			pc = ((VariableProcessor) parent).getPc();
		}
		else if (parent instanceof PlayerCharacter) {
			pc = (PlayerCharacter) parent;
		}
		if (pc == null) {
			return updated;
		}

		Double result = new Double(pc.getClassLevelString(src, false));
		jep.addVariable("CL", result.doubleValue());

		return true;
	}

	/**
	 * Runs classlevel on the inStack. The parameter is popped
	 * off the <code>inStack</code>, and the variable's value is
	 * pushed back to the top of <code>inStack</code>.
	 * @param inStack
	 * @throws ParseException
	 */
	public void run(Stack inStack) throws ParseException
	{
		// check the stack
		checkStack(inStack);

		// get the parameter from the stack
		Object param1;
		Object param2 = null;

		int paramCount = curNumberOfParameters;

		//
		// If there are no parameters and this is used in a CLASS file, then use the
		// class name
		//
		if (paramCount == 0) {
			String src = variableSource;
			if (src.startsWith("CLASS:")) {
				src = src.substring(6);
				inStack.push(src);
				++paramCount;
			}
		}

		//
		// have to do this in reverse order...this is a stack afterall
		//
		if (paramCount == 1) {
			param1 = inStack.pop();
		}
		else if (paramCount == 2) {
			param2 = inStack.pop();
			param1 = inStack.pop();

			if (param2 instanceof Integer) {
				// TODO Do Nothing?
			}
			else if (param2 instanceof Double) {
				param2 = new Integer(((Double) param2).intValue());
			}
			else {
				throw new ParseException("Invalid parameter type");
			}
		}
		else {
			throw new ParseException("Invalid parameter count");
		}

		Object result = null;

		if (param1 instanceof String) {
			PlayerCharacter pc = null;
			if (parent instanceof VariableProcessor) {
				pc = ((VariableProcessor) parent).getPc();
			}
			else if (parent instanceof PlayerCharacter) {
				pc = (PlayerCharacter) parent;
			}
			if (pc == null) {
				throw new ParseException("Invalid parent (no PC): " + parent.getClass().getName());
			}

			// ";BEFORELEVEL="
			String cl = (String)param1;
			if (param2 != null) {
				cl += ";BEFORELEVEL=" + param2.toString();
			}

			result = new Double(pc.getClassLevelString(cl, false));

			inStack.push(result);
		}
		else {
			throw new ParseException("Invalid parameter type");
		}
	}
}

