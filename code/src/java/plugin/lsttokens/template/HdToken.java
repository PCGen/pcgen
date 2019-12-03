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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with HD Token
 */
public class HdToken extends AbstractTokenWithSeparator<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
{

	@Override
	public String getTokenName()
	{
		return "HD";
	}

	@Override
	public ParseResult parseToken(LoadContext context, PCTemplate template, String value)
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.getObjectContext().removeList(template, ListKey.HD_TEMPLATES);
			return ParseResult.SUCCESS;
		}
		return super.parseToken(context, template, value);
	}

	@Override
	protected char separator()
	{
		return ':';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, PCTemplate template, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.COLON);

		String hdString = tok.nextToken();
		int minhd;
		int maxhd;
		try
		{
			int minusLoc = hdString.indexOf('-');
			if (minusLoc == -1)
			{
				int plusLoc = hdString.indexOf('+');
				if (plusLoc == 0)
				{
					return new ParseResult.Fail("Malformed " + getTokenName() + " Cannot start with +: " + hdString);
				}
				else if (plusLoc == hdString.length() - 1)
				{
					minhd = Integer.parseInt(hdString.substring(0, hdString.length() - 1));
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
				return new ParseResult.Fail("Malformed " + getTokenName() + " Token (Max < Min): " + hdString);
			}
		}
		catch (NumberFormatException ex)
		{
			return new ParseResult.Fail("Malformed " + getTokenName() + " Token (HD syntax invalid): " + hdString);
		}

		if (!tok.hasMoreTokens())
		{
			return new ParseResult.Fail(
				"Invalid " + getTokenName() + ": requires 3 colon separated elements (has one): " + value);
		}
		String typeStr = tok.nextToken();
		if (!tok.hasMoreTokens())
		{
			return new ParseResult.Fail(
				"Invalid " + getTokenName() + ": requires 3 colon separated elements (has two): " + value);
		}
		String argument = tok.nextToken();
		PCTemplate derivative = new PCTemplate();
		derivative.put(ObjectKey.VISIBILITY, Visibility.HIDDEN);
		derivative.put(IntegerKey.HD_MIN, minhd);
		derivative.put(IntegerKey.HD_MAX, maxhd);
		context.getReferenceContext().getManufacturer(PCTemplate.class).addDerivativeObject(derivative);
		context.getObjectContext().addToList(template, ListKey.HD_TEMPLATES, derivative);
		if (context.processToken(derivative, typeStr, argument))
		{
			return ParseResult.SUCCESS;
		}
		return ParseResult.INTERNAL_ERROR;
	}

	@Override
	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<PCTemplate> changes = context.getObjectContext().getListChanges(pct, ListKey.HD_TEMPLATES);
		Collection<PCTemplate> added = changes.getAdded();
		List<String> ret = new ArrayList<>();
		boolean globalClear = changes.includesGlobalClear();
		if (globalClear)
		{
			ret.add(Constants.LST_DOT_CLEAR);
		}
		if (added != null)
		{
			Set<String> set = new TreeSet<>();
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
				else
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
			ret.addAll(set);
		}
		if (ret.isEmpty())
		{
			return null;
		}
		return ret.toArray(new String[0]);
	}

	@Override
	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}

}
