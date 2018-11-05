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

import pcgen.base.formula.base.LegalScope;
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
	private final ScopeInstance parent;

	/**
	 * Contains the LegalScope in which this ScopeInstance was instantiated.
	 */
	private final LegalScope scope;

	/**
	 * Contains the VarScoped that this ScopeInstance was instantiated to represent.
	 */
	private final VarScoped representing;

	/**
	 * Constructs a new SimpleScopeInstance with the given parent ScopeInstance and within
	 * the given LegalScope.
	 * 
	 * @param parent
	 *            the ScopeInstance that is the parent of this ScopeInstance
	 * @param scope
	 *            the LegalScope in which this ScopeInstance was instantiated
	 * @param representing
	 *            The VarScoped object that this ScopeInstance represents
	 */
	public SimpleScopeInstance(ScopeInstance parent, LegalScope scope,
		VarScoped representing)
	{
		this.representing = Objects.requireNonNull(representing);
		Objects.requireNonNull(scope);
		if (parent == null)
		{
			if (scope.getParentScope().isPresent())
			{
				throw new IllegalArgumentException(
					"Incompatible ScopeInstance and LegalScope: "
						+ "Parent may only be null " + "when LegalScope has no parent");
			}
		}
		else if (scope.getParentScope().isPresent())
		{
			LegalScope parentScope = scope.getParentScope().get();
			if (!parentScope.equals(parent.getLegalScope()))
			{
				throw new IllegalArgumentException(
					"Incompatible ScopeInstance (" + parent.getLegalScope().getName()
						+ ") and LegalScope parent (" + parentScope.getName() + ")");
			}
		}
		else
		{
			throw new IllegalArgumentException(
				"Incompatible ScopeInstance and LegalScope: "
					+ "LegalScope Parent may only be null "
					+ "when ScopeInstance is null");
		}
		this.parent = parent;
		this.scope = scope;
	}

	@Override
	public LegalScope getLegalScope()
	{
		return scope;
	}

	@Override
	public ScopeInstance getParentScope()
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
