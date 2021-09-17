/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.cdom.formula.scope.PCGenScope;

/**
 * A VarModifier is a container for all the information necessary to modify a
 * variable. This includes the scope, the variable name, and the FormulaModifier
 * to be applied. This allows that grouping of information to be passed as a
 * single unit of information.
 * 
 * @param <T>
 *            The format of the variable modified by the FormulaModifier in this
 *            VarModifier
 */
public class VarModifier<T>
{
	/**
	 * This is an empty array of VarModifier objects, available for use by a
	 * VarContainer.
	 */
	public static final VarModifier<?>[] EMPTY_VARMODIFIER = new VarModifier[0];

	/**
	 * The name of the Variable to be modified when this VarModifier is applied.
	 */
	private final String varName;

	/**
	 * The PCGenScope of the variable to be modified when this VarModifier is
	 * applied.
	 */
	private final PCGenScope legalScope;

	/**
	 * The FormulaModifier to be applied to the Variable when this VarModifier is
	 * applied.
	 */
	private final FormulaModifier<T> modifier;

	/**
	 * Constructs a new VarModifier containing all the information necessary to
	 * modify a variable.
	 * 
	 * @param varName
	 *            The name of the Variable to be modified when this VarModifier
	 *            is applied
	 * @param legalScope
	 *            the PCGenScope in which the Modifier is applied
	 * @param modifier
	 *            The FormulaModifier to be applied to the Variable when this
	 *            VarModifier is applied
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null
	 */
	public VarModifier(String varName, PCGenScope legalScope, FormulaModifier<T> modifier)
	{
		Objects.requireNonNull(varName, "Var Name cannot be null");
		Objects.requireNonNull(legalScope, "PCGenScope cannot be null");
		Objects.requireNonNull(modifier, "Modifier cannot be null");
		this.varName = varName;
		this.legalScope = legalScope;
		this.modifier = modifier;
	}

	/**
	 * Retrieves the Variable Name for this VarModifier.
	 * 
	 * @return the Variable Name for this VarModifier
	 */
	public String getVarName()
	{
		return varName;
	}

	/**
	 * Retrieves the PCGenScope for this VarModifier.
	 * 
	 * @return the PCGenScope for this VarModifier
	 */
	public PCGenScope getLegalScope()
	{
		return legalScope;
	}

	/**
	 * Retrieves the FormulaModifier for this VarModifier.
	 * 
	 * @return the FormulaModifier for this VarModifier
	 */
	public FormulaModifier<T> getModifier()
	{
		return modifier;
	}

	@Override
	public int hashCode()
	{
		return varName.hashCode() ^ modifier.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof VarModifier<?> other)
		{
			return other.varName.equals(varName) && other.legalScope.equals(legalScope)
				&& other.modifier.equals(modifier);
		}
		return false;
	}

	/**
	 * Returns the fully qualified Legal Scope Name for the LegalScope in this
	 * VarModifier.
	 * 
	 * (e.g. The LegalScope might be "SKILL" and the fully qualified name "PC.SKILL")
	 * 
	 * @return The fully qualified Legal Scope Name for the LegalScope in this VarModifier
	 */
	public String getFullLegalScopeName()
	{
		return LegalScope.getFullName(getLegalScope());
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": " + varName + " ("
			+ legalScope.getName() + ") " + modifier;
	}
}
