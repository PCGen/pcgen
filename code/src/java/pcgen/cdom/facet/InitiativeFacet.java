/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;

/**
 * InitiativeFacet is a Facet that calculates the Initiative value for a Player
 * Character
 */
public class InitiativeFacet
{
	private FormulaResolvingFacet resolveFacet = FacetLibrary
			.getFacet(FormulaResolvingFacet.class);
	private BonusCheckingFacet bonusFacet = FacetLibrary
			.getFacet(BonusCheckingFacet.class);
	private Formula initcomp = FormulaFactory.getFormulaFor("INITCOMP");

	/**
	 * Returns the Initiative value for the Player Character represented by the
	 * given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Initiative value should be returned
	 * @return The Initiative value for the Player Character represented by the
	 *         given CharID
	 */
	public int getInitiative(CharID id)
	{
		return (int) bonusFacet.getBonus(id, "COMBAT", "Initiative")
				+ resolveFacet.resolve(id, initcomp, "").intValue();
	}

}
