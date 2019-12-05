/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.formula.scope;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.cdom.helper.SpringHelper;
import pcgen.cdom.inst.Dynamic;
import pcgen.cdom.inst.DynamicCategory;
import pcgen.rules.context.LoadContext;

/**
 * A DynamicScope is a PCGenScope that covers a specific Category of Dynamic object.
 */
public class DynamicScope implements PCGenScope
{
    /**
     * The parent PCGenScope for all DynamicScope objects is the GlobalPCScope.
     */
    private static final Optional<PCGenScope> PARENT_SCOPE = Optional.of(SpringHelper.getBean(GlobalPCScope.class));

    /**
     * The DynamicCategory indicating the objects contained by this DynamicScope.
     */
    private final DynamicCategory category;

    /**
     * The FormatManager for the objects contained in this DynamicScope.
     */
    private final FormatManager<Dynamic> formatManager;

    /**
     * Constructs a new DynamicScope for the given DynamicCategory and containing the
     * objects identified in the given FormatManager.
     *
     * @param category      The DynamicCategory indicating the objects contained by this
     *                      DynamicScope
     * @param formatManager The FormatManager for the objects contained in this DynamicScope
     */
    public DynamicScope(DynamicCategory category, FormatManager<Dynamic> formatManager)
    {
        this.category = Objects.requireNonNull(category);
        this.formatManager = Objects.requireNonNull(formatManager);
    }

    @Override
    public String getName()
    {
        return category.getName();
    }

    @Override
    public Optional<PCGenScope> getParentScope()
    {
        return PARENT_SCOPE;
    }

    @Override
    public Optional<FormatManager<?>> getFormatManager(LoadContext context)
    {
        return Optional.of(formatManager);
    }
}
