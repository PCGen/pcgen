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

import pcgen.core.*;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.PropertyFactory;

/**
 * <code>QualifyFilter</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
final class QualifyFilter extends AbstractPObjectFilter
{
	private static final String in_filterAccQual = PropertyFactory.getString("in_filterAccQual");
	private static final String in_filterAccQual2 = PropertyFactory.getString("in_filterAccQual2");

	QualifyFilter()
	{
		super(PropertyFactory.getString("in_miscel"), "Qualify");
	}

	public String getDescription(PlayerCharacter aPC)
	{
		return in_filterAccQual + ' ' + aPC.getName() + ' ' + in_filterAccQual2;
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
		else if (pObject instanceof Domain)
		{
			return ((Domain) pObject).qualifiesForDomain(aPC);
		}
		else if (pObject instanceof Equipment)
		{
			final Equipment equip = (Equipment) pObject;
			final boolean accept = PrereqHandler.passesAll(equip.getPreReqList(), aPC, equip);

			if (accept && (equip.isShield() || equip.isWeapon() || equip.isArmor()))
			{
				return aPC.isProficientWith(equip);
			}

			return accept;
		}
		else if (pObject instanceof PCClass)
		{
			return ((PCClass) pObject).isQualified(aPC);
		}
		else if (pObject instanceof PCTemplate)
		{
			return ((PCTemplate) pObject).isQualified(aPC);
		}
		else
		{
			//passesPrereqTests is global now, so this should be the right thing to do.
			return PrereqHandler.passesAll(pObject.getPreReqList(), aPC, pObject);
		}
	}
}
