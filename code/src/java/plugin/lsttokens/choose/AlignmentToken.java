/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.choiceset.SimpleChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class AlignmentToken extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMSecondaryToken<CDOMObject>,
		PersistentChoiceActor<PCAlignment>
{
	private static final Class<PCAlignment> PCALIGNMENT_CLASS = PCAlignment.class;

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
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		if (value.indexOf('[') != -1 || value.indexOf(']') != -1)
		{
			return new ParseResult.Fail(getParentToken() + ":" + getTokenName()
					+ " may not contain brackets: " + value);
		}
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
		PrimitiveChoiceSet<PCAlignment> pcs;
		if (Constants.LST_ALL.equals(activeValue))
		{
			pcs = new ReferenceChoiceSet<PCAlignment>(Collections
					.singletonList(context.ref
							.getCDOMAllReference(PCALIGNMENT_CLASS)));
		}
		else
		{
			if (hasIllegalSeparator(',', activeValue)
					|| hasIllegalSeparator('|', activeValue))
			{
				return ParseResult.INTERNAL_ERROR;
			}
			StringTokenizer st = new StringTokenizer(activeValue, ",|");
			Set<PCAlignment> set = new HashSet<PCAlignment>();
			while (st.hasMoreTokens())
			{
				String abb = st.nextToken();
				PCAlignment stat = context.ref.getAbbreviatedObject(
						PCALIGNMENT_CLASS, abb);
				if (stat == null)
				{
					ComplexParseResult cpr = new ComplexParseResult();
					cpr.addErrorMessage("Invalid object was used in: "
							+ activeValue);
					cpr.addErrorMessage("  " + abb + " is not a stat");
					return cpr;
				}
				if (!set.add(stat))
				{
					ComplexParseResult cpr = new ComplexParseResult();
					cpr
							.addErrorMessage("Invalid combination of objects was used in: "
									+ activeValue);
					cpr.addErrorMessage("  " + stat.getAbb()
							+ " was used twice");
					return cpr;
				}
			}
			if (set.isEmpty())
			{
				return ParseResult.INTERNAL_ERROR;
			}
			pcs = new SimpleChoiceSet<PCAlignment>(set, Constants.PIPE);
		}

		if (!pcs.getGroupingState().isValid())
		{
			ComplexParseResult cpr = new ComplexParseResult();
			cpr.addErrorMessage("Invalid combination of objects was used in: "
					+ activeValue);
			cpr.addErrorMessage("  Check that ALL is not combined");
			cpr.addErrorMessage("  Check that a key is not joined with AND (,)");
			return cpr;
		}
		ChooseInformation<PCAlignment> tc = new BasicChooseInformation<PCAlignment>(
				getTokenName(), pcs);
		tc.setTitle(title);
		tc.setChoiceActor(this);
		context.obj.put(obj, ObjectKey.CHOOSE_INFO, tc);
		return ParseResult.SUCCESS;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		ChooseInformation<?> tc = context.getObjectContext()
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
		StringBuilder sb = new StringBuilder();
		sb.append(tc.getLSTformat());
		String title = tc.getTitle();
		if (!title.equals(getDefaultTitle()))
		{
			sb.append("|TITLE=");
			sb.append(title);
		}
		return new String[] { sb.toString() };
	}

	public void applyChoice(CDOMObject owner, PCAlignment st, PlayerCharacter pc)
	{
		restoreChoice(pc, owner, st);
		List<ChooseSelectionActor<?>> actors = owner
				.getListFor(ListKey.NEW_CHOOSE_ACTOR);
		if (actors != null)
		{
			for (ChooseSelectionActor ca : actors)
			{
				ca.applyChoice(owner, st, pc);
			}
		}
	}

	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
			PCAlignment choice)
	{
		pc.removeAssoc(owner, getListKey(), choice);
		List<ChooseSelectionActor<?>> actors = owner
				.getListFor(ListKey.NEW_CHOOSE_ACTOR);
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
			PCAlignment choice)
	{
		pc.addAssoc(owner, getListKey(), choice);
		pc.addAssociation(owner, encodeChoice(choice));
	}

	public List<PCAlignment> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return pc.getAssocList(owner, getListKey());
	}

	public boolean allow(PCAlignment choice, PlayerCharacter pc,
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
		return "ALIGNMENT";
	}

	protected Class<PCAlignment> getChooseClass()
	{
		return PCALIGNMENT_CLASS;
	}

	protected String getDefaultTitle()
	{
		return "Alignment choice";
	}

	public PCAlignment decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				PCALIGNMENT_CLASS, s);
	}

	public String encodeChoice(PCAlignment choice)
	{
		return choice.getKeyName();
	}

	protected AssociationListKey<PCAlignment> getListKey()
	{
		return AssociationListKey.CHOOSE_PCALIGNMENT;
	}
}
