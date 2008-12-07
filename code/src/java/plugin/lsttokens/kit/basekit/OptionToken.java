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

import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
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

	public boolean parse(BaseKit baseKit, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, "|");
		while (tok.hasMoreTokens())
		{
			String val = tok.nextToken();
			int ind = -1;
			String lowVal;
			String highVal;
			if ((ind = val.indexOf(",")) != -1)
			{
				lowVal = val.substring(0, ind);
				highVal = val.substring(ind + 1);
			}
			else
			{
				lowVal = highVal = val;
			}
			Formula min = FormulaFactory.getFormulaFor(lowVal);
			Formula max = FormulaFactory.getFormulaFor(highVal);
			baseKit.setOptionBounds(min, max);
		}
		return true;
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
		int commaLoc = value.indexOf(',');
		String minString;
		String maxString;
		if (commaLoc == -1)
		{
			minString = value;
			maxString = value;
		}
		else if (commaLoc != value.lastIndexOf(','))
		{
			return false;
		}
		else
		{
			minString = value.substring(0, commaLoc);
			maxString = value.substring(commaLoc + 1);
		}
		Formula min = FormulaFactory.getFormulaFor(minString);
		Formula max = FormulaFactory.getFormulaFor(maxString);
		kit.setOptionBounds(min, max);
		return true;
	}

	public String[] unparse(LoadContext context, BaseKit kit)
	{
		Formula min = kit.getOptionMin();
		Formula max = kit.getOptionMax();
		if (min == null && max == null)
		{
			return null;
		}
		// TODO Error if only one is null
		StringBuilder sb = new StringBuilder();
		sb.append(min);
		if (!min.equals(max))
		{
			sb.append(',').append(max);
		}
		return new String[]{sb.toString()};
	}
}
