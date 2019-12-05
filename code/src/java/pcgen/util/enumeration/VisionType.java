/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */

package pcgen.util.enumeration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.lang.UnreachableError;

public final class VisionType
{
    private VisionType()
    {
    }

    private static Map<CaseInsensitiveString, VisionType> typeMap;

    public static VisionType getVisionType(String s)
    {
        if (typeMap == null)
        {
            buildMap();
        }
        CaseInsensitiveString caseInsensitiveS = new CaseInsensitiveString(s);
        /*
         * CONSIDER Now this is CASE INSENSITIVE. Should this really be the
         * case? - thpr 10/28/06
         */
        VisionType o = typeMap.get(caseInsensitiveS);
        if (o == null)
        {
            o = new VisionType();
            typeMap.put(caseInsensitiveS, o);
        }
        return o;
    }

    private static void buildMap()
    {
        typeMap = new HashMap<>();
        Field[] fields = VisionType.class.getDeclaredFields();
        for (Field field : fields)
        {
            int mod = field.getModifiers();

            if (Modifier.isStatic(mod) && Modifier.isFinal(mod) && Modifier.isPublic(mod))
            {
                try
                {
                    Object o = field.get(null);
                    if (o instanceof VisionType)
                    {
                        typeMap.put(new CaseInsensitiveString(field.getName()), (VisionType) o);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e)
                {
                    throw new UnreachableError(e);
                }
            }
        }
    }

    @Override
    public String toString()
    {
        if (typeMap == null)
        {
            return "";
        }
        return typeMap.entrySet()
                .stream()
                .filter(me -> me.getValue().equals(this))
                .findFirst()
                .map(me -> me.getKey().toString())
                .orElse("");
    }

    public static void clearConstants()
    {
        buildMap();
    }

    public static Collection<VisionType> getAllVisionTypes()
    {
        if (typeMap == null)
        {
            buildMap();
        }
        return Collections.unmodifiableCollection(typeMap.values());
    }
}
