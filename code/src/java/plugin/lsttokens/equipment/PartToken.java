/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.equipment;

import pcgen.cdom.formula.scope.EquipmentPartScope;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with PART token
 */
public class PartToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "PART";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
	{
		int pipeLoc = value.indexOf('|');
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail(getTokenName() + " requires <integer>|<token>");
		}
		String partNumString = value.substring(0, pipeLoc);
		int partNumber;
		try
		{
			partNumber = Integer.parseInt(partNumString);
		}
		catch (NumberFormatException e)
		{
			return new ParseResult.Fail(
				getTokenName() + " requires <integer>|<token> ... " + partNumString + " was not an integer");
		}
		if (partNumber <= 0)
		{
			return new ParseResult.Fail(
				getTokenName() + " requires a positive integer. " + partNumber + " was not positive");
		}
		EquipmentHead part = eq.getEquipmentHead(partNumber);
		String partToken = value.substring(pipeLoc + 1);

		int colonLoc = partToken.indexOf(':');
		if (colonLoc == -1)
		{
			return new ParseResult.Fail(
				getTokenName() + " requires <integer>|<token>:<token content>, but no colon was found in: " + value);
		}
		String tokenName = partToken.substring(0, colonLoc);
		String tokenValue = partToken.substring(colonLoc + 1);

		LoadContext subContext = context.dropIntoContext(EquipmentPartScope.PC_EQUIPMENT_PART);

		boolean processToken;

		processToken = subContext.processToken(part, tokenName, tokenValue);
		if (processToken)
		{
			return ParseResult.SUCCESS;
		}
		return new ParseResult.Fail(getTokenName() + " encountered an error in the token content of: " + value);
	}

	@Override
	public String[] unparse(LoadContext context, Equipment eq)
	{
		//TODO Need to define this...
		return null;
	}

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}
