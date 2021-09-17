package plugin.jepcommands;

import java.util.Stack;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessorEq;
import pcgen.core.VariableProcessorPC;
import pcgen.util.Logging;
import pcgen.util.PCGenCommand;

import org.nfunk.jep.ParseException;

/**
 * JEP command for get vars
 * 
 * eg. getvar("CL=Fighter")
 */
public class GetVarCommand extends PCGenCommand
{

	/** Constructor */
	public GetVarCommand()
	{
		numberOfParameters = -1; // allow variable # of parameters
	}

	/**
	 * Gets the name of the function handled by this class.
	 * @return The name of the function.
	 */
	@Override
	public String getFunctionName()
	{
		return "VAR";
	}

	/**
	 * Runs getvar on the inStack. The parameter is popped
	 * off the {@code inStack}, and the variable's value is
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
		final Object param1;
		Object param2 = null;

		//
		// have to do this in reverse order...this is a stack afterall
		//
		if (curNumberOfParameters == 1)
		{
			param1 = inStack.pop();
		}
		else if (curNumberOfParameters == 2)
		{
			param2 = inStack.pop();
			param1 = inStack.pop();

			if (!(param2 instanceof Double))
			{
				throw new ParseException("Invalid parameter type");
			}
		}
		else
		{
			throw new ParseException("Invalid parameter count");
		}

		if (param1 instanceof String)
		{
			Float result = null;
			if (parent instanceof final PlayerCharacter character)
			{
				result = getVariableForCharacter(character, param1);
			}
			else if (parent instanceof Equipment)
			{
				boolean bPrimary = true;

				if (param2 != null)
				{
					bPrimary = (((Double) param2).intValue() != 0);
				}

				result = ((Equipment) parent).getVariableValue((String) param1, "", bPrimary, null);
			}
			else if (parent instanceof final VariableProcessorPC vpc)
			{
				// check to see if this is just a variable
				result = vpc.lookupVariable((String) param1, variableSource, null);
				if (result == null)
				{
					result = vpc.getVariableValue(null, (String) param1, variableSource, 0);
				}
			}
			else if (parent instanceof VariableProcessorEq veq)
			{
				result = veq.getVariableValue(null, (String) param1, variableSource, 0);
			}
			else if (parent == null)
			{
				Logging.errorPrint("Ignored request for var " + String.valueOf(param1) + " with no parent.");
			}

			if (result == null)
			{
				throw new ParseException("Error retreiving variable:" + param1);
			}

			inStack.push(result.doubleValue());
		}
		else
		{
			throw new ParseException("Invalid parameter type");
		}
	}

	protected Float getVariableForCharacter(final PlayerCharacter character, final Object param1)
	{
		return character.getVariableValue((String) param1, variableSource);
	}
}
