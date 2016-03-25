/*
 * Copyright 2015-16 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.formula.inst.ScopeInstanceFactory;
import pcgen.base.formula.inst.SimpleFormulaManager;
import pcgen.base.formula.inst.SimpleVariableStore;

/**
 * An IndividualSetup is returned by a SplitFormulaSetup. This contains the
 * information unique to a specific solution area.
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
	 * The "Global" LegalScope for this IndividualSetup.
	 */
	private final LegalScope globalScope;

	/**
	 * The "Global" ScopeInstance for this IndividualSetup.
	 */
	private final ScopeInstance globalScopeInst;

	/**
	 * The ScopeInformation for this IndividualSetup.
	 */
	private final ScopeInformation scopeInfo;

	/**
	 * The WriteableVariableStore for this DefaultFormulaSetup. Lazily
	 * Instantiated.
	 */
	private WriteableVariableStore variableStore;

	/**
	 * Constructs a new IndividualSetup with the "global" LegalScope of the
	 * given name. The IndividualSetup will have a unique Global Scope Instance,
	 * VariableStore (and thus FormulaManager and ScopeInformation).
	 * 
	 * Note: A LegalScope object with the given name MUST have been loaded into
	 * the LegalScopeLibrary of the SplitFormulaSetup or this will throw an
	 * Exception.
	 * 
	 * Note: The LegalScope returned by the LegalScopeLibrary of the given
	 * SplitFormulaSetup must also be a "Global" scope in that it must return
	 * null as the parent LegalScope or this will throw an Exception.
	 * 
	 * @param parent
	 *            The parent SplitFormulaSetup for this IndividualSetup
	 * @param globalName
	 *            The name of the "global" LegalScope for this IndividualSetup
	 */
	public IndividualSetup(SplitFormulaSetup parent, String globalName)
	{
		LegalScopeLibrary scopeLibrary = parent.getLegalScopeLibrary();
		globalScope = scopeLibrary.getScope(globalName);
		instanceFactory = new ScopeInstanceFactory(scopeLibrary);
		formulaManager =
				new SimpleFormulaManager(parent.getFunctionLibrary(),
					parent.getOperatorLibrary(), parent.getVariableLibrary(),
					getVariableStore());
		globalScopeInst = instanceFactory.getInstance(null, globalScope);
		scopeInfo = new ScopeInformation(formulaManager, globalScopeInst);
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

	/**
	 * Return the "Global" LegalScope for this IndividualSetup.
	 * 
	 * @return the "Global" LegalScope for this IndividualSetup
	 */
	public LegalScope getGlobalScope()
	{
		return globalScope;
	}

	/**
	 * Return the "Global" ScopeInstance for this IndividualSetup.
	 * 
	 * @return the "Global" ScopeInstance for this IndividualSetup
	 */
	public ScopeInstance getGlobalScopeInst()
	{
		return globalScopeInst;
	}

	/**
	 * Return the ScopeInformation for this IndividualSetup.
	 * 
	 * @return the ScopeInformation for this IndividualSetup
	 */
	public ScopeInformation getScopeInfo()
	{
		return scopeInfo;
	}

	/**
	 * Return the VariableStore for this IndividualSetup.
	 * 
	 * @return the VariableStore for this IndividualSetup
	 */
	public WriteableVariableStore getVariableStore()
	{
		if (variableStore == null)
		{
			variableStore = buildVariableStore();
		}
		return variableStore;
	}
	
	/**
	 * Builds a new WriteableVariableStore for this IndividualSetup. This is
	 * intended to be called once during construction of the IndividualSetup.
	 * 
	 * @return a new WriteableVariableStore for this IndividualSetup.
	 */
	protected WriteableVariableStore buildVariableStore()
	{
		return new SimpleVariableStore();
	}

}
