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
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMCompoundOrReference;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Campaign;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * @author djones4
 * 
 */
public class TemplateLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	private static final Class<PCTemplate> PCTEMPLATE_CLASS = PCTemplate.class;

	@Override
	public String getTokenName()
	{
		return "TEMPLATE";
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
	{
		if (cdo instanceof Campaign)
		{
			return false;
		}

		ListKey<CDOMReference<PCTemplate>> lk;
		String remaining;
		boolean consolidate = false;
		boolean removeLegal = false;
		if (value.startsWith(Constants.LST_CHOOSE))
		{
			lk = ListKey.TEMPLATE_CHOOSE;
			remaining = value.substring(Constants.LST_CHOOSE.length());
			consolidate = true;
		}
		else if (value.startsWith(Constants.LST_ADDCHOICE))
		{
			lk = ListKey.TEMPLATE_ADDCHOICE;
			remaining = value.substring(Constants.LST_ADDCHOICE.length());
		}
		else
		{
			lk = ListKey.TEMPLATE;
			remaining = value;
			removeLegal = true;
		}
		if (isEmpty(remaining) || hasIllegalSeparator('|', remaining))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(remaining, Constants.PIPE);

		List<CDOMReference<PCTemplate>> list = new ArrayList<CDOMReference<PCTemplate>>();
		List<CDOMReference<PCTemplate>> removelist = new ArrayList<CDOMReference<PCTemplate>>();
		while (tok.hasMoreTokens())
		{
			String templKey = tok.nextToken();
			if (removeLegal && templKey.endsWith(".REMOVE"))
			{
				removelist.add(context.ref.getCDOMReference(PCTEMPLATE_CLASS,
						templKey.substring(0, templKey.length() - 7)));
			}
			else
			{
				list.add(context.ref.getCDOMReference(PCTEMPLATE_CLASS,
						templKey));
			}
		}

		if (consolidate)
		{
			CDOMCompoundOrReference<PCTemplate> ref = new CDOMCompoundOrReference<PCTemplate>(
					PCTEMPLATE_CLASS, Constants.LST_CHOOSE);
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
				context.getObjectContext().addToList(cdo,
						ListKey.REMOVE_TEMPLATES, ref);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		Changes<CDOMReference<PCTemplate>> changes = context.getObjectContext()
				.getListChanges(cdo, ListKey.TEMPLATE);

		List<String> list = new ArrayList<String>();

		Collection<CDOMReference<PCTemplate>> removed = changes.getRemoved();
		StringBuilder sb = new StringBuilder();
		if (changes.includesGlobalClear())
		{
			sb.append(Constants.LST_DOT_CLEAR);
		}
		if (removed != null && !removed.isEmpty())
		{
			boolean first = sb.length() == 0;
			for (CDOMReference<PCTemplate> ref : removed)
			{
				if (!first)
				{
					sb.append(Constants.PIPE);
				}
				list.add(Constants.LST_DOT_CLEAR_DOT + ref.getLSTformat());
			}
		}
		Collection<CDOMReference<PCTemplate>> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			if (sb.length() != 0)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(ReferenceUtilities.joinLstFormat(added, Constants.PIPE));
		}
		if (sb.length() != 0)
		{
			list.add(sb.toString());
		}

		Changes<CDOMReference<PCTemplate>> choosechanges = context
				.getObjectContext()
				.getListChanges(cdo, ListKey.TEMPLATE_CHOOSE);
		Collection<CDOMReference<PCTemplate>> chadded = choosechanges
				.getAdded();
		if (chadded != null && !chadded.isEmpty())
		{
			for (CDOMReference<PCTemplate> ref : chadded)
			{
				list.add(Constants.LST_CHOOSE
						+ ref.getLSTformat().replaceAll(",", "\\|"));
			}
		}

		Changes<CDOMReference<PCTemplate>> addchanges = context
				.getObjectContext().getListChanges(cdo,
						ListKey.TEMPLATE_ADDCHOICE);
		Collection<CDOMReference<PCTemplate>> addedItems = addchanges
				.getAdded();
		if (addedItems != null && !addedItems.isEmpty())
		{
			list.add(Constants.LST_ADDCHOICE
					+ ReferenceUtilities.joinLstFormat(addedItems,
							Constants.PIPE));
		}
		if (list.isEmpty())
		{
			// Possible if none triggered
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

}
