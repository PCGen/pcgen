package plugin.jepcommands;

import java.util.Stack;

import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.VariableProcessor;
import pcgen.util.PCGenCommand;
import pcgen.util.PJEP;

import org.nfunk.jep.ParseException;

/**
 * JEP command for class level (cl)
 *
 * eg. classlevel("Fighter")
 * eg. classlevel("Fighter", "APPLIEDAS=NONEPIC")
 * eg. classlevel("APPLIEDAS=NONEPIC") [in CLASS LST file only]
 * eg. classlevel() [in CLASS LST file only]
 */
public class ClassLevelCommand extends PCGenCommand
{

	@Override
	public boolean checkNumberOfParameters(int arg0)
	{
		return arg0 >= 0 && arg0 <= 2;
	}

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

		final Number result = Double.valueOf(pc.getClassLevelString(src, false));
		jep.addVariable("CL", result.doubleValue());

		return true;
	}

	/**
	 * Runs classlevel on the inStack. The parameter is popped
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

		int paramCount = curNumberOfParameters;

		String applied = null;
		String className = null;

		if (paramCount > 2)
		{
			throw new ParseException("Invalid number of parameters");
		}

		if (paramCount >= 2)
		{
			String p2 = inStack.pop().toString();
			if (p2.startsWith("APPLIEDAS="))
			{
				applied = p2.substring(10);
			}
		}

		if (paramCount >= 1)
		{
			String p1 = inStack.pop().toString();
			if (p1.startsWith("APPLIEDAS="))
			{
				if (applied != null)
				{
					throw new ParseException("Formula had two APPLIEDAS= entries");
				}
				applied = p1.substring(10);
			}
			else
			{
				//Should be a class name
				className = p1;
			}
		}

		/*
		 * If there was no parameter showing the class, and this is used in a
		 * CLASS file, then use the class name
		 */
		if (className == null)
		{
			String src = variableSource;
			if (src.startsWith("CLASS:"))
			{
				className = src.substring(6);
			}
		}

		if (className == null)
		{
			throw new ParseException("Unable to determine class name");
		}

		PlayerCharacter pc = getPC();

		String cl = className;
		if (applied != null)
		{
			if ("NONEPIC".equalsIgnoreCase(applied))
			{
				GameMode mode = SettingsHandler.getGameAsProperty().get();
				//Add 1 since game mode is inclusive, but BEFORELEVEL is not!
				int limit = mode.getMaxNonEpicLevel() + 1;
				if (limit == Integer.MAX_VALUE)
				{
					throw new ParseException("Game Mode has no EPIC limit");
				}
				cl += ";BEFORELEVEL=" + limit;
			}
			else
			{
				throw new ParseException("Did not understand APPLIEDAS=" + applied);
			}
		}

		Double result = Double.valueOf(pc.getClassLevelString(cl, false));
		inStack.push(result);
	}

	private PlayerCharacter getPC() throws ParseException
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
			throw new ParseException("Invalid parent (no PC): " + parent.getClass().getName());
		}
		return pc;
	}
}
