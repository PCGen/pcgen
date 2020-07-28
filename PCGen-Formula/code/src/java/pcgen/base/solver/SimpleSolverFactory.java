/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Objects;

import pcgen.base.util.FormatManager;

/**
 * A SimpleSolverFactory is a centralized location to define a shared default value for a
 * format of Solver, and then construct Solver objects loaded with that common default.
 * 
 * The format of Solver is represented by a Class object.
 */
public class SimpleSolverFactory implements SolverFactory
{

	/**
	 * The IndirectValueStore containing the relationship between a format of Solver and
	 * the default value for that format of Solver.
	 */
	private final SupplierValueStore valueStore;

	/**
	 * Constructs a new SimpleSolverFactory using the given IndirectValueStore as the
	 * underlying ValueStore.
	 * 
	 * @param valueStore
	 *            The IndirectValueStore to be used as the underlying ValueStore
	 */
	public SimpleSolverFactory(SupplierValueStore valueStore)
	{
		this.valueStore = Objects.requireNonNull(valueStore);
	}

	@Override
	public <T> Solver<T> getSolver(FormatManager<T> formatManager)
	{
		return new Solver<T>(formatManager, formatManager.initializeFrom(valueStore));
	}

	@Override
	public <T> T getDefault(FormatManager<T> formatManager)
	{
		return formatManager.initializeFrom(valueStore);
	}
}
