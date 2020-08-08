/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.Objects;

import pcgen.base.util.Indirect;

/**
 * IndirectDependency holds Indirect objects, so formulas can have the appropriate
 * dependencies attached.
 */
public class IndirectDependency
{

	/**
	 * The underlying List that contains the Indirect objects.
	 */
	private List<Indirect<?>> indirectObjects;

	/**
	 * Adds a Indirect to this IndirectDependency.
	 * 
	 * @param indirect
	 *            The Indirect to be added to this IndirectDependency
	 */
	public void add(Indirect<?> indirect)
	{
		if (indirectObjects == null)
		{
			indirectObjects = new ArrayList<>();
		}
		indirectObjects.add(Objects.requireNonNull(indirect));
	}

	/**
	 * Adds all of the Indirect objects in the given (non-null) Collection to this
	 * IndirectDependency.
	 * 
	 * @param collection
	 *            The Collection for which all of the included Indirect objects should be
	 *            added to this IndirectDependency
	 */
	public void addAll(Collection<Indirect<?>> collection)
	{
		collection.stream().forEach(this::add);
	}

	/**
	 * Returns a non-null, unmodifiable Collection of Indirect objects contained by this
	 * IndirectDependency.
	 * 
	 * @return A non-null, unmodifiable Collection of Indirect objects contained by this
	 *         IndirectDependency
	 */
	public Collection<Indirect<?>> getIndirects()
	{
		return (indirectObjects == null) ? Collections.emptyList()
			: Collections.unmodifiableCollection(indirectObjects);
	}

}
