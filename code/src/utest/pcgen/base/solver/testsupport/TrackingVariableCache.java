/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.solver.testsupport;

import java.util.HashSet;
import java.util.Set;

import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.inst.SimpleVariableStore;

public final class TrackingVariableCache extends SimpleVariableStore
{
	private final Set<Object> set = new HashSet<>();

	@Override
	public <T> T put(VariableID<T> id, T process)
	{
		set.add(id);
		return super.put(id, process);
	}

	public void reset()
	{
		set.clear();
	}

	public boolean contains(Object o)
	{
		return set.contains(o);
	}

	public int size()
	{
		return set.size();
	}

}
