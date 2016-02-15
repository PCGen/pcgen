/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.HashMap;
import java.util.Map;

/**
 * A DependencyManager is a class to capture Formula dependencies.
 * 
 * In order to capture specific dependencies, a specific dependency should be
 * loaded into this DependencyManager.
 */
public class DependencyManager
{

	/**
	 * The map containing the managers of specific types of dependencies (where
	 * the types are identified by the DependencyKey).
	 */
	private Map<DependencyKey<?>, Object> map = new HashMap<>();

	/**
	 * Adds a new dependency to this DependencyManager, represented by the given
	 * DependencyKey and managed by the given object.
	 * 
	 * @param <T>
	 *            The Class of Object identified by the given DependencyKey
	 * @param key
	 *            The DependencyKey used to identify the manager of the
	 *            dependency
	 * @param manager
	 *            The class that manages the dependency represented by the given
	 *            DependencyKey
	 * @return The previous manager of the dependency represented by the given
	 *         DependencyKey
	 */
	public <T> T addDependency(DependencyKey<T> key, T manager)
	{
		return key.cast(map.put(key, manager));
	}

	/**
	 * Returns the object managing the dependency represented by the given
	 * DependencyKey.
	 * 
	 * @param <T>
	 *            The Class of Object identified by the given DependencyKey
	 * @param key
	 *            The DependencyKey used to identify the manager of the
	 *            dependency
	 * @return The object managing the dependency represented by the given
	 *         DependencyKey
	 */
	public <T> T getDependency(DependencyKey<T> key)
	{
		return key.cast(map.get(key));
	}

}
