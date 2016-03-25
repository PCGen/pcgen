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
package pcgen.cdom.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.calculation.NEPCalculation;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.util.FormatManager;

/**
 * A FormulaCalc is a Modifier that is a wrapper around a NEPCalculation. A
 * FormulaCalc also contains the user priority of the Modifier.
 * 
 * @see pcgen.base.calculation.NEPCalculation
 * 
 * @param <T>
 *            The format that this FormulaCalc acts upon
 */
public final class FormulaCalc<T> implements FormulaModifier<T>
{

	/**
	 * The user priority for this FormulaCalc.
	 */
	private int userPriority = 0;

	/**
	 * The NEPCalculation to be performed by this FormulaCalc.
	 */
	private final NEPCalculation<T> toDo;

	private final FormatManager<T> formatManager;

	/**
	 * Constructs a new FormulaCalc from the given NEPCalculation.
	 * 
	 * The intent is that a solver would process the Modifier with the lowest
	 * user priority first.
	 * 
	 * @param calc
	 *            The NEPCalculation to be performed by this FormulaCalc when it
	 *            is processed
	 * @throws IllegalArgumentException
	 *             if the given NEPCalculation is null
	 */
	public FormulaCalc(NEPCalculation<T> calc, FormatManager<T> fmtManager)
	{
		if (calc == null)
		{
			throw new IllegalArgumentException("Calculation cannot be null");
		}
		if (fmtManager == null)
		{
			throw new IllegalArgumentException("FormatManager cannot be null");
		}
		toDo = calc;
		formatManager = fmtManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getUserPriority()
	{
		return userPriority;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T process(T input, ScopeInformation scopeInfo, Object source)
	{
		//TODO Associations???
		return toDo.process(input, scopeInfo, source);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInherentPriority()
	{
		return toDo.getInherentPriority();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getDependencies(ScopeInformation scopeInfo,
		DependencyManager fdm, Class<?> assertedFormat)
	{
		toDo.getDependencies(scopeInfo, fdm, assertedFormat);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInstructions()
	{
		return toDo.getInstructions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<T> getVariableFormat()
	{
		return toDo.getVariableFormat();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentification()
	{
		return toDo.getIdentification();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		return userPriority ^ toDo.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof FormulaCalc)
		{
			FormulaCalc<?> other = (FormulaCalc<?>) o;
			return (other.userPriority == userPriority)
				&& other.toDo.equals(toDo);
		}
		return false;
	}

	@Override
	public void addAssociation(String assocInstructions)
	{
		if (assocInstructions.startsWith("=")
			|| assocInstructions.endsWith("="))
		{
			throw new IllegalArgumentException(
				"Cannot process instructions starting or ending with =");
		}
		int equalLoc = assocInstructions.indexOf("=");
		if (equalLoc == -1)
		{
			throw new IllegalArgumentException(
				"Cannot process instructions without =");
		}
		String assocName = assocInstructions.substring(0, equalLoc);
		if ("PRIORITY".equalsIgnoreCase(assocName))
		{
			userPriority =
					AssociationUtilities.processUserPriority(assocInstructions);
		}
		else
		{
			throw new IllegalArgumentException("Format: "
				+ formatManager.getIdentifierType()
				+ " does not support associations other than PRIORITY");
		}
	}

	public boolean isCompatible(FormatManager<?> fm)
	{
		return formatManager.equals(fm);
	}
	
	@Override
	public Collection<String> getAssociationInstructions()
	{
		List<String> list = new ArrayList<String>();
		list.add("PRIORITY=" + userPriority);
		return list;
	}
}
