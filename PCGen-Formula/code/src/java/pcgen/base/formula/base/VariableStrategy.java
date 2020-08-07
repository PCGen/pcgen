/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

/**
 * A VariableStrategy is an interface for defining an appropriate behavior to be taken
 * when a variable is encountered in a formula. This behavior can change, based on whether
 * the current context is static or dynamic. Therefore, multiple strategies exist.
 */
@FunctionalInterface
public interface VariableStrategy
{
	/**
	 * Adds a Variable to the DependencyManager based on the behavior of the strategy.
	 * 
	 * @param depManager
	 *            The DependencyManager to which the variable should be added
	 * @param varName
	 *            The name of the variable to be added to the DependencyManager
	 */
	public void addVariable(DependencyManager depManager, String varName);
}
