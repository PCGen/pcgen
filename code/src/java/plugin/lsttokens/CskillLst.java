/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.reference.PatternMatchingReference;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * @author djones4
 *
 */
public class CskillLst extends AbstractTokenWithSeparator<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>, ChooseResultActor
{
	private static final Class<Skill> SKILL_CLASS = Skill.class;

	@Override
	public String getTokenName()
	{
		return "CSKILL";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
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
					return new ParseResult.Fail("  Non-sensical "
							+ getTokenName()
							+ ": .CLEAR was not the first list item");
				}
				context.getObjectContext().removeList(obj, ListKey.CSKILL);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				String clearText = tokText.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					context.getObjectContext().removeFromList(obj,
							ListKey.CSKILL,
							context.ref.getCDOMAllReference(SKILL_CLASS));
				}
				else if (Constants.LST_LIST.equals(clearText))
				{
					context.getObjectContext().removeFromList(obj,
							ListKey.CHOOSE_ACTOR, this);
				}
				else
				{
					CDOMReference<Skill> ref = TokenUtilities
							.getTypeOrPrimitive(context, SKILL_CLASS, clearText);
					if (ref == null)
					{
						return new ParseResult.Fail(
								"  Error was encountered while parsing "
										+ getTokenName());
					}
					context.getObjectContext().removeFromList(obj,
							ListKey.CSKILL, ref);
				}
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
					context.getObjectContext().addToList(obj, ListKey.CSKILL,
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
							return new ParseResult.Fail("  Error was encountered while parsing "
											+ getTokenName());
						}
						context.getObjectContext().addToList(obj,
								ListKey.CSKILL, ref);
					}
				}
			}
			first = false;
		}
		if (foundAny && foundOther)
		{
			return new ParseResult.Fail("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
		}
		return ParseResult.SUCCESS;
	}

	private CDOMReference<Skill> getSkillReference(LoadContext context,
			String tokText)
	{
		if (tokText.endsWith(Constants.PERCENT))
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
				.getListChanges(obj, ListKey.CSKILL);
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
					+ ReferenceUtilities
							.joinLstFormat(removedItems, "|.CLEAR."));
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
			for (ChooseResultActor cra : listAdded)
			{
				if (cra.getSource().equals(getTokenName()))
				{
					try
					{
						list.add(cra.getLstFormat());
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
								+ e);
						return null;
					}
				}
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
			pc.addGlobalCost(SkillCost.CLASS, skill, obj);
		}
	}

	public void remove(PlayerCharacter pc, CDOMObject obj, String o)
	{
		Skill skill = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(SKILL_CLASS, o);
		if (skill != null)
		{
			pc.removeGlobalCost(SkillCost.CLASS, skill, obj);
		}
	}

	public String getSource()
	{
		return getTokenName();
	}

	public String getLstFormat()
	{
		return "LIST";
	}
}
