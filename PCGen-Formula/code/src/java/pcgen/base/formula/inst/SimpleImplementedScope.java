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
import java.util.Optional;

import pcgen.base.formula.base.ImplementedScope;

/**
 * A SimpleImplementedScope is a simple implementation of ImplementedScope.
 */
public class SimpleImplementedScope implements ImplementedScope
{

	/**
	 * The ImplementedScope that is the parent of this ImplementedScope.
	 */
	private final Optional<ImplementedScope> parent;

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
	 */
	public SimpleImplementedScope(String name)
	{
		this(Optional.empty(), name);
	}

	/**
	 * Constructs a new ImplementedScope with the given parent ImplementedScope and
	 * name.
	 * 
	 * @param parentScope
	 *            The ImplementedScope that is the parent of this ImplementedScope.
	 * @param name
	 *            The name of this ImplementedScope
	 */
	public SimpleImplementedScope(ImplementedScope parentScope, String name)
	{
		this(Optional.of(parentScope), name);
	}

	/**
	 * Constructs a new SimpleImplementedScope with the optional parent ImplementedScope
	 * and for the given name.
	 * 
	 * @param parentScope
	 *            The ImplementedScope that is the parent of this ImplementedScope, if
	 *            present
	 * @param name
	 *            The name of this ImplementedScope
	 */
	public SimpleImplementedScope(Optional<ImplementedScope> parentScope,
		String name)
	{
		this.parent = Objects.requireNonNull(parentScope);
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public Optional<ImplementedScope> getParentScope()
	{
		return parent;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return parent.isPresent() ? parent.get().toString() + "." + getName()
			: getName();
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
}
