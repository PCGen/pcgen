/*
 * AbilityToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 * Created on March 3, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.basekit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.helper.OptionBound;
import pcgen.core.kit.BaseKit;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

public class OptionToken extends AbstractToken implements
		CDOMSecondaryToken<BaseKit>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "OPTION";
	}

	public Class<BaseKit> getTokenClass()
	{
		return BaseKit.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, BaseKit kit, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, "|");
		while (tok.hasMoreTokens())
		{
			String subTok = tok.nextToken();
			if (hasIllegalSeparator(',', subTok))
			{
				return false;
			}
			int commaLoc = subTok.indexOf(',');
			String minString;
			String maxString;
			if (commaLoc == -1)
			{
				minString = subTok;
				maxString = subTok;
			}
			else if (commaLoc != subTok.lastIndexOf(','))
			{
				return false;
			}
			else
			{
				minString = subTok.substring(0, commaLoc);
				maxString = subTok.substring(commaLoc + 1);
			}
			Formula min = FormulaFactory.getFormulaFor(minString);
			Formula max = FormulaFactory.getFormulaFor(maxString);
			kit.setOptionBounds(min, max);
		}
		return true;
	}

	public String[] unparse(LoadContext context, BaseKit kit)
	{
		Collection<OptionBound> bounds = kit.getBounds();
		if (bounds == null)
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (OptionBound bound : bounds)
		{
			Formula min = bound.getOptionMin();
			Formula max = bound.getOptionMax();
			if (min == null || max == null)
			{
				// Error if only one is null
				return null;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(min);
			if (!min.equals(max))
			{
				sb.append(',').append(max);
			}
			list.add(sb.toString());
		}
		return new String[] { StringUtil.join(list, Constants.PIPE) };
	}
}
