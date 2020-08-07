/*
 * Copyright 2014-20 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.inst;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pcgen.base.formula.base.ImplementedScope;

/**
 * A SimpleImplementedScope is a simple implementation of ImplementedScope.
 */
public class SimpleImplementedScope implements ImplementedScope
{
	/**
	 * Indicates if this ImplementedScope is a global scope.
	 */
	private final boolean isGlobalScope;

	/**
	 * The List of ImplementedScope objects that items in this ImplementedScope can draw
	 * from...
	 */
	private final List<ImplementedScope> supplyingScopes = new LinkedList<>();

	/**
	 * The name of this ImplementedScope.
	 */
	private final String name;

	/**
	 * Constructs a new SimpleImplementedScope with the given name.
	 * 
	 * @param name
	 *            The name of this ImplementedScope
	 * @param isGlobal
	 *            true if this ImplementedScope is a global scope
	 */
	public SimpleImplementedScope(String name, boolean isGlobal)
	{
		this.name = Objects.requireNonNull(name);
		this.isGlobalScope = isGlobal;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * Adds a new ImplementedScope that this ImplementedScope can draw from.
	 * 
	 * @param implScope
	 *            The ImplementedScope that this ImplementedScope can draw from
	 */
	public void drawsFrom(ImplementedScope implScope)
	{
		supplyingScopes.add(implScope);
	}

	@Override
	public List<ImplementedScope> drawsFrom()
	{
		return Collections.unmodifiableList(supplyingScopes);
	}

	@Override
	public boolean isGlobal()
	{
		return isGlobalScope;
	}
}
