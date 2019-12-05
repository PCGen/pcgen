/*
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 */
package pcgen.core;

import java.util.List;
import java.util.Optional;

import pcgen.base.formula.base.VarScoped;
import pcgen.core.analysis.BonusActivation;
import pcgen.core.bonus.BonusObj;

/**
 * {@code SizeAdjustment}.
 */
public final class SizeAdjustment extends PObject implements VarScoped
{
    /**
     * Activates (checks PrereqToUse) and returns list of BonusObj's
     *
     * @param aPC
     * @return active bonuses
     **/
    @Override
    public List<BonusObj> getActiveBonuses(final PlayerCharacter aPC)
    {
        BonusActivation.activateBonuses(this, aPC);

        return super.getActiveBonuses(aPC);
    }

    @Override
    public Optional<String> getLocalScopeName()
    {
        return Optional.of("PC.SIZE");
    }
}
