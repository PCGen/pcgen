/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.PatternMatchingReference;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class CcskillLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>, ChooseResultActor
{

	private static final Class<Skill> SKILL_CLASS = Skill.class;

	@Override
	public String getTokenName()
	{
		return "CCSKILL";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		boolean first = true;
		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item");
					return false;
				}
				context.getObjectContext().removeList(obj, ListKey.CCSKILL);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Skill> ref;
				String clearText = tokText.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					ref = context.ref.getCDOMAllReference(SKILL_CLASS);
				}
				else
				{
					ref = TokenUtilities.getTypeOrPrimitive(context,
							SKILL_CLASS, clearText);
				}
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName());
					return false;
				}
				context.getObjectContext().removeFromList(obj, ListKey.CCSKILL,
						ref);
			}
			else
			{
				/*
				 * Note this HAS to be done one-by-one, because the
				 * .clearChildNodeOfClass method above does NOT recognize the
				 * C/CC Skill object and therefore doesn't know how to search
				 * the sublists
				 */
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					context.getObjectContext().addToList(obj, ListKey.CCSKILL,
							context.ref.getCDOMAllReference(SKILL_CLASS));
				}
				else
				{
					foundOther = true;
					if (Constants.LST_LIST.equals(tokText))
					{
						context.getObjectContext().addToList(obj,
								ListKey.CHOOSE_ACTOR, this);
					}
					else
					{
						CDOMReference<Skill> ref = getSkillReference(context,
								tokText);
						if (ref == null)
						{
							Logging
									.errorPrint("  Error was encountered while parsing "
											+ getTokenName());
							return false;
						}
						context.getObjectContext().addToList(obj,
								ListKey.CCSKILL, ref);
					}
				}
			}
			first = false;
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	private CDOMReference<Skill> getSkillReference(LoadContext context,
			String tokText)
	{
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

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<CDOMReference<Skill>> changes = context.getObjectContext()
				.getListChanges(obj, ListKey.CCSKILL);
		Changes<ChooseResultActor> listChanges = context.getObjectContext()
				.getListChanges(obj, ListKey.CHOOSE_ACTOR);
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
					+ ReferenceUtilities.joinLstFormat(removedItems,
							",|.CLEAR."));
		}
		Collection<ChooseResultActor> listRemoved = listChanges.getRemoved();
		if (listRemoved != null && !listRemoved.isEmpty())
		{
			if (listRemoved.contains(this))
			{
				list.add(".CLEAR.LIST");
			}
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		Collection<CDOMReference<Skill>> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			list.add(ReferenceUtilities.joinLstFormat(added, Constants.PIPE));
		}
		Collection<ChooseResultActor> listAdded = listChanges.getAdded();
		if (listAdded != null && !listAdded.isEmpty())
		{
			if (listAdded.contains(this))
			{
				list.add("LIST");
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void apply(PlayerCharacter pc, CDOMObject obj, String o)
	{
		Skill skill = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(SKILL_CLASS, o);
		if (skill != null)
		{
			pc.addAssoc(obj, AssociationListKey.CCSKILL, skill);
		}
	}

	public void remove(PlayerCharacter pc, CDOMObject obj, String o)
	{
		Skill skill = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(SKILL_CLASS, o);
		if (skill != null)
		{
			pc.removeAssoc(obj, AssociationListKey.CCSKILL, skill);
		}
	}
}
