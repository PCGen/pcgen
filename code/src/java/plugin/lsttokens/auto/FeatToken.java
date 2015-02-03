/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.auto;

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
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.content.ConditionalSelectionActor;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.AbilitySelector;
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
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class FeatToken extends AbstractTokenWithSeparator<CDOMObject> implements
		CDOMSecondaryToken<CDOMObject>
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	/** 
	 * We use a specific source string so we can split our choice actor entries 
	 * from other FEAT tag entries. */
	private static final String SOURCE = "AUTO:FEAT";

	@Override
	public String getParentToken()
	{
		return "AUTO";
	}

	private String getFullName()
	{
		return getParentToken() + Constants.COLON + getTokenName();
	}

	@Override
	public String getTokenName()
	{
		return "FEAT";
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
		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Nature.AUTOMATIC;
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		String token = tok.nextToken();

		if (looksLikeAPrerequisite(token))
		{
			return new ParseResult.Fail("Cannot have only PRExxx subtoken in " + getFullName()
							+ ": " + value, context);
		}

		ArrayList<PrereqObject> edgeList = new ArrayList<PrereqObject>();

		CDOMReference<AbilityList> abilList = AbilityList
				.getAbilityListReference(category, nature);

		boolean first = true;
		boolean allowPre = true;

		ReferenceManufacturer<Ability> rm = context.getReferenceContext().getManufacturer(
				ABILITY_CLASS, AbilityCategory.FEAT);

		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					return new ParseResult.Fail("  Non-sensical "
							+ getFullName()
							+ ": .CLEAR was not the first list item: " + value, context);
				}
				context.getListContext().removeAllFromList(getFullName(), obj,
						abilList);
				allowPre = false;
			}
			else if (token.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				String clearText = token.substring(7);
				CDOMReference<Ability> ref = TokenUtilities.getTypeOrPrimitive(rm, clearText);
				if (ref == null)
				{
					return ParseResult.INTERNAL_ERROR;
				}
				context.getListContext().removeFromList(getFullName(), obj,
						abilList, ref);
				allowPre = false;
			}
			else if (Constants.LST_PERCENT_LIST.equals(token))
			{
				ConditionalSelectionActor<AbilitySelection> cca =
						new ConditionalSelectionActor<AbilitySelection>(
							new AbilitySelector(SOURCE, AbilityCategory.FEAT,
								Nature.AUTOMATIC));
				edgeList.add(cca);
				context.getObjectContext().addToList(obj, ListKey.NEW_CHOOSE_ACTOR, cca);
				allowPre = false;
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
								SOURCE, category, ref, nature);
							context.getObjectContext().addToList(obj,
								ListKey.NEW_CHOOSE_ACTOR, ats);
							edgeList.add(ats);
							loadList = false;
						}
					}
				}
				if (loadList)
				{
					AssociatedPrereqObject assoc = context.getListContext()
							.addToList(getFullName(), obj, abilList, ability);
					assoc.setAssociation(AssociationKey.NATURE, nature);
					assoc.setAssociation(AssociationKey.CATEGORY, category);
					if (choices != null)
					{
						assoc.setAssociation(AssociationKey.ASSOC_CHOICES, choices);
					}
					edgeList.add(assoc);
				}
			}
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return ParseResult.SUCCESS;
			}
			first = false;
			token = tok.nextToken();
			if (looksLikeAPrerequisite(token))
			{
				break;
			}
		}

		if (!allowPre)
		{
			return new ParseResult.Fail(
				"Cannot use PREREQs when using .CLEAR, .CLEAR., or %LIST in "
					+ getTokenName(), context);
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				return new ParseResult.Fail("   (Did you put feats after the " + "PRExxx tags in "
								+ getFullName() + ":?)", context);
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

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<String> returnSet = new TreeSet<String>();
		List<String> returnList = new ArrayList<String>();
		MapToList<List<Prerequisite>, CDOMReference<Ability>> m =
				new HashMapToList<List<Prerequisite>, CDOMReference<Ability>>();
		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Nature.AUTOMATIC;

		CDOMReference<AbilityList> abilList = AbilityList
				.getAbilityListReference(category, nature);
		AssociatedChanges<CDOMReference<Ability>> changes = context
				.getListContext()
				.getChangesInList(getFullName(), obj, abilList);
		Collection<CDOMReference<Ability>> removedItems = changes.getRemoved();
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
			returnList.add(Constants.LST_DOT_CLEAR_DOT
					+ ReferenceUtilities.joinLstFormat(removedItems,
							"|.CLEAR.", true));
		}

		Changes<ChooseSelectionActor<?>> listChanges =
				context.getObjectContext().getListChanges(obj,
					ListKey.NEW_CHOOSE_ACTOR);
		Collection<ChooseSelectionActor<?>> listAdded = listChanges.getAdded();
		if (listAdded != null && !listAdded.isEmpty())
		{
			for (ChooseSelectionActor<?> csa : listAdded)
			{
				if (csa.getSource().equals(SOURCE))
				{
					try
					{
						returnList.add(csa.getLstFormat());
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

		MapToList<CDOMReference<Ability>, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (mtl != null)
		{
			for (CDOMReference<Ability> ab : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
				{
					m.addToListFor(assoc.getPrerequisiteList(), ab);
				}
			}
		}

		for (List<Prerequisite> prereqs : m.getKeySet())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(ReferenceUtilities.joinLstFormat(m.getListFor(prereqs),
					Constants.PIPE));
			if (prereqs != null && !prereqs.isEmpty())
			{
				sb.append(Constants.PIPE);
				sb.append(getPrerequisiteString(context, prereqs));
			}
			returnSet.add(sb.toString());
		}
		returnList.addAll(returnSet);
		return returnList.toArray(new String[returnList.size()]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
