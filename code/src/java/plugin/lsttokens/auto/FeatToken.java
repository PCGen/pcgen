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
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.ConditionalChoiceActor;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
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
		CDOMSecondaryToken<CDOMObject>, ChooseResultActor
{
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	public String getParentToken()
	{
		return "AUTO";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
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

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			return new ParseResult.Fail("Cannot have only PRExxx subtoken in " + getFullName()
							+ ": " + value);
		}

		ArrayList<PrereqObject> edgeList = new ArrayList<PrereqObject>();

		CDOMReference<AbilityList> abilList = AbilityList
				.getAbilityListReference(category, nature);

		boolean first = true;

		ReferenceManufacturer<Ability> rm = context.ref.getManufacturer(
				ABILITY_CLASS, AbilityCategory.FEAT);

		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					return new ParseResult.Fail("  Non-sensical "
							+ getFullName()
							+ ": .CLEAR was not the first list item: " + value);
				}
				context.getListContext().removeAllFromList(getFullName(), obj,
						abilList);
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
			}
			else if ("%LIST".equals(token))
			{
				ConditionalChoiceActor cca = new ConditionalChoiceActor(this);
				edgeList.add(cca);
				context.obj.addToList(obj, ListKey.CHOOSE_ACTOR, cca);
			}
			else
			{
				CDOMReference<Ability> ability = TokenUtilities.getTypeOrPrimitive(rm, token);
				if (ability == null)
				{
					return ParseResult.INTERNAL_ERROR;
				}
				ability.setRequiresTarget(true);
				AssociatedPrereqObject assoc = context.getListContext()
						.addToList(getFullName(), obj, abilList, ability);
				assoc.setAssociation(AssociationKey.NATURE, nature);
				assoc.setAssociation(AssociationKey.CATEGORY, category);
				if (token.indexOf('(') != -1)
				{
					List<String> choices = new ArrayList<String>();
					AbilityUtilities.getUndecoratedName(token, choices);
					assoc.setAssociation(AssociationKey.ASSOC_CHOICES, choices);
				}
				edgeList.add(assoc);
			}
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return ParseResult.SUCCESS;
			}
			first = false;
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				return new ParseResult.Fail("   (Did you put feats after the " + "PRExxx tags in "
								+ getFullName() + ":?)");
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
		Set<String> returnSet = new TreeSet<String>();
		List<String> returnList = new ArrayList<String>();
		MapToList<List<Prerequisite>, CDOMReference<Ability>> m = new HashMapToList<List<Prerequisite>, CDOMReference<Ability>>();
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

		Changes<ChooseResultActor> listChanges =
				context.getObjectContext().getListChanges(obj,
					ListKey.CHOOSE_ACTOR);
		Collection<ChooseResultActor> listAdded = listChanges.getAdded();
		if (listAdded != null && !listAdded.isEmpty())
		{
			for (ChooseResultActor csa : listAdded)
			{
				if (csa.getSource().equals(getTokenName()))
				{
					returnList.add(Constants.LST_PRECENTLIST);
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

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void apply(PlayerCharacter pc, CDOMObject obj, String choice)
	{
		pc.addAppliedAbility(obj, decodeChoice(choice), Nature.AUTOMATIC);
	}

	public String getLstFormat() throws PersistenceLayerException
	{
		return "%LIST";
	}

	public String getSource()
	{
		return getTokenName();
	}

	public void remove(PlayerCharacter pc, CDOMObject obj, String choice)
	{
		AbilitySelection as = decodeChoice(choice);
		pc.removeAppliedAbility(obj, as, Nature.AUTOMATIC);
	}

	public AbilitySelection decodeChoice(String s)
	{
		Ability ability = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(Ability.class,
						AbilityCategory.FEAT, s);

		if (ability == null)
		{
			List<String> choices = new ArrayList<String>();
			String baseKey = AbilityUtilities.getUndecoratedName(s, choices);
			ability = Globals.getContext().ref
					.silentlyGetConstructedCDOMObject(Ability.class,
							AbilityCategory.FEAT, baseKey);
			if (ability == null)
			{
				throw new IllegalArgumentException("String in decodeChoice "
						+ "must be a Feat Key "
						+ "(or Feat Key with Selection if appropriate), was: "
						+ s);
			}
			return new AbilitySelection(ability, Nature.NORMAL, choices.get(0));
		}
		else if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			/*
			 * MULT:YES, CHOOSE:NOCHOICE can land here
			 * 
			 * TODO There needs to be better validation at some point that this
			 * is proper (meaning it is actually CHOOSE:NOCHOICE!)
			 */
			return new AbilitySelection(ability, Nature.NORMAL, "");
		}
		else
		{
			return new AbilitySelection(ability, Nature.NORMAL);
		}
	}
}
