/*
 * Formatcat.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 19/08/2006
 *
 * $Id:  $
 *
 */
package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * Deals with FORMATCAT token, which indicates where the name of the 
 * equipment modifier should be added in the name of any equipment item
 * the eqmod is added to.   
 */
public class FormatcatToken implements EquipmentModifierLstToken
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 * @Override
	 */
	public String getTokenName()
	{
		return "FORMATCAT";
	}

	/**
	 * @see pcgen.persistence.lst.EquipmentModifierLstToken#parse(pcgen.core.EquipmentModifier, java.lang.String)
	 * @Override
	 */
	public boolean parse(EquipmentModifier mod, String value)
	{
		if ("FRONT".equalsIgnoreCase(value))
		{
			mod.setFormatCat(EquipmentModifier.FORMATCAT_FRONT);
		}
		else if ("MIDDLE".equalsIgnoreCase(value))
		{
			mod.setFormatCat(EquipmentModifier.FORMATCAT_MIDDLE);
		}
		else if ("PARENS".equalsIgnoreCase(value))
		{
			mod.setFormatCat(EquipmentModifier.FORMATCAT_PARENS);
		}
		else
		{
			Logging.errorPrint("Ignoring unrecognized format category " + value
				+ " for EqMod " + mod.getKeyName());
			return false;
		}
		return true;
	}
}
