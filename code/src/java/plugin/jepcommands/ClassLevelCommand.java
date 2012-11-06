package plugin.jepcommands;

import org.nfunk.jep.ParseException;
import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessor;
import pcgen.util.PCGenCommand;
import pcgen.util.PJEP;

import java.util.Stack;

/**
 * JEP command for class level (cl)
 *
 * eg. cl("Fighter")
 * eg. cl("Fighter", 21)
 * eg. cl()
 */
public class ClassLevelCommand extends PCGenCommand
{

	/**
	 * Constructor
	 */
	public ClassLevelCommand()
	{
		numberOfParameters = -1;
	}

	/**
	 * Gets the name of the function handled by this class.
	 * @return The name of the function.
	 */
    @Override
	public String getFunctionName()
	{
		return "CLASSLEVEL";
	}

    @Override
	public boolean updateVariables(final PJEP jep)
	{
		boolean updated = false;
		if (jep.removeVariable("CL") != null)
		{
			updated = true;
		}

		String src = variableSource;
		if ((src == null) || !src.startsWith("CLASS:"))
		{
			return updated;
		}
		src = src.substring(6);

		PlayerCharacter pc = null;
		if (parent instanceof VariableProcessor)
		{
			pc = ((VariableProcessor) parent).getPc();
		}
		else if (parent instanceof PlayerCharacter)
		{
			pc = (PlayerCharacter) parent;
		}
		if (pc == null)
		{
			return updated;
		}

		final Number result = new Double(pc.getClassLevelString(src, false));
		jep.addVariable("CL", result.doubleValue());

		return true;
	}

	/**
	 * Runs classlevel on the inStack. The parameter is popped
	 * off the <code>inStack</code>, and the variable's value is
	 * pushed back to the top of <code>inStack</code>.
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

        int paramCount = curNumberOfParameters;

		//
		// If there are no parameters and this is used in a CLASS file, then use the
		// class name
		//
		if (paramCount == 0)
		{
			String src = variableSource;
			if (src.startsWith("CLASS:"))
			{
				src = src.substring(6);
				inStack.push(src);
				++paramCount;
			}
		}

		//
		// have to do this in reverse order...this is a stack afterall
		//
        final Object param1;
        Object param2 = null;
        if (paramCount == 1)
		{
			param1 = inStack.pop();
		}
		else if (paramCount == 2)
		{
			param2 = inStack.pop();
			param1 = inStack.pop();

			if (param2 instanceof Integer)
			{
				// Do Nothing, param is already an integer
			}
			else if (param2 instanceof Double)
			{
				param2 = ((Double) param2).intValue();
			}
			else
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
			PlayerCharacter pc = null;
			if (parent instanceof VariableProcessor)
			{
				pc = ((VariableProcessor) parent).getPc();
			}
			else if (parent instanceof PlayerCharacter)
			{
				pc = (PlayerCharacter) parent;
			}
			if (pc == null)
			{
				throw new ParseException("Invalid parent (no PC): "
					+ parent.getClass().getName());
			}

			// ";BEFORELEVEL="
			String cl = (String) param1;
			if (param2 != null)
			{
				cl += ";BEFORELEVEL=" + param2.toString();
			}

            final Object result = new Double(pc.getClassLevelString(cl, false));

            inStack.push(result);
		}
		else
		{
			throw new ParseException("Invalid parameter type");
		}
	}
}
