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
package pcgen.cdom.facet.analysis;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;

/**
 * InitiativeFacet is a Facet that calculates the Initiative value for a Player
 * Character.
 */
public class InitiativeFacet
{
    private static Formula initcomp = FormulaFactory.getFormulaFor("INITCOMP");

    private FormulaResolvingFacet formulaResolvingFacet;
    private BonusCheckingFacet bonusCheckingFacet;

    /**
     * Returns the Initiative value for the Player Character represented by the
     * given CharID.
     *
     * @param id The CharID representing the Player Character for which the
     *           Initiative value should be returned
     * @return The Initiative value for the Player Character represented by the
     * given CharID
     */
    public int getInitiative(CharID id)
    {
        return (int) bonusCheckingFacet.getBonus(id, "COMBAT", "Initiative")
                + formulaResolvingFacet.resolve(id, initcomp, "").intValue();
    }

    public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
    {
        this.formulaResolvingFacet = formulaResolvingFacet;
    }

    public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
    {
        this.bonusCheckingFacet = bonusCheckingFacet;
    }

}
