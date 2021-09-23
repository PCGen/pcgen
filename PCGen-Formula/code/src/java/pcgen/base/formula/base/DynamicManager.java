/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * DynamicManager is a manager used to contain the DynamicDependency objects within a
 * DependencyManager.
 */
public class DynamicManager
{

	/**
	 * The List of DynamicDependency objects for the formula managed by this
	 * DynamicManager.
	 */
	private final List<DynamicDependency> dependencies = new ArrayList<>();

	/**
	 * Adds a new DynamicDependency to the List of dynamic dependencies in this
	 * DynamicManager.
	 * 
	 * @param dep
	 *            The DynamicDependency to be added to this DynamicManager.
	 */
	public void addDependency(DynamicDependency dep)
	{
		dependencies.add(dep);
	}

	/**
	 * Returns a Collection of DynamicDependency objects contained by this DynamicManager.
	 * 
	 * @return a Collection of DynamicDependency objects contained by this DynamicManager
	 */
	public Collection<DynamicDependency> getDependencies()
	{
		return Collections.unmodifiableCollection(dependencies);
	}

}
