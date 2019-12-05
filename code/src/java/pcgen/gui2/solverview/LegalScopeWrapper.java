/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package pcgen.gui2.solverview;

import pcgen.base.formula.base.LegalScope;

/**
 * An LegalScopeWrapper wraps a LegalScope in order to display an informative
 * String from the toString() method. This allows the LegalScopeWrapper to be
 * directly used in the UI without worrying about the actual toString() behavior
 * of any given LegalScope.
 */
class LegalScopeWrapper
{
    /**
     * The underlying LegalScope.
     */
    private final LegalScope legalScope;

    /**
     * Constructs a new LegalScopeWrapper with the given LegalScope
     *
     * @param legalScope The LegalScope that this LegalScopeWrapper will represent
     */
    LegalScopeWrapper(LegalScope legalScope)
    {
        this.legalScope = legalScope;
    }

    /**
     * Returns the LegalScope underlying this LegalScopeWrapper.
     *
     * @return the LegalScope underlying this LegalScopeWrapper
     */
    public LegalScope getLegalScope()
    {
        return legalScope;
    }

    /**
     * Returns an informative String identifying the LegalScope underlying this
     * LegalScopeWrapper
     */
    @Override
    public String toString()
    {
        return legalScope.getName();
    }

}
