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

public final class VisionType extends AbstractConstant
{

	private VisionType()
	{
		//Private Constructor
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
		for (int i = 0; i < fields.length; i++)
		{
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod) && Modifier.isPublic(mod))
			{
				try
				{
					Object o = fields[i].get(null);
					if (o instanceof VisionType)
					{
						typeMap.put(new CaseInsensitiveString(fields[i].getName()), (VisionType) o);
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					//TODO Why throw an InternalError? Wouldn't an assert false be better? JK070115
					throw new InternalError();
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
		for (Map.Entry<CaseInsensitiveString, VisionType> me : typeMap.entrySet())
		{
			if (me.getValue().equals(this))
			{
				return me.getKey().toString();
			}
		}
		// Error
		return "";
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
