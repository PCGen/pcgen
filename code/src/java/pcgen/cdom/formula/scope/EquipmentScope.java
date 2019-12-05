/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.formula.scope;

import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.rules.context.LoadContext;

/**
 * The EquipmentScope is for variables that are local to a piece of Equipment.
 * Note that certain items may be more appropriate on a specific part of the
 * Equipment (see EquipmentPartScope).
 */
public class EquipmentScope implements PCGenScope
{
    /**
     * The parent of this scope (once loaded)
     */
    private Optional<PCGenScope> parent;

    @Override
    public String getName()
    {
        return "EQUIPMENT";
    }

    @Override
    public Optional<PCGenScope> getParentScope()
    {
        return parent;
    }

    /**
     * Sets the parent PCGenScope for this EquipmentScope.
     *
     * @param parent The parent PCGenScope for this EquipmentScope
     */
    public void setParent(PCGenScope parent)
    {
        this.parent = Optional.of(parent);
    }

    @Override
    public Optional<FormatManager<?>> getFormatManager(LoadContext context)
    {
        return Optional.empty();
    }
}
