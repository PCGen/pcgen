/*
 * SpellPointCosts.java
 * Copyright 2006 (C) Joe Frazier
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
 * Current Ver: $Revision: $
 * Last Editor: $Author: $
 * Last Edited: $Date: $
 */
package plugin.bonustokens;

import java.util.StringTokenizer;

import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.util.SpellPointCostInfo;
import pcgen.core.bonus.util.SpellPointCostInfo.SpellPointFilterType;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Handles the BONUS:SPELLPOINTCOSTS token.
 */
public class SpellPointCosts extends BonusObj
{
	/**
	 * Parse the bonus token.
	 * @see pcgen.core.bonus.BonusObj#parseToken(LoadContext, java.lang.String)
	 * @return True if successfully parsed.
	 */
	@Override
	protected boolean parseToken(LoadContext context, final String token)
	{
		SpellPointCostInfo spi;
		SpellPointFilterType type;
		String typeValue;
		String part;

		if (token == null)
		{
			Logging.errorPrint("Malformed BONUS:SPELLPOINTCOST.");
			return false;
		}
		StringTokenizer aTok = new StringTokenizer(token, ";");
		String val = aTok.nextToken();
		StringTokenizer aTok2 = new StringTokenizer(val, ".");
		if(aTok2.countTokens() >=1 )
		{
			String theType = aTok2.nextToken();
			if(theType.equals("SCHOOL"))
			{
				type = SpellPointFilterType.SCHOOL;
			}
			else if(theType.equals("SUBSCHOOL"))
			{
				type = SpellPointFilterType.SUBSCHOOL;
			}
			else if(theType.equals("SPELL"))
			{
				type = SpellPointFilterType.SPELL;
			}
			else 
			{
				Logging.errorPrint("Malformed BONUS:SPELLPOINTCOST: " + token);
				return false;
			}
			typeValue = aTok2.nextToken();
		}
		else 
		{
			Logging.errorPrint("Malformed BONUS:SPELLPOINTCOST: " + token);
			return false;
		}
		
		if(aTok.hasMoreTokens())
		{
			part = aTok.nextToken();
		}
		else 
		{
			part = "TOTAL";
		}
			

		// Type cannot be null at this point
		if(typeValue == null || part == null)
		{
			Logging.errorPrint("Malformed BONUS:SPELLPOINTCOST: " + token);
			return false;
		}
		spi = new SpellPointCostInfo(type, typeValue, part, false);
		
		
		addBonusInfo(spi);

		return true;
	}

	/**
	 * Unparse the bonus token.
	 * @see pcgen.core.bonus.BonusObj#unparseToken(java.lang.Object)
	 * @param obj The object to unparse
	 * @return The unparsed string.
	 */
	@Override
	protected String unparseToken(Object obj)
	{
		SpellPointCostInfo spInfo = (SpellPointCostInfo) obj;
		StringBuilder sb = new StringBuilder();
		sb.append(spInfo.getSpellPointPartFilter().toString());
		sb.append(".");
		sb.append(spInfo.getSpellPointPartFilterValue());
		
		sb.append(";");
		sb.append(spInfo.getSpellPointPart());

		return sb.toString();
	}

	/**
	 * Return the bonus tag handled by this class.
	 * @return The bonus handled by this class.
	 */
	@Override
	public String getBonusHandled()
	{
		return "SPELLPOINTCOST";
	}
}
