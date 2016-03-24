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
import java.util.List;

import pcgen.base.calculation.Modifier;
import pcgen.base.formula.base.EvaluationManager;
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
	 * The underlying EvaluationManager for this Solver, used when a Modifier
	 * uses a Formula to determine the output value.
	 */
	private final EvaluationManager evaluationManager;

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
	private final TreeMapToList<Long, ModInfo<T>> modifierList =
			new TreeMapToList<Long, ModInfo<T>>();

	/**
	 * A map of sources to the Modifiers provided by that source. This is used
	 * for tracing responsibility for modification as well as allowing a
	 * "global remove" of Modifiers from a given source.
	 */
	private final HashMapToList<Object, Modifier<T>> sourceList =
			new HashMapToList<Object, Modifier<T>>();

	/**
	 * Constructs a new Solver with the given default Modifier and
	 * ScopeInformation.
	 * 
	 * The default Modifier MUST NOT depend on anything (it must be able to
	 * accept both a null ScopeInformation and null input value to its process
	 * method). (See SetNumberModifier for an example of this)
	 * 
	 * @param defaultModifier
	 *            The "starting" or "default" modifier for this Solver
	 * @param evaluationManager
	 *            The underlying EvaluationManager for this Solver, used when a
	 *            Modifier uses a Formula to determine the output value
	 */
	public Solver(Modifier<T> defaultModifier, EvaluationManager evaluationManager)
	{
		if (defaultModifier == null)
		{
			throw new IllegalArgumentException(
				"Default Modifier cannot be null");
		}
		if (evaluationManager == null)
		{
			throw new IllegalArgumentException(
				"EvaluationManager cannot be null");
		}
		//Enforce no dependencies
		try
		{
			defaultModifier.process(evaluationManager);
		}
		catch (NullPointerException e)
		{
			throw new IllegalArgumentException(
				"Default Modifier must support null input", e);
		}
		this.defaultModifier = defaultModifier;
		this.evaluationManager = evaluationManager;
	}

	/**
	 * Add a Modifier (from the given source) to this Solver. The Modifier will
	 * be processed in the order defined by the priority of the Modifier.
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
		modifierList.addToListFor(Long.valueOf(modifier.getPriority()),
			new ModInfo<>(modifier, source));
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
		modifierList.removeFromListFor(Long.valueOf(modifier.getPriority()),
			new ModInfo<>(modifier, source));
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
				@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
				ModInfo<T> modInfo = new ModInfo<>(modifier, source);
				modifierList.removeFromListFor(
					Long.valueOf(modifier.getPriority()), modInfo);
			}
		}
	}

	/**
	 * Process this Solver to provide the value after all Modifiers are
	 * processed (in priority order).
	 * 
	 * @return The resulting value after all Modifier objects are processed
	 */
	public T process()
	{
		T result = defaultModifier.process(evaluationManager);
		for (Long priority : modifierList.getKeySet())
		{
			for (ModInfo<T> modInfo : modifierList.getListFor(priority))
			{
				evaluationManager.set(EvaluationManager.INPUT, result);
				result = modInfo.modifier.process(evaluationManager);
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
		T stepResult = defaultModifier.process(evaluationManager);
		steps.add(new ProcessStep<T>(defaultModifier, new DefaultValue(
			defaultModifier.getVariableFormat().getSimpleName()), stepResult));
		if (!modifierList.isEmpty())
		{
			for (Long priority : modifierList.getKeySet())
			{
				for (ModInfo<T> modInfo : modifierList.getListFor(priority))
				{
					evaluationManager.set(EvaluationManager.INPUT, stepResult);
					stepResult = modInfo.modifier.process(evaluationManager);
					@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
					ProcessStep<T> step =
							new ProcessStep<T>(modInfo.modifier, modInfo.source, stepResult);
					steps.add(step);
				}
			}
		}
		return steps;
	}

	/**
	 * Carries the Default Value information for display in diagnosis
	 */
	private class DefaultValue
	{
		/**
		 * The reporting String indicating that the value is the default value
		 * for a given format
		 */
		private String reportString;
		
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

	/**
	 * Constructs a new ModInfo to store information about a Modifier and the
	 * source of the modifier
	 * 
	 * @param <IT>
	 *            The format the included Modifier acts upon
	 */
	private static final class ModInfo<IT>
	{
		private final Modifier<IT> modifier;
		private final Object source;

		public ModInfo(Modifier<IT> modifier, Object source)
		{
			this.modifier = modifier;
			this.source = source;
		}

		public Modifier<IT> getModifier()
		{
			return modifier;
		}

		public Object getSource()
		{
			return source;
		}

		@Override
		public int hashCode()
		{
			return modifier.hashCode() ^ source.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ModInfo)
			{
				ModInfo<?> other = (ModInfo<?>) obj;
				return modifier.equals(other.modifier)
					&& source.equals(other.source);
			}
			return false;
		}

	}
}
