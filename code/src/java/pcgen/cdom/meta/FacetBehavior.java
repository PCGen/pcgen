/*
 * Copyright (c) Thomas Parker, 2013.
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
import java.util.Objects;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;

public final class FacetBehavior
{

	public static final FacetBehavior MODEL = new FacetBehavior("Model");
	public static final FacetBehavior INPUT = new FacetBehavior("Input");
	public static final FacetBehavior CONDITIONAL = new FacetBehavior("Conditional");
	public static final FacetBehavior CONDITIONAL_GRANTED = new FacetBehavior("Conditional-Granted");
	//	public static final CorePerspective SELECTION = new CorePerspective("Selection");
	//	public static final CorePerspective CONDITIONAL_SELECTION = new CorePerspective("Conditional Selection");

	private static CaseInsensitiveMap<FacetBehavior> map = null;

	private String type;

	private FacetBehavior(String type)
	{
		Objects.requireNonNull(type, "Type cannot be null");
		this.type = type;
	}

	public static FacetBehavior getKeyFor(String type)
	{
		if (map == null)
		{
			buildMap();
		}
        return map.computeIfAbsent(type, k -> new FacetBehavior(type));
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<>();
		Field[] fields = FacetBehavior.class.getDeclaredFields();
		for (Field field : fields)
		{
			int mod = field.getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod) && Modifier.isPublic(mod))
			{
				try
				{
					Object obj = field.get(null);
					if (obj instanceof FacetBehavior)
					{
						map.put(field.getName(), (FacetBehavior) obj);
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					throw new UnreachableError(e);
				}
			}
		}
	}

	@Override
	public String toString()
	{
		return type;
	}

	public static Collection<FacetBehavior> getAllConstants()
	{
		if (map == null)
		{
			buildMap();
		}
		return new HashSet<>(map.values());
	}

}
