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

import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.base.DefinedScope;
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
	 * The name of this ImplementedScope.
	 */
	private final DefinedScope scope;

	/**
	 * Constructs a new SimpleImplementedScope with the given DefinedScope.
	 * 
	 * @param scope
	 *            The DefinedScope of this SimpleImplementedScope
	 */
	public SimpleImplementedScope(DefinedScope scope)
	{
		this(Optional.empty(), scope);
	}

	/**
	 * Constructs a new ImplementedScope with the given parent ImplementedScope and
	 * DefinedScope.
	 * 
	 * @param parentScope
	 *            The ImplementedScope that is the parent of this ImplementedScope.
	 * @param scope
	 *            The DefinedScope of this SimpleImplementedScope
	 */
	public SimpleImplementedScope(ImplementedScope parentScope,
		DefinedScope scope)
	{
		this(Optional.of(parentScope), scope);
	}

	public SimpleImplementedScope(Optional<ImplementedScope> parentScope,
		DefinedScope scope)
	{
		this.parent = Objects.requireNonNull(parentScope);
		this.scope = Objects.requireNonNull(scope);
	}

	@Override
	public Optional<ImplementedScope> getParentScope()
	{
		return parent;
	}

	@Override
	public String getName()
	{
		return scope.getName();
	}

	@Override
	public String toString()
	{
		return parent.isPresent() ? parent.get().toString() + "." + getName()
			: getName();
	}

	@Override
	public DefinedScope getDefinedScope()
	{
		return scope;
	}
}
