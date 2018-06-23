/*
 * Copyright 2003 (C) Devon Jones
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
package plugin.overland.util;

import java.util.ArrayList;
import java.util.List;

/** Class that holds a set of travel methods and speeds
 */
public class PairList<T extends Pair<?, ?>>
{
	private List<T> vPairs;

	public PairList()
	{
		vPairs = new ArrayList<>();
	}

	public int getCount()
	{
		return vPairs.size();
	}

	public T get(int i)
	{
		return vPairs.get(i);
	}

	public void add(T p)
	{
		vPairs.add(p);
	}
}
