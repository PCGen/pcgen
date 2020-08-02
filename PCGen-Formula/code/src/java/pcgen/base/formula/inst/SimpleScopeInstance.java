/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;

/**
 * A SimpleScopeInstance is a minimal implementation of the ScopeInstance interface.
 */
public class SimpleScopeInstance implements ScopeInstance
{

	/**
	 * Contains the ScopeInstance that is the parent of this ScopeInstance.
	 */
	private final Optional<ScopeInstance> parent;

	/**
	 * Contains the ImplementedScope in which this ScopeInstance was instantiated.
	 */
	private final ImplementedScope scope;

	/**
	 * Contains the VarScoped that this ScopeInstance was instantiated to represent.
	 */
	private final VarScoped representing;

	/**
	 * Constructs a new SimpleScopeInstance with the given parent ScopeInstance and within
	 * the given ImplementedScope.
	 * 
	 * @param parent
	 *            the ScopeInstance that is the parent of this ScopeInstance
	 * @param scope
	 *            the ImplementedScope in which this ScopeInstance was instantiated
	 * @param representing
	 *            The VarScoped object that this ScopeInstance represents
	 */
	public SimpleScopeInstance(Optional<ScopeInstance> parent, ImplementedScope scope,
		VarScoped representing)
	{
		this.representing = Objects.requireNonNull(representing);
		Objects.requireNonNull(scope);
		if (parent.isEmpty())
		{
			if (scope.getParentScope().isPresent())
			{
				throw new IllegalArgumentException(
					"Incompatible ScopeInstance and ImplementedScope: "
						+ "Parent may only be null when ImplementedScope has no parent");
			}
		}
		else if (scope.getParentScope().isPresent())
		{
			ImplementedScope parentScope = scope.getParentScope().get();
			if (!parentScope.equals(parent.get().getImplementedScope()))
			{
				throw new IllegalArgumentException(
					"Incompatible ScopeInstance (" + parent.get().getImplementedScope().getName()
						+ ") and ImplementedScope parent (" + parentScope.getName() + ")");
			}
		}
		else
		{
			throw new IllegalArgumentException(
				"Incompatible ScopeInstance and ImplementedScope: "
					+ "ImplementedScope Parent may only be null "
					+ "when ScopeInstance is null");
		}
		this.parent = parent;
		this.scope = scope;
	}

	@Override
	public ImplementedScope getImplementedScope()
	{
		return scope;
	}

	@Override
	public Optional<ScopeInstance> getParentScope()
	{
		return parent;
	}

	@Override
	public String getIdentification()
	{
		return representing.getClass().getSimpleName() + " " + representing;
	}

	@Override
	public VarScoped getOwningObject()
	{
		return representing;
	}

}
