/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.AbilityTargetSelector;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deal with FEAT token
 */
public class FeatToken extends AbstractTokenWithSeparator<Domain> implements
		CDOMPrimaryToken<Domain>
{
	public static final Class<Ability> ABILITY_CLASS = Ability.class;

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
		Domain obj, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;

		ReferenceManufacturer<Ability> rm = context.ref.getManufacturer(
				ABILITY_CLASS, AbilityCategory.FEAT);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					return new ParseResult.Fail("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value, context);
				}
				context.getListContext().removeAllFromList(getTokenName(), obj,
						Ability.FEATLIST);
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
									getTokenName(), AbilityCategory.FEAT, ref,
									Nature.AUTOMATIC);
							context.obj.addToList(obj, ListKey.CHOOSE_ACTOR,
									ats);
							loadList = false;
						}
					}
				}
				if (loadList)
				{
					AssociatedPrereqObject assoc = context.getListContext()
							.addToList(getTokenName(), obj, Ability.FEATLIST,
									ability);
					assoc.setAssociation(AssociationKey.NATURE,
							Nature.AUTOMATIC);
					assoc.setAssociation(AssociationKey.CATEGORY,
							AbilityCategory.FEAT);
					if (choices != null)
					{
						assoc.setAssociation(AssociationKey.ASSOC_CHOICES,
								choices);
					}
				}
			}
			first = false;
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Domain domain)
	{
		AssociatedChanges<CDOMReference<Ability>> changes = context
				.getListContext().getChangesInList(getTokenName(), domain,
						Ability.FEATLIST);
		Changes<ChooseResultActor> actors = context.getObjectContext()
				.getListChanges(domain, ListKey.CHOOSE_ACTOR);
		MapToList<CDOMReference<Ability>, AssociatedPrereqObject> added = changes
				.getAddedAssociations();
		Collection<CDOMReference<Ability>> removedItems = changes.getRemoved();
		StringBuilder sb = new StringBuilder();
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			sb.append(Constants.LST_DOT_CLEAR);
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName() + " does not support "
					+ Constants.LST_DOT_CLEAR_DOT);
			return null;
		}
		if (added != null && !added.isEmpty())
		{
			boolean needsPipe = sb.length() != 0;
			for (CDOMReference<Ability> ref : added.getKeySet())
			{
				String lstFormat = ref.getLSTformat(false);
				for (int i = 0; i < added.sizeOfListFor(ref); i++)
				{
					if (needsPipe)
					{
						sb.append(Constants.PIPE);
					}
					needsPipe = true;
					sb.append(lstFormat);
				}
			}
		}
		Collection<ChooseResultActor> addedActors = actors.getAdded();
		if (addedActors != null)
		{
			for (ChooseResultActor cra : addedActors)
			{
				if (getTokenName().equals(cra.getSource()))
				{
					try
					{
						sb.append(cra.getLstFormat());
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
		if (sb.length() == 0)
		{
			return null;
		}
		return new String[] { sb.toString() };
	}

	@Override
	public Class<Domain> getTokenClass()
	{
		return Domain.class;
	}
}
