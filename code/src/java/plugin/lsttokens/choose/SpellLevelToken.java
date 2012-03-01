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
package plugin.lsttokens.choose;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.SpellLevelChooseInformation;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.SpellLevel;
import pcgen.cdom.helper.SpellLevelInfo;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.ParsingSeparator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * New chooser plugin, handles Spell Level.
 */
public class SpellLevelToken extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMSecondaryToken<CDOMObject>,
		PersistentChoiceActor<SpellLevel>
{

	@Override
	public String getParentToken()
	{
		return "CHOOSE";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	public ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		int pipeLoc = value.lastIndexOf('|');
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

		pipeLoc = value.indexOf('|');
		if (pipeLoc != -1)
		{
			String firstValue = value.substring(0, pipeLoc);
			try
			{
				Integer.valueOf(firstValue);
				Logging
					.deprecationPrint("CHOOSE:SPELLLEVEL with first argument integer is deprecated");
				return doDeprecatedParse(context, obj, activeValue);
			}
			catch (NumberFormatException e)
			{
				// ok
			}
		}

		ParsingSeparator sep = new ParsingSeparator(activeValue, '|');
		if (!sep.hasNext())
		{
			return new ParseResult.Fail("Found no arguments in "
				+ getFullName() + ": " + value);
		}
		List<SpellLevelInfo> sliList = new ArrayList<SpellLevelInfo>();
		while (sep.hasNext())
		{
			String token = sep.next();
			PrimitiveCollection<PCClass> pcf = context
					.getPrimitiveChoiceFilter(context.ref
							.getManufacturer(PCClass.class), token);
			if (!sep.hasNext())
			{
				return new ParseResult.Fail(
					"Expected minimum level argument after " + token + " in "
						+ getFullName() + ": " + value);
			}
			String minLevelString = sep.next();
			int minLevel;
			try
			{
				minLevel = Integer.parseInt(minLevelString);
			}
			catch (NumberFormatException e)
			{
				return new ParseResult.Fail("Badly formed minimum level: "
					+ minLevelString + " in " + getFullName() + " value: "
					+ value);
			}
			if (!sep.hasNext())
			{
				return new ParseResult.Fail(
					"Expected maximum level argument after " + minLevelString
						+ " in " + getFullName() + ": " + value);
			}
			String maxLevelString = sep.next();
			Formula maxLevel = FormulaFactory.getFormulaFor(maxLevelString);
			if (!maxLevel.isValid())
			{
				return new ParseResult.Fail("Max Level Formula in " + getTokenName()
						+ " was not valid: " + maxLevel.toString());
			}
			SpellLevelInfo sli = new SpellLevelInfo(pcf, minLevel, maxLevel);
			sliList.add(sli);
		}
		SpellLevelChooseInformation tc =
				new SpellLevelChooseInformation(getTokenName(), sliList);
		tc.setTitle(title);
		tc.setChoiceActor(this);
		context.obj.put(obj, ObjectKey.CHOOSE_INFO, tc);
		return ParseResult.SUCCESS;
	}

	private ParseResult doDeprecatedParse(LoadContext context, CDOMObject obj,
		String value)
	{
		if (value == null)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " requires additional arguments");
		}
		if (value.indexOf(',') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value);
		}
		List<String> bonuses = new ArrayList<String>();
		int bracketLoc;
		while ((bracketLoc = value.lastIndexOf('[')) != -1)
		{
			int closeLoc = value.indexOf("]", bracketLoc);
			if (closeLoc != value.length() - 1)
			{
				return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments does not contain matching brackets: " + value);
			}
			String bracketString = value.substring(bracketLoc + 1, closeLoc);
			if (bracketString.startsWith("BONUS:"))
			{
				// This is okay.
				bonuses.add(bracketString.substring(6));
			}
			else
			{
				return new ParseResult.Fail("CHOOSE:" + getTokenName()
					+ " arguments may not contain [" + bracketString
					+ "] without BONUS: : " + value);
			}
			value = value.substring(0, bracketLoc);
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value);
		}
		if (value.indexOf("||") != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value);
		}
		int pipeLoc = value.indexOf("|");
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " must have two or more | delimited arguments : " + value);
		}
		String startString = value.substring(0, pipeLoc);
		try
		{
			Integer.parseInt(startString);
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " first argument must be an Integer : " + value);
		}
		String newVal = value.substring(pipeLoc + 1);
		try
		{
			String repl = newVal.replaceAll("TYPE=", "SPELLTYPE=").replaceAll("TYPE\\.", "SPELLTYPE=");
			if (context.processToken(obj, "CHOOSE", "SPELLLEVEL|" + repl))
			{
				for (String bonus : bonuses)
				{
					String b = bonus.replace("CLASS=%;LEVEL=%", "%LIST");
					if (b.indexOf("=%") != -1)
					{
						return new ParseResult.Fail("CHOOSE:" + getTokenName()
							+ " failure in BONUS: " + bonus
							+ " did not understand items with =%");
					}
					if (!context.processToken(obj, "BONUS", b))
					{
						return new ParseResult.Fail("CHOOSE:" + getTokenName()
							+ " failure in BONUS: " + b);
					}
				}
				return ParseResult.SUCCESS;
			}
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " failure: " + value);
		}
		catch (PersistenceLayerException e)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " failure: " + value + " " + e.getLocalizedMessage());
		}
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	@Override
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
		sb.append(tc.getLSTformat());
		String title = tc.getTitle();
		if (!title.equals(getDefaultTitle()))
		{
			sb.append("|TITLE=");
			sb.append(title);
		}
		return new String[]{sb.toString()};
	}

	@Override
	public void applyChoice(CDOMObject owner, SpellLevel st, PlayerCharacter pc)
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
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
		SpellLevel choice)
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

	@Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
		SpellLevel choice)
	{
		pc.addAssoc(owner, getListKey(), choice);
		pc.addAssociation(owner, encodeChoice(choice));
	}

	@Override
	public List<SpellLevel> getCurrentlySelected(CDOMObject owner,
		PlayerCharacter pc)
	{
		return pc.getAssocList(owner, getListKey());
	}

	@Override
	public boolean allow(SpellLevel choice, PlayerCharacter pc,
		boolean allowStack)
	{
		/*
		 * This is universally true, as any filter for qualify, etc. was dealt
		 * with by the ChoiceSet built during parse
		 */
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "SPELLLEVEL";
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	protected String getDefaultTitle()
	{
		return "Choose Spell Level";
	}

	protected AssociationListKey<SpellLevel> getListKey()
	{
		return AssociationListKey.CHOOSE_SPELLLEVEL;
	}

	@Override
	public SpellLevel decodeChoice(String s)
	{
		return SpellLevel.decodeChoice(s);
	}

	@Override
	public String encodeChoice(SpellLevel choice)
	{
		return choice.toString();
	}

}
