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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.AbilityTargetSelector;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class VFeatLst extends AbstractTokenWithSeparator<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>
{

	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "VFEAT";
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
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		String token = tok.nextToken();

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			return new ParseResult.Fail("Cannot have only PRExxx subtoken in "
					+ getTokenName() + ": " + value);
		}

		ArrayList<PrereqObject> edgeList = new ArrayList<PrereqObject>();
		boolean first = true;
		boolean foundClear = false;

		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Nature.VIRTUAL;
		CDOMReference<AbilityList> list = Ability.FEATLIST;

		ReferenceManufacturer<Ability> rm = context.ref.getManufacturer(
				ABILITY_CLASS, AbilityCategory.FEAT);

		while (true)
		{
			if (token.equals(Constants.LST_DOT_CLEAR))
			{
				if (!first)
				{
					return new ParseResult.Fail("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
				}
				context.getListContext().removeAllFromList(getTokenName(), obj,
						list);
				foundClear = true;
			}
			else
			{
				CDOMReference<Ability> ability = TokenUtilities.getTypeOrPrimitive(rm, token);
				if (ability == null)
				{
					return ParseResult.INTERNAL_ERROR;
				}
				ability.setRequiresTarget(true);
				boolean loadList = true;
				List<String> choices = null;
				if (token.indexOf('(') != -1)
				{
					choices = new ArrayList<String>();
					AbilityUtilities.getUndecoratedName(token, choices);
					if (choices.size() == 1)
					{
						if (Constants.LST_PERCENT_LIST.equals(choices.get(0))
								&& (ability instanceof CDOMSingleRef))
						{
							CDOMSingleRef<Ability> ref = (CDOMSingleRef<Ability>) ability;
							AbilityTargetSelector ats = new AbilityTargetSelector(
									getTokenName(), category, ref, nature);
							context.obj.addToList(obj, ListKey.CHOOSE_ACTOR,
									ats);
							edgeList.add(ats);
							loadList = false;
						}
					}
				}
				if (loadList)
				{
					AssociatedPrereqObject assoc = context.getListContext()
							.addToList(getTokenName(), obj, list, ability);
					assoc.setAssociation(AssociationKey.NATURE, nature);
					assoc.setAssociation(AssociationKey.CATEGORY, category);
					if (choices != null)
					{
						assoc.setAssociation(AssociationKey.ASSOC_CHOICES, choices);
					}
					edgeList.add(assoc);
				}
			}

			first = false;
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return ParseResult.SUCCESS;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		if (foundClear)
		{
			return new ParseResult.Fail(
					"Cannot use PREREQs when using .CLEAR in "
							+ getTokenName());
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				return new ParseResult.Fail("   (Did you put feats after the "
						+ "PRExxx tags in " + getTokenName() + ":?)");
			}
			for (PrereqObject edge : edgeList)
			{
				edge.addPrerequisite(prereq);
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<CDOMReference<Ability>> changes = context
				.getListContext().getChangesInList(getTokenName(), obj,
						Ability.FEATLIST);
		MapToList<CDOMReference<Ability>, AssociatedPrereqObject> added = changes
				.getAddedAssociations();
		Collection<CDOMReference<Ability>> removedItems = changes.getRemoved();
		List<String> returnList = new ArrayList<String>();
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			returnList.add(Constants.LST_DOT_CLEAR);
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName() + " does not support "
					+ Constants.LST_DOT_CLEAR_DOT);
			return null;
		}
		if (added != null && !added.isEmpty())
		{
			HashMapToList<List<Prerequisite>, CDOMReference<Ability>> m = new HashMapToList<List<Prerequisite>, CDOMReference<Ability>>();
			for (CDOMReference<Ability> ab : added.getKeySet())
			{
				for (AssociatedPrereqObject assoc : added.getListFor(ab))
				{
					m.addToListFor(assoc.getPrerequisiteList(), ab);
				}
			}

			Set<String> returnSet = new TreeSet<String>();
			for (List<Prerequisite> prereqs : m.getKeySet())
			{
				StringBuilder sb = new StringBuilder();
				sb.append(ReferenceUtilities.joinLstFormat(m.getListFor(prereqs), Constants.PIPE));
				if (prereqs != null && !prereqs.isEmpty())
				{
					sb.append(Constants.PIPE);
					sb.append(getPrerequisiteString(context, prereqs));
				}
				returnSet.add(sb.toString());
			}
			returnList.addAll(returnSet);
		}
		Changes<ChooseResultActor> actors = context.getObjectContext()
				.getListChanges(obj, ListKey.CHOOSE_ACTOR);
		Collection<ChooseResultActor> addedActors = actors.getAdded();
		if (addedActors != null)
		{
			for (ChooseResultActor cra : addedActors)
			{
				if (getTokenName().equals(cra.getSource()))
				{
					try
					{
						returnList.add(cra.getLstFormat());
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage(getTokenName()
								+ " encountered error: " + e.getMessage());
						return null;
					}
				}
			}
		}
		if (returnList.isEmpty())
		{
			return null;
		}
		return returnList.toArray(new String[returnList.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
