package plugin.jepcommands;

import java.util.Stack;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.VariableProcessor;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.util.Logging;
import pcgen.util.PCGenCommand;

import org.nfunk.jep.ParseException;

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

	/**
	 * Gets the name of the function handled by this class.
	 * @return The name of the function.
	 */
	@Override
	public String getFunctionName()
	{
		return "SKILLINFO";
	}

	/**
	 * Runs skill on the inStack. The parameter is popped
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
				throw new ParseException("Invalid parent (no PC): " + parent.getClass().getName());
			}

			Skill aSkill = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class,
				param2.toString());

			Object result = null;
			if (aSkill != null && pc.getDisplay().hasSkill(aSkill))
			{
				if ("modifier".equalsIgnoreCase((String) param1))
				{
					result =
						(double) SkillModifier.modifier(aSkill, pc); // aSkill.modifier() returns Integer
				}
				else if ("rank".equalsIgnoreCase((String) param1))
				{
					result = pc.getDisplay().getRank(aSkill).doubleValue(); // aSkill.getRank() returns Float
				}
				else if ("total".equalsIgnoreCase((String) param1))
				{
					result = (double) SkillRankControl.getTotalRank(pc, aSkill).intValue()
						+ SkillModifier.modifier(aSkill, pc);
				}
				else if ("totalrank".equalsIgnoreCase((String) param1))
				{
					result =
						SkillRankControl.getTotalRank(pc, aSkill).doubleValue(); // aSkill.getTotalRank() returns Float
				}
				else if ("stat".equalsIgnoreCase((String) param1))
				{
					result = (double) SkillModifier.getStatMod(aSkill, pc);
				}
				else if ("misc".equalsIgnoreCase((String) param1))
				{
					result = (double) (SkillModifier.modifier(aSkill, pc)
						- SkillModifier.getStatMod(aSkill, pc));
				}
				else
				{
					Logging.log(Logging.LST_ERROR, "Ignoring unknown parameter '" + param1
						+ "' in Skillinfo call: skillinfo(\"" + param1 + "\",\"" + param2 + "\")");
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
