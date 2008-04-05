package plugin.jepcommands;

import org.nfunk.jep.ParseException;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.VariableProcessor;
import pcgen.util.Logging;
import pcgen.util.PCGenCommand;

import java.util.Stack;

/**
 * Deals with skill() JEP commands
 *
 * eg. skill("rank", "Swim")
 * eg. skill("total", "Swim")
 * eg. skill("modifier", "Swim")
 * eg. skill("totalrank", "Swim")
 */
public class SkillInfoCommand extends PCGenCommand
{

	/**
	 * Constructor
	 */
	public SkillInfoCommand()
	{
		numberOfParameters = 2;
	}

	public String getFunctionName()
	{
		return "SKILLINFO";
	}

	/**
	 * Runs skill on the inStack. The parameter is popped
	 * off the <code>inStack</code>, and the variable's value is
	 * pushed back to the top of <code>inStack</code>.
	 * @param inStack the jep stack
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
	public void run(final Stack inStack) throws ParseException
	{
		// check the stack
		checkStack(inStack);

		// get the parameters from the stack
		//
		// have to do this in reverse order...this is a stack afterall
		//
		final Object param2 = inStack.pop();
		final Object param1 = inStack.pop();

		if ((param1 instanceof String) && (param2 instanceof String))
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

			final Skill aSkill = pc.getSkillKeyed((String) param2);

			Object result = null;
			if (aSkill != null)
			{
				if ("modifier".equalsIgnoreCase((String) param1))
				{
					result = (double) aSkill.modifier(pc).intValue(); // aSkill.modifier() returns Integer
				}
				else if ("rank".equalsIgnoreCase((String) param1))
				{
					result = aSkill.getRank().doubleValue(); // aSkill.getRank() returns Float
				}
				else if ("total".equalsIgnoreCase((String) param1))
				{
					result = (double) aSkill.getTotalRank(pc).intValue() + aSkill.modifier(pc);
                }
				else if ("totalrank".equalsIgnoreCase((String) param1))
				{
					result = aSkill.getTotalRank(pc).doubleValue(); // aSkill.getTotalRank() returns Float
				}
				else
				{
					Logging.log(Logging.LST_ERROR,
						"Ignoring unknown parameter '" + param1
							+ "' in Skillinfo call: skillinfo(\"" + param1
							+ "\",\"" + param2 + "\")");
					result = (double) 0;
				}
			}
			else
			{
				result = (double) 0;
			}

			inStack.push(result);
		}
		else
		{
			throw new ParseException("Invalid parameter type");
		}
	}
}
