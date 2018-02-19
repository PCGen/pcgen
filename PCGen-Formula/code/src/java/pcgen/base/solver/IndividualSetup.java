/*
 * Copyright 2015-16 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableStore;
import pcgen.base.formula.inst.SimpleFormulaManager;
import pcgen.base.formula.inst.SimpleScopeInstanceFactory;

/**
 * An IndividualSetup is returned by a SplitFormulaSetup. This contains the information
 * unique to a specific solution area.
 */
public class IndividualSetup
{

	/**
	 * The ScopeInstanceFactory for this IndividualSetup.
	 */
	private final ScopeInstanceFactory instanceFactory;

	/**
	 * The FormulaManager for this IndividualSetup.
	 */
	private final FormulaManager formulaManager;

	/**
	 * Constructs a new IndividualSetup with the "global" LegalScope of the given name.
	 * The IndividualSetup will have a unique Global Scope Instance, VariableStore (and
	 * thus FormulaManager and ScopeInformation).
	 * 
	 * Note: A LegalScope object with the given name MUST have been loaded into the
	 * LegalScopeLibrary of the SplitFormulaSetup or this will throw an Exception.
	 * 
	 * Note: The LegalScope returned by the LegalScopeLibrary of the given
	 * SplitFormulaSetup must also be a "Global" scope in that it must return null as the
	 * parent LegalScope or this will throw an Exception.
	 * 
	 * @param parent
	 *            The parent SplitFormulaSetup for this IndividualSetup
	 * @param variableStore
	 *            the VariableStore to be used by the FormulaManager in this
	 *            IndividualSetup
	 */
	public IndividualSetup(SplitFormulaSetup parent, VariableStore variableStore)
	{
		LegalScopeLibrary scopeLibrary = parent.getLegalScopeManager();
		instanceFactory = new SimpleScopeInstanceFactory(scopeLibrary);
		SimpleFormulaManager fManager = new SimpleFormulaManager(
			parent.getOperatorLibrary(), parent.getVariableLibrary(), instanceFactory,
			variableStore, parent.getSolverFactory());
		formulaManager =
				fManager.getWith(FormulaManager.FUNCTION, parent.getFunctionLibrary());
	}

	/**
	 * Return the ScopeInstanceFactory for this IndividualSetup.
	 * 
	 * @return the ScopeInstanceFactory for this IndividualSetup
	 */
	public ScopeInstanceFactory getInstanceFactory()
	{
		return instanceFactory;
	}

	/**
	 * Return the FormulaManager for this IndividualSetup.
	 * 
	 * @return the FormulaManager for this IndividualSetup
	 */
	public FormulaManager getFormulaManager()
	{
		return formulaManager;
	}
}
