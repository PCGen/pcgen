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
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.base.calculation.AbstractNEPCalculation;
import pcgen.base.calculation.BasicCalculation;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.util.Indirect;

/**
 * An IndirectCalculation is an AbstractNEPCalculation that uses an Indirect object for
 * the calculation.
 * 
 * @param <T>
 *            The format of object on which this IndirectCalculation operates
 */
public final class IndirectCalculation<T> extends AbstractNEPCalculation<T>
{
	/**
	 * The underlying Indirect pointing to the object to be passed in to the
	 * BasicCalculation of this IndirectCalculation when it is processed.
	 */
	private final Indirect<T> obj;

	/**
	 * Constructs a new IndirectCalculation from the given Indirect and BasicCalculation.
	 * 
	 * @param object
	 *            The Indirect pointing to the object to be passed into the given
	 *            BasicCalculation when this IndirectCalculation is processed
	 * @param calc
	 *            The BasicCalculation which defines the operation to be performed when
	 *            this IndirectCalculation is processed
	 */
	public IndirectCalculation(Indirect<T> object, BasicCalculation<T> calc)
	{
		super(calc);
		this.obj = Objects.requireNonNull(object);
	}

	@Override
	public T process(EvaluationManager evalManager)
	{
		@SuppressWarnings("unchecked")
		T input = (evalManager == null) ? null : (T) evalManager.get(EvaluationManager.INPUT);
		return getBasicCalculation().process(input, obj.get());
	}

	@Override
	public String getInstructions()
	{
		return obj.getUnconverted();
	}

	@Override
	public int hashCode()
	{
		return obj.hashCode() ^ getBasicCalculation().hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof IndirectCalculation<?> other)
		{
			return other.getBasicCalculation().equals(getBasicCalculation()) && other.obj.equals(obj);
		}
		return false;
	}

	@Override
	public void getDependencies(DependencyManager fdm)
	{
		//CONSIDER: How does DependencyManager want to know about Indirect?
	}

	@Override
	public void isValid(FormulaSemantics semantics)
	{
		/*
		 * Since this is direct (already has a reference to the object), it has no
		 * semantic issues (barring someone violating Generics)
		 */
	}
}
