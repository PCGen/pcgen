/*
 * Copyright 2008,2014 (C) Thomas Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.BasicClassIdentity;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMCompoundOrReference;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class TemplateLst extends AbstractToken implements CDOMPrimaryToken<CDOMObject>, ChooseSelectionActor<PCTemplate>
{

	private static final String ADDCHOICE_COLON = "ADDCHOICE:";
	private static final Class<PCTemplate> PCTEMPLATE_CLASS = PCTemplate.class;
	private static final ClassIdentity<PCTemplate> PCTEMPLATE_IDENTITY =
			BasicClassIdentity.getIdentity(PCTEMPLATE_CLASS);

	@Override
	public String getTokenName()
	{
		return "TEMPLATE";
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject cdo, String value)
	{
		if (cdo instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + cdo.getClass().getSimpleName());
		}
		ListKey<CDOMReference<PCTemplate>> lk;
		String remaining;
		boolean consolidate = false;
		boolean specialLegal = false;
		if (value.startsWith(Constants.LST_CHOOSE_COLON))
		{
			lk = ListKey.TEMPLATE_CHOOSE;
			remaining = value.substring(Constants.LST_CHOOSE_COLON.length());
			consolidate = true;
		}
		else if (value.startsWith(ADDCHOICE_COLON))
		{
			lk = ListKey.TEMPLATE_ADDCHOICE;
			remaining = value.substring(ADDCHOICE_COLON.length());
		}
		else
		{
			lk = ListKey.TEMPLATE;
			remaining = value;
			specialLegal = true;
		}
		ParseResult pr = checkSeparatorsAndNonEmpty('|', remaining);
		if (!pr.passed())
		{
			return pr;
		}

		StringTokenizer tok = new StringTokenizer(remaining, Constants.PIPE);

		List<CDOMReference<PCTemplate>> list = new ArrayList<>();
		List<CDOMReference<PCTemplate>> removelist = new ArrayList<>();
		while (tok.hasMoreTokens())
		{
			String templKey = tok.nextToken();
			if (specialLegal && templKey.endsWith(".REMOVE"))
			{
				removelist.add(context.getReferenceContext().getCDOMReference(PCTEMPLATE_CLASS,
					templKey.substring(0, templKey.length() - 7)));
			}
			else if (specialLegal && templKey.equals(Constants.LST_PERCENT_LIST))
			{
				context.getObjectContext().addToList(cdo, ListKey.NEW_CHOOSE_ACTOR, this);
			}
			else
			{
				ReferenceManufacturer<PCTemplate> rm = context.getReferenceContext().getManufacturer(PCTEMPLATE_CLASS);
				CDOMReference<PCTemplate> ref = TokenUtilities.getTypeOrPrimitive(rm, templKey);
				if (ref == null)
				{
					// If we have an invalid template reference, regardless of type, log it
					Logging.log(Logging.WARNING, "Invalid template reference in TEMPLATE token in " +
							cdo.getDisplayName() + ": " + templKey);

					return ParseResult.INTERNAL_ERROR;
				}
				list.add(ref);
			}
		}

		if (consolidate)
		{
			CDOMCompoundOrReference<PCTemplate> ref =
					new CDOMCompoundOrReference<>(PCTEMPLATE_IDENTITY, Constants.LST_CHOOSE_COLON);
			for (CDOMReference<PCTemplate> r : list)
			{
				ref.addReference(r);
			}
			ref.trimToSize();
			list.clear();
			list.add(ref);
		}
		for (CDOMReference<PCTemplate> ref : list)
		{
			context.getObjectContext().addToList(cdo, lk, ref);
		}
		if (!removelist.isEmpty())
		{
			for (CDOMReference<PCTemplate> ref : removelist)
			{
				context.getObjectContext().addToList(cdo, ListKey.REMOVE_TEMPLATES, ref);
			}
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		Changes<CDOMReference<PCTemplate>> changes = context.getObjectContext().getListChanges(cdo, ListKey.TEMPLATE);
		Changes<CDOMReference<PCTemplate>> removechanges =
				context.getObjectContext().getListChanges(cdo, ListKey.REMOVE_TEMPLATES);
		Changes<ChooseSelectionActor<?>> listChanges =
				context.getObjectContext().getListChanges(cdo, ListKey.NEW_CHOOSE_ACTOR);

		List<String> list = new ArrayList<>();

		Collection<CDOMReference<PCTemplate>> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			list.add(ReferenceUtilities.joinLstFormat(added, Constants.PIPE));
		}

		Collection<ChooseSelectionActor<?>> listAdded = listChanges.getAdded();
		if (listAdded != null && !listAdded.isEmpty())
		{
			for (ChooseSelectionActor<?> csa : listAdded)
			{
				if (csa.equals(this))
				{
					list.add(Constants.LST_PERCENT_LIST);
				}
			}
		}

		Changes<CDOMReference<PCTemplate>> choosechanges =
				context.getObjectContext().getListChanges(cdo, ListKey.TEMPLATE_CHOOSE);
		Collection<CDOMReference<PCTemplate>> chadded = choosechanges.getAdded();
		if (chadded != null && !chadded.isEmpty())
		{
			for (CDOMReference<PCTemplate> ref : chadded)
			{
				list.add(Constants.LST_CHOOSE_COLON + ref.getLSTformat(false).replaceAll(",", "\\|"));
			}
		}

		Changes<CDOMReference<PCTemplate>> addchanges =
				context.getObjectContext().getListChanges(cdo, ListKey.TEMPLATE_ADDCHOICE);
		Collection<CDOMReference<PCTemplate>> addedItems = addchanges.getAdded();
		if (addedItems != null && !addedItems.isEmpty())
		{
			list.add(ADDCHOICE_COLON + ReferenceUtilities.joinLstFormat(addedItems, Constants.PIPE));
		}

		Collection<CDOMReference<PCTemplate>> radd = removechanges.getAdded();
		if (radd != null && !radd.isEmpty())
		{
			StringBuilder sb = new StringBuilder();
			boolean needPipe = false;
			for (CDOMReference<PCTemplate> ref : radd)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				needPipe = true;
				sb.append(ref.getLSTformat(false)).append(".REMOVE");
			}
			list.add(sb.toString());
		}

		if (list.isEmpty())
		{
			// Possible if none triggered
			return null;
		}
		return list.toArray(new String[0]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public void removeChoice(ChooseDriver owner, PCTemplate choice, PlayerCharacter pc)
	{
		pc.removeTemplate(choice);
	}

	@Override
	public void applyChoice(ChooseDriver owner, PCTemplate choice, PlayerCharacter pc)
	{
		pc.addTemplate(choice);
	}

	@Override
	public String getLstFormat()
	{
		return Constants.LST_PERCENT_LIST;
	}

	@Override
	public String getSource()
	{
		return getTokenName();
	}

	@Override
	public Class<PCTemplate> getChoiceClass()
	{
		return PCTEMPLATE_CLASS;
	}
}
