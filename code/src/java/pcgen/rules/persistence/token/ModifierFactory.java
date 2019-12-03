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

import pcgen.base.calculation.FormulaModifier;
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
    String getIdentification();

	/**
	 * Returns the Format (Class) of object upon which this ModifierFactory can
	 * operate. May be a parent class if the ModifierFactory can act upon
	 * various related classes such as java.lang.Number.
	 * 
	 * @return The Format (Class) of object upon which Modifiers built by this
	 *         ModifierFactory can operate
	 */
    Class<T> getVariableFormat();

	/**
	 * Returns a FormulaModifier with the given instructions. The instructions will be
	 * parsed, and an IllegalArgumentException thrown if the instructions are not valid
	 * for this type of ModifierFactory.
	 * 
	 * @param instructions
	 *            The String form of the instructions of the FormulaModifier to be
	 *            returned
	 * @param formatManager
	 *            The FormatManager for the FormulaModifier to be returned
	 * @return a FormulaModifier with the given instructions
	 */
    FormulaModifier<T> getModifier(String instructions,
                                   FormatManager<T> formatManager);

	/**
	 * Returns a FormulaModifier with the given instructions.
	 * 
	 * The instructions must be Fixed (does not require a calculation to determine what it
	 * means), or this method will throw an exception.
	 * 
	 * The instructions will be parsed, and an IllegalArgumentException thrown if the
	 * instructions are not valid for this type of ModifierFactory.
	 * 
	 * @param formatManager
	 *            The FormatManager for the FormulaModifier to be returned
	 * @param instructions
	 *            The String form of the instructions of the FormulaModifier to be returned
	 * 
	 * @return a FormulaModifier with the given instructions
	 */
    FormulaModifier<T> getFixedModifier(FormatManager<T> formatManager, String instructions);
}
