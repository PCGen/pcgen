/*
 * Copyright (c) Thomas Parker, 2016.
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
package pcgen.cdom.formula;

import pcgen.base.util.TypedKey;
import pcgen.cdom.helper.ReferenceDependency;
import pcgen.rules.context.LoadContext;

/**
 * ManagerKey is a class indicating PCGen-specific keys for storing objects in the
 * managers that are provided to visitors in the Formula system.
 */
public final class ManagerKey
{
    /**
     * Provides a key for storing a LoadContext in a Manager provided to the formula
     * system.
     */
    public static final TypedKey<LoadContext> CONTEXT = new TypedKey<>();

    /**
     * Provides a key for storing a ReferenceDependency in a Manager provided to the
     * formula system.
     */
    public static final TypedKey<ReferenceDependency> REFERENCES = new TypedKey<>();

    private ManagerKey()
    {
    }
}
