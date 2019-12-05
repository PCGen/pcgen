/*
 * Copyright (c) Thomas Parker, 2013-14.
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
package pcgen.cdom.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;

public final class CorePerspective
{

    public static final CorePerspective LANGUAGE = new CorePerspective("Granted Languages");
    public static final CorePerspective DOMAIN = new CorePerspective("Granted Domains");
    public static final CorePerspective ARMORPROF = new CorePerspective("Armor Proficiencies");
    public static final CorePerspective SHIELDPROF = new CorePerspective("Shield Proficiencies");

    private static CaseInsensitiveMap<CorePerspective> map = null;

    private String name;

    private CorePerspective(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    static
    {
        buildMap();
    }

    private static void buildMap()
    {
        map = new CaseInsensitiveMap<>();
        Field[] fields = CorePerspective.class.getDeclaredFields();
        for (Field field : fields)
        {
            int mod = field.getModifiers();

            if (Modifier.isStatic(mod) && Modifier.isFinal(mod) && Modifier.isPublic(mod))
            {
                try
                {
                    Object obj = field.get(null);
                    if (obj instanceof CorePerspective)
                    {
                        map.put(field.getName(), (CorePerspective) obj);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e)
                {
                    throw new UnreachableError(e);
                }
            }
        }
    }

    public static Collection<CorePerspective> getAllConstants()
    {
        return new HashSet<>(map.values());
    }
}
