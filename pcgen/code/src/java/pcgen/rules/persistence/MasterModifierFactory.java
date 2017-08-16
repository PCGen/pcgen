/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence;

import pcgen.base.calculation.IgnoreVariables;
import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.util.FormatManager;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.helper.ReferenceDependency;
import pcgen.rules.persistence.token.ModifierFactory;

/**
 * The MasterModifierFactory is a single location to request Modifier objects.
 * This manages the delegation to the actual ModifierFactory capable of
 * producing a Modifier of the requested identifier.
 */
public class MasterModifierFactory
{

	/**
	 * The FormulaManager underlying this MasterModifierFactory, which is used
	 * to process any formulas in the instructions of a requested Modifier.
	 */
	private final FormulaManager formulaManager;

	/**
	 * Constructs a new MasterModifierFactory with the given FormulaManager and
	 * ReferenceContext.
	 * 
	 * @param fm
	 *            The FormulaManager to be used to process any formulas in the
	 *            instructions of a requested Modifier
	 */
	public MasterModifierFactory(FormulaManager fm)
	{
		formulaManager = fm;
	}

	/**
	 * Returns a Modifier representing the information given in the parameters.
	 * 
	 * @param modIdentifier
	 *            The Identifier of the Modifier (indicating the general
	 *            function being performed, e.g. ADD)
	 * @param modInstructions
	 *            The Instructions of the Modifier (indicating the actual value
	 *            the Modifier will use)
	 * @param managerFactory
	 *            The ManagerFactory to be used to support analyzing the
	 *            instructions
	 * @param priorityNumber
	 *            The user priority of the Modifier to be produced
	 * @param varScope
	 *            The VariableScope for the Modifier to be returned
	 * @param formatManager
	 *            The FormatManager for the Modifier to be returned
	 * @return a Modifier representing the information given in the parameters
	 */
	public <T> PCGenModifier<T> getModifier(String modIdentifier, String modInstructions,
		ManagerFactory managerFactory, int priorityNumber, LegalScope varScope,
		FormatManager<T> formatManager)
	{
		Class<T> varClass = formatManager.getManagedClass();
		ModifierFactory<T> factory =
				TokenLibrary.getModifier(varClass, modIdentifier);
		if (factory == null)
		{
			throw new IllegalArgumentException(
				"Requested unknown ModifierType: " + varClass.getSimpleName()
					+ " " + modIdentifier);
		}
		PCGenModifier<T> modifier = factory.getModifier(priorityNumber, modInstructions,
			managerFactory, formulaManager, varScope, formatManager);
		/*
		 * getDependencies needs to be called during LST load, so that object references are captured
		 */
		DependencyManager fdm = managerFactory.generateDependencyManager(formulaManager,
			null, formatManager.getManagedClass());
		fdm = fdm.getWith(DependencyManager.VARSTRATEGY, new IgnoreVariables());
		fdm = fdm.getWith(ManagerKey.REFERENCES, new ReferenceDependency());
		modifier.getDependencies(fdm);
		modifier.addReferences(fdm.get(ManagerKey.REFERENCES).getReferences());
		return modifier;
	}
}
