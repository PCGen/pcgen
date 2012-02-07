/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.StringKey;

public class FactFacet extends AbstractStorageFacet
{

	private final Class<?> thisClass = getClass();

	private Map<StringKey, String> getConstructingInfo(CharID id)
	{
		Map<StringKey, String> rci = getInfo(id);
		if (rci == null)
		{
			rci = new HashMap<StringKey, String>();
			setCache(id, thisClass, rci);
		}
		return rci;
	}

	private Map<StringKey, String> getInfo(CharID id)
	{
		return (Map<StringKey, String>) getCache(id, thisClass);
	}

	public void set(CharID id, StringKey key, String s)
	{
		getConstructingInfo(id).put(key, s);
	}

	public String get(CharID id, StringKey key)
	{
		Map<StringKey, String> rci = getInfo(id);
		if (rci != null)
		{
			return rci.get(key);
		}
		return null;
	}

	@Override
	public void copyContents(CharID source, CharID destination)
	{
		Map<StringKey, String> sourceRCI = getInfo(source);
		if (sourceRCI != null)
		{
			getConstructingInfo(destination).putAll(sourceRCI);
		}
	}

}
