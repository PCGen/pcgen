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
package pcgen.base.solver;

import java.util.function.Supplier;

import pcgen.base.util.ComplexResult;
import pcgen.base.util.FormatManager;

/**
 * A SolverFactory is a centralized location to define a shared default value
 * for a format of Solver, and then construct Solver objects loaded with that
 * common default.
 * 
 * The format of Solver is represented by a Class object.
 */
public interface SolverFactory
{

	/**
	 * Adds a relationship between a Solver format and a default value for that format of
	 * Solver to this SolverFactory.
	 * 
	 * The default value for a format of Solver may not be redefined for a
	 * SolverFactory. Once a given default Supplier has been established for a format of
	 * Solver, this method MUST NOT be called a second time for that format of Solver.
	 * 
	 * @param <T>
	 *            The format (class) of object changed by the given Supplier
	 * @param formatManager
	 *            The FormatManager of the Solver format for which the given Supplier
	 *            should provide the default value
	 * @param defaultValue
	 *            The Supplier to be used to get the default value for the given Solver
	 *            format
	 * @throws IllegalArgumentException
	 *             If the given Solver format already has a default value defined for this
	 *             SolverFactory
	 */
	public <T> void addSolverFormat(FormatManager<T> formatManager,
		Supplier<? extends T> defaultValue);

	/**
	 * Returns a ComplexResult indicating the status of validating the defaults added to
	 * the addSolverFormat method. If all of the defaults are usable, this will return a
	 * ComplexResult indicating TRUE. If not, it will return a ComplexResult indicating
	 * FALSE and an appropriate message.
	 * 
	 * @return A ComplexResult indicating the status of validating the defaults added to
	 *         the addSolverFormat method
	 */
	public <T> ComplexResult<Boolean> validateDefaults();

	/**
	 * Returns a new Solver for the given format. The default value of the
	 * Solver is loaded based on values previously provided to the
	 * addSolverType() method of the SolverFactory.
	 * 
	 * @param <T>
	 *            The format (class) of object managed by the given
	 *            FormatManager
	 * @param formatManager
	 *            The FormatManager used to manage items in this generated
	 *            Solver
	 * @return A new Solver with default Modifier stored in this SolverFactory
	 * @throws IllegalArgumentException
	 *             if no default Modifier for the given format has been provided
	 *             with the addSolverType method on SolverFactory
	 */
	public <T> Solver<T> getSolver(FormatManager<T> formatManager);

	/**
	 * Returns the default value for a given FormatManager.
	 * 
	 * @param <T>
	 *            The format (class) of object for which the default value
	 *            should be returned
	 * @param formatManager
	 *            The FormatManager for which the default value should be
	 *            returned
	 * @return The default value for the given FormatManager
	 */
	public <T> T getDefault(FormatManager<T> formatManager);
}
