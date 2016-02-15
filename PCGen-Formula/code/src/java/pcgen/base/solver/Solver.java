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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.calculation.Modifier;
import pcgen.base.formula.inst.ScopeInformation;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.TreeMapToList;

/**
 * A Solver manages a series of Modifiers in order to "solve" those Modifiers to
 * produce the result for a single "variable" (specifically managed by a
 * VariableID).
 * 
 * The primary role of a Solver is to process the priority of each Modifier
 * added to the Solver (both user priority and inherent priority) in order to
 * "schedule" each Modifier (to put them in an ordered list for how they will be
 * processed).
 * 
 * Note that a Solver makes NO attempt to understand whether it is behaving
 * correctly relative to other Solver instances. That cross-variable resolution
 * is done by a SolverManager.
 * 
 * @param <T>
 *            The format of object that this Solver operates on (e.g.
 *            java.lang.Number)
 */
public class Solver<T>
{

	/**
	 * The underlying ScopeInformation for this Solver, used when a Modifier
	 * uses a Formula to determine the output value.
	 */
	private final ScopeInformation scopeInfo;

	/**
	 * The "starting" or "default" modifier for this Solver. This is the value
	 * the Solver has if no other Modifier was added to the Solver.
	 * 
	 * Note that this Modifier MUST NOT depend on anything (it must be able to
	 * accept both a null ScopeInformation and null input value to its process
	 * method).
	 */
	private final Modifier<T> defaultModifier;

	/**
	 * The list of Modifiers for this Solver. This is maintained as an ordered
	 * list: TreeMap sorts the Modifiers by their priority.
	 */
	private final TreeMapToList<Long, Modifier<T>> modifierList =
			new TreeMapToList<Long, Modifier<T>>();

	/**
	 * A map of sources to the Modifiers provided by that source. This is used
	 * for tracing responsibility for modification as well as allowing a
	 * "global remove" of Modifiers from a given source.
	 */
	private final HashMapToList<Object, Modifier<T>> sourceList =
			new HashMapToList<Object, Modifier<T>>();

	/**
	 * Constructs a new Solver with the given default Modifier and
	 * FormulaManager.
	 * 
	 * The default Modifier MUST NOT depend on anything (it must be able to
	 * accept both a null ScopeInformation and null input value to its process
	 * method). (See SetNumberModifier for an example of this)
	 * 
	 * @param defaultModifier
	 *            The "starting" or "default" modifier for this Solver
	 * @param scopeInfo
	 *            The underlying ScopeInformation for this Solver, used when a
	 *            Modifier uses a Formula to determine the output value
	 */
	public Solver(Modifier<T> defaultModifier, ScopeInformation scopeInfo)
	{
		if (defaultModifier == null)
		{
			throw new IllegalArgumentException(
				"Default Modifier cannot be null");
		}
		if (scopeInfo == null)
		{
			throw new IllegalArgumentException(
				"ScopeInformation cannot be null");
		}
		//Enforce no dependencies
		try
		{
			defaultModifier.process(null, null);
		}
		catch (NullPointerException e)
		{
			throw new IllegalArgumentException(
				"Default Modifier must support null input", e);
		}
		this.defaultModifier = defaultModifier;
		this.scopeInfo = scopeInfo;
	}

	/**
	 * Add a Modifier (from the given source) to this Solver. The Modifier will
	 * be processed in the order defined by the priority of the Modifier
	 * 
	 * null is not a valid source.
	 * 
	 * @param modifier
	 *            The Modifier to be added to this Solver
	 * @param source
	 *            The source object for the given Modifier
	 * @throws IllegalArgumentException
	 *             if any of the parameters is null
	 */
	public void addModifier(Modifier<T> modifier, Object source)
	{
		if (modifier == null)
		{
			throw new IllegalArgumentException("Cannot add null Modifier");
		}
		if (source == null)
		{
			throw new IllegalArgumentException(
				"Cannot add Modifier with null source");
		}
		//Ensure someone isn't playing fast and loose with generics
		Class<?> varFormat = defaultModifier.getVariableFormat();
		if (!modifier.getVariableFormat().equals(varFormat))
		{
			throw new IllegalArgumentException(
				"Expected Modifier of Process Class: "
					+ varFormat.getCanonicalName() + " but got: "
					+ modifier.getVariableFormat().getCanonicalName());
		}
		modifierList
			.addToListFor(Long.valueOf(getPriority(modifier)), modifier);
		sourceList.addToListFor(source, modifier);
	}

	/**
	 * Removes the given Modifier (From the given source) from this Solver.
	 * 
	 * For this to have any effect, the combination of Modifier and source must
	 * be the same (as defined by .equals() equality) as a combination provided
	 * to the addModifier method of this Solver.
	 * 
	 * @param modifier
	 *            The Modifier to be removed from this Solver
	 * @param source
	 *            The source object for the Modifier to be removed from this
	 *            Solver
	 * @throws IllegalArgumentException
	 *             if the given Modifier is null
	 */
	public void removeModifier(Modifier<T> modifier, Object source)
	{
		if (modifier == null)
		{
			throw new IllegalArgumentException("Cannot remove null Modifier");
		}
		if (source == null)
		{
			throw new IllegalArgumentException(
				"Cannot remove Modifier with null source");
		}
		modifierList.removeFromListFor(Long.valueOf(getPriority(modifier)),
			modifier);
		sourceList.removeFromListFor(source, modifier);
	}

	/**
	 * Removes all Modifiers from a given source (as defined by .equals()
	 * equality for the given source Object).
	 * 
	 * @param source
	 *            The source for which all Modifiers should be removed from this
	 *            Solver
	 * @throws IllegalArgumentException
	 *             if the given source object is null
	 */
	public void removeFromSource(Object source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException(
				"Cannot remove Modifiers with null source");
		}
		List<Modifier<T>> removed = sourceList.removeListFor(source);
		if (removed != null)
		{
			for (Modifier<T> modifier : removed)
			{
				modifierList.removeFromListFor(
					Long.valueOf(getPriority(modifier)), modifier);
			}
		}
	}

	/**
	 * Gets the full priority of the Modifier. This processes the rules
	 * described on the Modifier interface, in that the user priority takes
	 * precedence, and then the inherent priority is used. Builds these into a
	 * single long value, so that a single sort can be used to process both
	 * priority values.
	 * 
	 * @param modifier
	 *            The Modifier for which the full priority should be returned
	 * @return A long representing the combination of the user priority and
	 *         inherent priority
	 */
	private long getPriority(Modifier<T> modifier)
	{
		int inherentPriority = modifier.getInherentPriority();
		if (inherentPriority < 0)
		{
			/*
			 * Required to reject this or the ordering will not be correct due
			 * to bitwise creation of overall priority. This "contract" is
			 * defined on the Modifier interface.
			 */
			throw new IllegalArgumentException(
				"Cannot add Modifier with InherentPriority < 0");
		}
		/*
		 * TODO Some limit on user priority; (positive?)
		 */
		//Must be a long or the shift below will result in 0 effective user priority
		long userPriority = modifier.getUserPriority();
		return (userPriority << 32) + inherentPriority;
	}

	/**
	 * Process this Solver to provide the value after all Modifiers are
	 * processed (in priority order).
	 * 
	 * @return The resulting value after all Modifier objects are processed
	 */
	public T process()
	{
		T result = defaultModifier.process(null, scopeInfo);
		for (Long priority : modifierList.getKeySet())
		{
			for (Modifier<T> modifier : modifierList.getListFor(priority))
			{
				result = modifier.process(result, scopeInfo);
			}
		}
		return result;
	}

	/**
	 * Provides a "debugging" view of the operations taking place in this
	 * Solver. This returns a List of ProcessStep objects that are an ordered
	 * list of the steps taken and the value after each step.
	 * 
	 * @return A list of ProcessStep objects indicating the operations that take
	 *         place in this Solver when process() is called
	 */
	public List<ProcessStep<T>> diagnose()
	{
		List<ProcessStep<T>> steps = new ArrayList<ProcessStep<T>>();
		T stepResult = defaultModifier.process(null, scopeInfo);
		steps.add(new ProcessStep<T>(defaultModifier, new DefaultValue(
			defaultModifier.getVariableFormat().getSimpleName()), stepResult));
		if (!modifierList.isEmpty())
		{
			Map<Modifier<T>, Object> sources = getReversedSources();
			for (Long priority : modifierList.getKeySet())
			{
				for (Modifier<T> modifier : modifierList.getListFor(priority))
				{
					stepResult = modifier.process(stepResult, scopeInfo);
					@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
					ProcessStep<T> step =
							new ProcessStep<T>(modifier, sources.get(modifier),
								stepResult);
					steps.add(step);
				}
			}
		}
		return steps;
	}

	/**
	 * A Convenience method used to "reverse" the sourceList map. This is
	 * intended to be private, and since it is used in a "diagnosis"
	 * infrastructure is not particularly performance sensitive.
	 * 
	 * @return A version of the sourceList map showing the source of each
	 *         Modifier in this Solver
	 */
	private Map<Modifier<T>, Object> getReversedSources()
	{
		Map<Modifier<T>, Object> reversedMap =
				new IdentityHashMap<Modifier<T>, Object>();
		for (Object source : sourceList.getKeySet())
		{
			for (Modifier<T> modifier : sourceList.getListFor(source))
			{
				reversedMap.put(modifier, source);
			}
		}
		return reversedMap;
	}

	private class DefaultValue
	{
		String reportString;
		
		public DefaultValue(String formatName)
		{
			this.reportString = "for " + formatName;
		}
		
		@Override
		public String toString()
		{
			return reportString;
		}
	}
}
