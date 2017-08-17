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
package pcgen.rules.persistence.token;

import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;

/**
 * A ModifierFactory is an object designed to build Modifier objects.
 * 
 * @param <T>
 *            The object type of the Modifiers built by this ModifierFactory
 */
public interface ModifierFactory<T>
{

	/**
	 * Returns a String identifying the type of Modifier built by this
	 * ModifierFactory. May be "ADD" for a ModifierFactory that performs
	 * Addition.
	 * 
	 * @return A String identifying the type of Modifier built by this
	 *         ModifierFactory
	 */
	public String getIdentification();

	/**
	 * Returns the Format (Class) of object upon which this ModifierFactory can
	 * operate. May be a parent class if the ModifierFactory can act upon
	 * various related classes such as java.lang.Number.
	 * 
	 * @return The Format (Class) of object upon which Modifiers built by this
	 *         ModifierFactory can operate
	 */
	public Class<T> getVariableFormat();

	/**
	 * Returns a Modifier with the given user priority and instructions. The
	 * instructions will be parsed, and an IllegalArgumentException thrown if
	 * the instructions are not valid for this type of ModifierFactory.
	 * 
	 * @param userPriority
	 *            The User Priority for the Modifier to be returned
	 * @param instructions
	 *            The String form of the instructions of the Modifier to be
	 *            returned
	 * @param managerFactory
	 *            The ManagerFactory to be used to support analyzing the
	 *            instructions
	 * @param formulaManager
	 *            The FormulaManager used, if necessary, to initialize the
	 *            Modifier to be returned
	 * @param varScope
	 *            The VariableScope for the Modifier to be returned
	 * @param formatManager
	 *            The FormatManager for the Modifier to be returned
	 */
	public PCGenModifier<T> getModifier(int userPriority, String instructions,
		ManagerFactory managerFactory, FormulaManager formulaManager, LegalScope varScope,
		FormatManager<T> formatManager);

	public Modifier<T> getFixedModifier(int userPriority,
		FormatManager<T> formatManager, String instructions);
}
