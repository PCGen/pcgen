/*
 * Copyright (c) 2016-18 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.inst;

/**
 * A DynamicCategory is a method for keeping different Formats of Dynamic objects
 * separated.
 * <p>
 * For example, when Movement and Vision are defined as DYNAMIC objects, we need a method
 * of keeping those separated when someone tries to look up a certain object (e.g. "Fly").
 * Since those objects share a class upon construction (Dynamic.class), it can't be done
 * like a Skill or Language, we need to have a more complicated method like what we use
 * with Abilities. We thus use a higher level of separation - which is a Category.
 * Therefore, all Dynamic objects are categorized, that category name is defined by the
 * DYNAMICSCOPE (which in effect triggers the construction of the DynamicCategory for a
 * certain type of Dynamic).
 * <p>
 * This is a simple, non-hierarchical Category.
 */
public final class DynamicCategory extends AbstractCategory<Dynamic>
{
    @Override
    public Class<Dynamic> getReferenceClass()
    {
        return Dynamic.class;
    }

    @Override
    public String getReferenceDescription()
    {
        return getKeyName() + " (Dynamic)";
    }

    @Override
    public Dynamic newInstance()
    {
        Dynamic instance = new Dynamic();
        instance.setCDOMCategory(this);
        return instance;
    }

    @Override
    public String getPersistentFormat()
    {
        return getKeyName().toUpperCase();
    }
}
