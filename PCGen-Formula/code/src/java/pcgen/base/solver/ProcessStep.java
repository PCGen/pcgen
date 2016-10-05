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

import pcgen.base.formula.base.Identified;

/**
 * A ProcessStep is a piece of diagnostic information about a Solver.
 * 
 * A ProcessStep represents a combination of the Modifier that performed a
 * specific action, the source of that Modifier, and the result of the
 * calculation after that Modifier was applied.
 * 
 * @param <T>
 *            The format that this ProcessStep represents (the Class being acted
 *            upon in the Solver)
 */
public class ProcessStep<T>
{

	/**
	 * The Modifier that was performing the step in the process represented by
	 * this ProcessStep.
	 */
	private final Modifier<T> modifier;

	/**
	 * The source of the Modifier, for tracing where the Modifier came from.
	 */
	private final Identified source;

	/**
	 * The resulting value after the Modifier was applied.
	 */
	private final T result;

	/**
	 * Constructs a new ProcessStep containing the given Modifier, source of the
	 * Modifier, and resulting value.
	 * 
	 * @param modifier
	 *            The Modifier that was performing the step in the process
	 *            represented by this ProcessStep
	 * @param source
	 *            The source of the given Modifier, for tracing where the
	 *            Modifier came from
	 * @param result
	 *            The resulting value after the given Modifier was applied
	 */
	public ProcessStep(Modifier<T> modifier, Identified source, T result)
	{
		this.modifier = modifier;
		this.source = source;
		this.result = result;
	}

	/**
	 * Returns the Modifier contained in this ProcessStep.
	 * 
	 * @return the Modifier contained in this ProcessStep
	 */
	public Modifier<T> getModifier()
	{
		return modifier;
	}

	/**
	 * Returns the source of the Modifier contained in this ProcessStep.
	 * 
	 * @return The source of the Modifier contained in this ProcessStep
	 */
	public Identified getSource()
	{
		return source;
	}

	/**
	 * Returns the resulting value after this ProcessStep was completed by the
	 * Solver.
	 * 
	 * @return The resulting value after this ProcessStep was completed by the
	 *         Solver
	 */
	public T getResult()
	{
		return result;
	}
	
	/**
	 * Returns the information about what caused the ProcessStep. This could be
	 * either a message that the output of this step is the default value or
	 * some identification of the object that caused the processing step.
	 * 
	 * @return A non-null String providing information about what caused the
	 *         ProcessStep
	 */
	public String getSourceInfo()
	{
		return source.getIdentification();
	}
}
