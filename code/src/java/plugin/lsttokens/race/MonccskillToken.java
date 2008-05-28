package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.reference.PatternMatchingReference;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with MONCCSKILL Token
 */
public class MonccskillToken extends AbstractToken implements
		CDOMPrimaryToken<Race>
{

	private static final Class<Skill> SKILL_CLASS = Skill.class;

	@Override
	public String getTokenName()
	{
		return "MONCCSKILL";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		boolean firstToken = true;
		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!firstToken)
				{
					Logging.errorPrint("Non-sensical situation was "
							+ "encountered while parsing " + getTokenName()
							+ ": When used, .CLEAR must be the first argument");
					return false;
				}
				context.getListContext().removeAllFromList(getTokenName(),
						race, PCClass.MONSTER_SKILL_LIST);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Skill> skill;
				String clearText = tokText.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					skill = context.ref.getCDOMAllReference(SKILL_CLASS);
				}
				else
				{
					skill = getSkillReference(context, race, clearText);
				}
				if (skill == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName());
					return false;
				}
				context.getListContext().removeFromList(getTokenName(), race,
						PCClass.MONSTER_SKILL_LIST, skill);
			}
			else
			{
				/*
				 * Note this is done one-by-one, because .CLEAR. token type
				 * needs to be able to perform the unlink. That could be
				 * changed, but the increase in complexity isn't worth it.
				 * (Changing it to a grouping object that didn't place links in
				 * the graph would also make it harder to trace the source of
				 * class skills, etc.)
				 */
				CDOMReference<Skill> skill;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					skill = context.ref.getCDOMAllReference(SKILL_CLASS);
				}
				else
				{
					foundOther = true;
					skill = getSkillReference(context, race, tokText);
				}
				if (skill == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName());
					return false;
				}
				AssociatedPrereqObject apo = context.getListContext()
						.addToList(getTokenName(), race,
								PCClass.MONSTER_SKILL_LIST, skill);
				apo.setAssociation(AssociationKey.SKILL_COST,
						SkillCost.CROSS_CLASS);
			}
			firstToken = false;
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	private CDOMReference<Skill> getSkillReference(LoadContext context, Race r,
			String tokText)
	{
		// if (Constants.LST_LIST.equals(tokText))
		// {
		// return new AssociationReference<Skill>(Skill.class, context.ref
		// .getCDOMAllReference(SKILL_CLASS), r);
		// }
		// else
		if (tokText.endsWith(Constants.LST_PATTERN))
		{
			return new PatternMatchingReference<Skill>(Skill.class, context.ref
					.getCDOMAllReference(SKILL_CLASS), tokText);
		}
		else
		{
			return TokenUtilities.getTypeOrPrimitive(context, SKILL_CLASS,
					tokText);
		}
	}

	public String[] unparse(LoadContext context, Race race)
	{
		AssociatedChanges<CDOMReference<Skill>> changes = context
				.getListContext().getChangesInList(getTokenName(), race,
						PCClass.MONSTER_SKILL_LIST);
		List<String> list = new ArrayList<String>();
		Collection<CDOMReference<Skill>> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR_DOT
					+ ReferenceUtilities
							.joinLstFormat(removedItems, "|.CLEAR."));
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		MapToList<CDOMReference<Skill>, AssociatedPrereqObject> map = changes
				.getAddedAssociations();
		if (map != null && !map.isEmpty())
		{
			Set<CDOMReference<Skill>> added = map.getKeySet();
			for (CDOMReference<Skill> ab : added)
			{
				for (AssociatedPrereqObject assoc : map.getListFor(ab))
				{
					if (!SkillCost.CROSS_CLASS.equals(assoc
							.getAssociation(AssociationKey.SKILL_COST)))
					{
						context.addWriteMessage("Skill Cost must be "
								+ "CROSS_CLASS for Token " + getTokenName());
						return null;
					}
				}
			}
			list.add(ReferenceUtilities.joinLstFormat(added, Constants.PIPE));
		}
		if (list.isEmpty())
		{
			// Zero indicates no add or clear
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
