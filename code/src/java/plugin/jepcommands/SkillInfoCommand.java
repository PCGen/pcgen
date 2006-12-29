package plugin.jepcommands;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.VariableProcessor;
import pcgen.util.PCGenCommand;

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
	 * @param inStack
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
	public void run(Stack inStack) throws ParseException
	{
		// check the stack
		checkStack(inStack);

		// get the parameters from the stack
		//
		// have to do this in reverse order...this is a stack afterall
		//
		Object param2 = inStack.pop();
		Object param1 = inStack.pop();

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
				if (((String) param1).equalsIgnoreCase("modifier"))
				{
					result = new Double(aSkill.modifier(pc).intValue()); // aSkill.modifier() returns Integer
				}
				else if (((String) param1).equalsIgnoreCase("rank"))
				{
					result = new Double(aSkill.getRank().doubleValue()); // aSkill.getRank() returns Float
				}
				else if (((String) param1).equalsIgnoreCase("total"))
				{
					result =
							new Double(aSkill.getTotalRank(pc).intValue()
								+ aSkill.modifier(pc).intValue());
				}
				else if (((String) param1).equalsIgnoreCase("totalrank"))
				{
					result = new Double(aSkill.getTotalRank(pc).doubleValue()); // aSkill.getTotalRank() returns Float
				}
			}
			else
			{
				result = new Double(0);
			}

			inStack.push(result);
		}
		else
		{
			throw new ParseException("Invalid parameter type");
		}
	}
}
