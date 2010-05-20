/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import java.util.List;

import pcgen.cdom.base.AbilityChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class AbilityToken extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMSecondaryToken<CDOMObject>,
		PersistentChoiceActor<AbilitySelection>
{

	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	protected ParseResult parseTokenWithSeparator(LoadContext context,
		ReferenceManufacturer<Ability> rm, Category<Ability> category,
		CDOMObject obj, String value)
	{
		int pipeLoc = value.indexOf('|');
		String activeValue;
		String title;
		if (pipeLoc == -1)
		{
			activeValue = value;
			title = getDefaultTitle();
		}
		else
		{
			String titleString = value.substring(pipeLoc + 1);
			if (titleString.startsWith("TITLE="))
			{
				title = titleString.substring(6);
				if (title.startsWith("\""))
				{
					title = title.substring(1, title.length() - 1);
				}
				activeValue = value.substring(0, pipeLoc);
			}
			else
			{
				activeValue = value;
				title = getDefaultTitle();
			}
		}

		PrimitiveChoiceSet<Ability> pcs = context.getChoiceSet(rm, activeValue);
		if (pcs == null)
		{
			return ParseResult.INTERNAL_ERROR;
		}
		if (!pcs.getGroupingState().isValid())
		{
			ComplexParseResult cpr = new ComplexParseResult();
			cpr.addErrorMessage("Invalid combination of objects was used in: "
				+ activeValue);
			cpr.addErrorMessage("  Check that ALL is not combined");
			cpr
				.addErrorMessage("  Check that a key is not joined with AND (,)");
			return cpr;
		}
		AbilityChooseInformation tc =
				new AbilityChooseInformation(getTokenName(), category, pcs);
		tc.setTitle(title);
		tc.setChoiceActor(this);
		context.obj.put(obj, ObjectKey.CHOOSE_INFO, tc);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		ChooseInformation<?> tc =
				context.getObjectContext()
					.getObject(cdo, ObjectKey.CHOOSE_INFO);
		if (tc == null)
		{
			return null;
		}
		if (!tc.getName().equals(getTokenName()))
		{
			// Don't unparse anything that isn't owned by this SecondaryToken
			/*
			 * TODO Either this really needs to be a check against the subtoken
			 * (which thus needs to be stored in the ChooseInfo) or there needs
			 * to be a loadtime check that no more than once CHOOSE subtoken
			 * uses the same AssociationListKey... :P
			 */
			return null;
		}
		if (!tc.getGroupingState().isValid())
		{
			context.addWriteMessage("Invalid combination of objects"
				+ " was used in: " + getParentToken() + ":" + getTokenName());
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(((AbilityChooseInformation) tc).getCategory());
		sb.append('|');
		sb.append(tc.getLSTformat());
		String title = tc.getTitle();
		if (!title.equals(getDefaultTitle()))
		{
			sb.append("|TITLE=");
			sb.append(title);
		}
		return new String[]{sb.toString()};
	}

	public void applyChoice(CDOMObject owner, AbilitySelection st,
		PlayerCharacter pc)
	{
		restoreChoice(pc, owner, st);
		List<ChooseSelectionActor<?>> actors =
				owner.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				ca.applyChoice(owner, st, pc);
			}
		}
		pc.addAssociation(owner, encodeChoice(st));
	}

	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
		AbilitySelection choice)
	{
		pc.removeAssoc(owner, getListKey(), choice);
		List<ChooseSelectionActor<?>> actors =
				owner.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				ca.removeChoice(owner, choice, pc);
			}
		}
		pc.removeAssociation(owner, encodeChoice(choice));
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
		AbilitySelection choice)
	{
		pc.addAssoc(owner, getListKey(), choice);
	}

	public List<AbilitySelection> getCurrentlySelected(CDOMObject owner,
		PlayerCharacter pc)
	{
		return pc.getAssocList(owner, getListKey());
	}

	public boolean allow(AbilitySelection choice, PlayerCharacter pc,
		boolean allowStack)
	{
		/*
		 * This is universally true, as any filter for qualify, etc. was dealt
		 * with by the ChoiceSet built during parse
		 */
		return true;
	}

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "ABILITY";
	}

	@Override
	public ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		if (isEmpty(value))
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " requires additional arguments");
		}
		if (hasIllegalSeparator('|', value))
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " has invalid placement of '|'");
		}
		int barLoc = value.indexOf('|');
		if (barLoc == -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " requires a CATEGORY and arguments : " + value);
		}
		String cat = value.substring(0, barLoc);
		Category<Ability> category =
				context.ref.getCategoryFor(ABILITY_CLASS, cat);
		if (category == null)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " found invalid CATEGORY: " + cat + " in value: " + value);
		}
		String abilities = value.substring(barLoc + 1);
		return parseTokenWithSeparator(context, context.ref.getManufacturer(
			ABILITY_CLASS, category), category, obj, abilities);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	protected String getDefaultTitle()
	{
		return "Ability choice";
	}

	protected AssociationListKey<AbilitySelection> getListKey()
	{
		return AssociationListKey.CHOOSE_ABILITY;
	}

	public AbilitySelection decodeChoice(String s)
	{
		return AbilitySelection.getAbilitySelectionFromPersistentFormat(s);
	}

	public String encodeChoice(AbilitySelection choice)
	{
		return choice.getPersistentFormat();
	}

}
