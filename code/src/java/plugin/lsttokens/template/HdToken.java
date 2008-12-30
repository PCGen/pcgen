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
package plugin.lsttokens.template;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HD Token
 */
public class HdToken extends AbstractToken implements CDOMPrimaryToken<PCTemplate>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "HD";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
			throws PersistenceLayerException
	{
		if (".CLEAR".equals(value))
		{
			context.getObjectContext().removeList(template,
					ListKey.HD_TEMPLATES);
			return true;
		}
		if (isEmpty(value) || hasIllegalSeparator(':', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.COLON);

		String hdString = tok.nextToken();
		int minhd;
		int maxhd;
		try
		{
			int minusLoc = hdString.indexOf('-');
			if (minusLoc == -1)
			{
				if (hdString.indexOf('+') == hdString.length() - 1)
				{
					minhd = Integer.parseInt(hdString.substring(0, hdString
							.length() - 1));
					maxhd = Integer.MAX_VALUE;
				}
				else
				{
					minhd = Integer.parseInt(hdString);
					maxhd = minhd;
				}
			}
			else
			{
				minhd = Integer.parseInt(hdString.substring(0, minusLoc));
				maxhd = Integer.parseInt(hdString.substring(minusLoc + 1));
			}
			if (maxhd < minhd)
			{
				Logging.errorPrint("Malformed " + getTokenName()
						+ " Token (Max < Min): " + hdString);
				Logging.errorPrint("  Line was: " + value);
				return false;
			}
		}
		catch (NumberFormatException ex)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (HD syntax invalid): " + hdString);
			return false;
		}

		if (!tok.hasMoreTokens())
		{
			Logging.errorPrint("Invalid " + getTokenName()
					+ ": requires 3 colon separated elements (has one): "
					+ value);
			return false;
		}
		String typeStr = tok.nextToken();
		if (!tok.hasMoreTokens())
		{
			Logging.errorPrint("Invalid " + getTokenName()
					+ ": requires 3 colon separated elements (has two): "
					+ value);
			return false;
		}
		String argument = tok.nextToken();
		PCTemplate derivative = new PCTemplate();
		derivative.put(IntegerKey.HD_MIN, minhd);
		derivative.put(IntegerKey.HD_MAX, maxhd);
		context.getObjectContext().addToList(template, ListKey.HD_TEMPLATES,
				derivative);
		if (context.processToken(derivative, typeStr, argument))
		{
			return true;
		}
		return false;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<PCTemplate> changes = context.getObjectContext()
				.getListChanges(pct, ListKey.HD_TEMPLATES);
		Collection<PCTemplate> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (PCTemplate pctChild : added)
		{
			StringBuilder sb = new StringBuilder();
			Integer min = pctChild.get(IntegerKey.HD_MIN);
			Integer max = pctChild.get(IntegerKey.HD_MAX);
			StringBuilder hd = new StringBuilder();
			hd.append(min);
			if (max == Integer.MAX_VALUE)
			{
				hd.append('+');
			}
			else if (!max.equals(min))
			{
				hd.append('-').append(max);
			}
			sb.append(hd.toString()).append(':');
			Collection<String> unparse = context.unparse(pctChild);
			if (unparse != null)
			{
				int masterLength = sb.length();
				for (String str : unparse)
				{
					sb.setLength(masterLength);
					set.add(sb.append(str).toString());
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}

}
