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
package plugin.lsttokens.ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Ability;
import pcgen.core.Description;
import pcgen.io.EntityEncoder;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * This class deals with the BENEFIT Token
 */
public class BenefitToken extends AbstractToken implements
		CDOMPrimaryToken<Ability>
{

	@Override
	public String getTokenName()
	{
		return "BENEFIT";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.getObjectContext().removeList(ability, ListKey.BENEFIT);
			return true;
		}
		if (value.startsWith(Constants.LST_DOT_CLEAR_DOT))
		{
			context.getObjectContext().removePatternFromList(ability,
					ListKey.BENEFIT, value.substring(7));
			return true;
		}

		Description ben = parseBenefit(value);
		if (ben == null)
		{
			return false;
		}
		context.getObjectContext().addToList(ability, ListKey.BENEFIT, ben);
		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		Changes<Description> changes = context.getObjectContext()
				.getListChanges(ability, ListKey.BENEFIT);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		Collection<Description> removedItems = changes.getRemoved();
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			for (Description d : removedItems)
			{
				list.add(Constants.LST_DOT_CLEAR_DOT + d);
			}
		}
		/*
		 * TODO .CLEAR. is not properly round-robin capable
		 */
		Collection<Description> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			for (Description d : added)
			{
				list.add(d.getPCCText());
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Parses the BENEFIT tag into a Description object.
	 * 
	 * @param aDesc
	 *            The LST tag
	 * @return A <tt>Description</tt> object
	 */
	public Description parseBenefit(final String aDesc)
	{
		if (isEmpty(aDesc) || hasIllegalSeparator('|', aDesc))
		{
			return null;
		}
		final StringTokenizer tok = new StringTokenizer(aDesc, Constants.PIPE);

		String firstToken = tok.nextToken();
		if (PreParserFactory.isPreReqString(firstToken))
		{
			Logging.errorPrint("Invalid " + getTokenName() + ": " + aDesc);
			Logging.errorPrint("  PRExxx can not be only value");
			return null;
		}
		final Description desc = new Description(EntityEncoder
				.decode(firstToken));

		boolean isPre = false;
		while (tok.hasMoreTokens())
		{
			final String token = tok.nextToken();
			if (PreParserFactory.isPreReqString(token))
			{
				desc.addPrerequisite(getPrerequisite(token));
				isPre = true;
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": "
							+ aDesc);
					Logging
							.errorPrint("  PRExxx must be at the END of the Token");
					return null;
				}
				desc.addVariable(token);
			}
		}

		return desc;
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

}
