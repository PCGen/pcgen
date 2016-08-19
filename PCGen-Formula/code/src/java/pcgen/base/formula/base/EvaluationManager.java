/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.util.MappedDeque;
import pcgen.base.util.TypedKey;

/**
 * An EvaluationManager is a class to capture information used to support
 * Evaluation of a formula.
 * 
 * For this to be viable with a formula of any complexity, it must have a
 * FormulaManager and ScopeInstance loaded. Certain situations may also require
 * knowing the input value, asserted format or other characteristics that can be
 * added to the EvaluationManager.
 */
public class EvaluationManager extends MappedDeque
{

	/**
	 * A TypedKey used for storing the FormulaManager contained in this
	 * EvaluationManager.
	 */
	public static final TypedKey<FormulaManager> FMANAGER =
			new TypedKey<FormulaManager>();

	/**
	 * A TypedKey used for storing the ScopeInstance contained in this
	 * EvaluationManager.
	 */
	public static final TypedKey<ScopeInstance> INSTANCE =
			new TypedKey<ScopeInstance>();

	/**
	 * A TypedKey used for storing the Class<?> asserted by the current context
	 * of a formula
	 */
	public static final TypedKey<Class<?>> ASSERTED = new TypedKey<Class<?>>();

	/**
	 * A TypedKey used for storing the Input Object contained in this
	 * EvaluationManager.
	 */
	public static final TypedKey<Object> INPUT = new TypedKey<Object>();

	/**
	 * Generates a new EvaluationManager initialized with the given parameters
	 * 
	 * @param formulaManager
	 *            The FormulaManager used to evaluate formulas processed by this
	 *            EvaluationManager
	 * @param globalScopeInst
	 *            The ScopeInstance of formulas processed by this
	 *            EvaluationManager
	 * @param assertedFormat
	 *            the format (class) asserted by the current context of a
	 *            formula
	 * @return A new EvaluationManager initialized with the given parameters
	 */
	public static EvaluationManager generate(FormulaManager formulaManager,
		ScopeInstance globalScopeInst, Class<Number> assertedFormat)
	{
		EvaluationManager manager = new EvaluationManager();
		manager.set(FMANAGER, formulaManager);
		manager.set(INSTANCE, globalScopeInst);
		manager.set(ASSERTED, assertedFormat);
		return manager;
	}

	/**
	 * Generates a new EvaluationManager initialized with the given
	 * FormulaManager and the ScopeInstance and asserted format as implied by
	 * the given VariableID.
	 * 
	 * @param formulaManager
	 *            The FormulaManager used to evaluate formulas processed by this
	 *            EvaluationManager
	 * @param varID
	 *            The VariableID to be used to derive the the ScopeInstance and
	 *            asserted format of the EvaluationManager
	 * @return A new EvaluationManager initialized with the given FormulaManager
	 *         and the ScopeInstance and asserted format as implied by the given
	 *         VariableID.
	 */
	public static EvaluationManager generate(FormulaManager formulaManager,
		VariableID<?> varID)
	{
		EvaluationManager manager = new EvaluationManager();
		manager.set(FMANAGER, formulaManager);
		manager.set(INSTANCE, varID.getScope());
		manager.set(ASSERTED, varID.getVariableFormat());
		return manager;
	}

}
