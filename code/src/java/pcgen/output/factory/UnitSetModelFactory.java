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
package pcgen.output.factory;

import pcgen.core.GameMode;
import pcgen.output.base.ModeModelFactory;
import pcgen.output.model.UnitSetModel;

/**
 * An UnitSetModelFactory is a ModeModelFactory that produces an UnitSetModel
 * for the current Game Mode
 */
public class UnitSetModelFactory implements ModeModelFactory
{

    /*
     * Note: It would be nice someday if this wasn't necessary as a separate
     * class. A more "CDOM-like" structure in the GameMode object would allow
     * the UnitSet to be grabbed "generically" as an ObjectKey, but that is just
     * not in place today for GameMode.
     */

    @Override
    public UnitSetModel generate(GameMode mode)
    {
        return new UnitSetModel(mode.getUnitSet());
    }
}
