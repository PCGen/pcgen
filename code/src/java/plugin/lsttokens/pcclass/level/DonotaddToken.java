package plugin.lsttokens.pcclass.level;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;




//import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
//import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
//import pcgen.cdom.base.FormulaFactory;
//import pcgen.cdom.base.PrimitiveChoiceSet;
//import pcgen.cdom.base.TransitionChoice;
//import pcgen.cdom.choiceset.SpellReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
//import pcgen.cdom.list.ClassSpellList;
//import pcgen.cdom.list.DomainSpellList;
//import pcgen.core.PCClass;
//import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DONOTADD Token
 */
public class DonotaddToken extends AbstractToken implements
		CDOMPrimaryToken<PCClassLevel>
{
	@Override
	public String getTokenName()
	{
		return "DONOTADD";
	}

	public boolean parse(LoadContext context, PCClassLevel po, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		boolean foundAny = false;
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if ("HITDIE".equals(tokText))
			{
				context.getObjectContext().put(po, ObjectKey.DONTADD_HITDIE, Boolean.TRUE);
				foundAny = true;
			}
			else if ("SKILLPOINTS".equals(tokText))
			{
				context.getObjectContext().put(po, ObjectKey.DONTADD_SKILLPOINTS, Boolean.TRUE);
				foundAny = true;
			}
			else
			{
				Logging.errorPrint(getTokenName() + " encountered an invalid 'Do Not Add' type: " + value);
				Logging.errorPrint("  Legal values are: HITDIE, SKILLPOINTS");
			}
		}

		return foundAny;
	}

	public String[] unparse(LoadContext context, PCClassLevel po)
	{
		Boolean increaseHitDice = context.getObjectContext().getObject(po, ObjectKey.DONTADD_HITDIE);
		Boolean increaseSkills  = context.getObjectContext().getObject(po, ObjectKey.DONTADD_SKILLPOINTS);

		if ((increaseHitDice == null) && (increaseSkills == null))
		{
			return null;
		}

		StringBuilder sb = new StringBuilder();
		if (increaseHitDice != null)
		{
			sb.append("HITDIE");
		}


		if (increaseSkills != null)
		{
			if (increaseHitDice != null)
			{
				sb.append(Constants.PIPE);
			}
			sb.append("SKILLPOINTS");
		}

		return new String[] { sb.toString() };
	}

	public Class<PCClassLevel> getTokenClass()
	{
		return PCClassLevel.class;
	}
}
