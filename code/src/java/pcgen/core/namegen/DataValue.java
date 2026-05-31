/*
 * Copyright 2003 (C) Devon Jones
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.core.namegen;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataValue
{
	private final String value;
	private Map<String, String> subValues;

	public DataValue(String value)
	{
		this.value = value;
	}

	public String getSubValue(String key)
	{
		return subValues == null ? null : subValues.get(key);
	}

	public String getValue()
	{
		return value;
	}

	public void addSubValue(String key, String subValue)
	{
		if (subValues == null)
		{
			subValues = new LinkedHashMap<>(2);
		}
		subValues.putIfAbsent(key, subValue);
	}
}
