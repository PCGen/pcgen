/*
 * QualifyFilter.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 9, 2002, 2:30 PM
 */
package pcgen.gui.filter;

import pcgen.core.Deity;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.system.LanguageBundle;

/**
 * <code>QualifyFilter</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
final class QualifyFilter extends AbstractPObjectFilter
{

	QualifyFilter()
	{
		super(LanguageBundle.getString("in_miscel"), "Qualify");
	}

	public String getDescription(PlayerCharacter aPC)
	{
		return LanguageBundle.getFormattedString("in_filterAccQual",aPC.getName());
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Deity)
		{
			return aPC.canSelectDeity((Deity) pObject);
		}
		else if (pObject instanceof Equipment)
		{
			final Equipment equip = (Equipment) pObject;
			final boolean accept = equip.qualifies(aPC, equip);

			if (accept && (equip.isShield() || equip.isWeapon() || equip.isArmor()))
			{
				return aPC.isProficientWith(equip);
			}

			return accept;
		}
		else
		{
			return pObject.qualifies(aPC, pObject);
		}
	}
}
