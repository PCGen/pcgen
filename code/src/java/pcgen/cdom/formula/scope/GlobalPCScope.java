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
 * This is the global variable scope
 */
public class GlobalPCScope implements PCGenScope
{
    /**
     * The name of the Global Scope for PCGen characters, publicly available for reuse...
     */
    public static final String GLOBAL_SCOPE_NAME = "PC";

    @Override
    public String getName()
    {
        return GLOBAL_SCOPE_NAME;
    }

    @Override
    public Optional<PCGenScope> getParentScope()
    {
        return Optional.empty();
    }

    @Override
    public Optional<FormatManager<?>> getFormatManager(LoadContext context)
    {
        return Optional.empty();
    }
}
