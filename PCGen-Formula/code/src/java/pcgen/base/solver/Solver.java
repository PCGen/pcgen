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
import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.Identified;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.spotbugs.SuppressFBWarnings;
import pcgen.base.util.FormatManager;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.TreeMapToList;

/**
 * A Solver manages a series of Modifiers in order to "solve" those Modifiers to
 * produce the result for a single "variable" (specifically identified by a
 * VariableID).
 * 
 * The primary role of a Solver is to process the priority of each Modifier
 * added to the Solver (both user priority and inherent priority) in order to
 * "schedule" each Modifier (to put them in an ordered list for how they will be
 * processed).
 * 
 * Note that a Solver makes NO attempt to understand whether it is behaving
 * correctly relative to other Solver instances. That cross-variable resolution
 * is done by a SolverSystem.
 * 
 * @param <T>
 *            The format of object that this Solver operates on (e.g.
 *            java.lang.Number)
 */
public class Solver<T>
{

	/**
	 * The "starting" or "default" value for this Solver. This is the value the Solver has
	 * if no Modifier was added to the Solver.
	 */
	private final T defaultValue;

	/**
	 * The format for this Solver.
	 */
	private final FormatManager<T> formatManager;

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
	 * Constructs a new Solver with the given default Value.
	 * 
	 * @param formatManager
	 *            The FormatManager for this Solver
	 * @param defaultValue
	 *            The "starting" or "default" value for this Solver
	 */
	public Solver(FormatManager<T> formatManager, T defaultValue)
	{
		this.formatManager = Objects.requireNonNull(formatManager,
			"FormatManager cannot be null");
		this.defaultValue = Objects.requireNonNull(defaultValue,
			"Default Value cannot be null");
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
	 */
	public void addModifier(Modifier<T> modifier, ScopeInstance source)
	{
		//Ensure someone isn't playing fast and loose with generics
		if (!modifier.getVariableFormat().equals(formatManager))
		{
			throw new IllegalArgumentException("Expected Modifier of Process Class: "
				+ formatManager.getManagedClass().getCanonicalName() + " but got: "
				+ modifier.getVariableFormat().getManagedClass().getCanonicalName());
		}
		modifierList.addToListFor(Long.valueOf(modifier.getPriority()),
			new ModInfo<>(modifier, Objects.requireNonNull(source)));
		sourceList.addToListFor(source, modifier);
	}

	/**
	 * Removes the given Modifier (from the given source) from this Solver.
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
	 */
	public void removeModifier(Modifier<T> modifier, ScopeInstance source)
	{
		modifierList.removeFromListFor(Long.valueOf(modifier.getPriority()),
			new ModInfo<>(modifier, Objects.requireNonNull(source)));
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
	public void removeFromSource(ScopeInstance source)
	{
		List<Modifier<T>> removed = sourceList.removeListFor(Objects.requireNonNull(source));
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
	 * Process this Solver to provide the value after all Modifiers are processed (in
	 * priority order).
	 * 
	 * @param evalManager
	 *            The EvaluationManager used to support evaluation of the Modifiers in
	 *            this Solver
	 * @return The resulting value after all Modifier objects are processed
	 */
	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	public T process(EvaluationManager evalManager)
	{
		EvaluationManager assertedManager = evalManager.getWith(
			EvaluationManager.ASSERTED, Optional.of(formatManager));
		T result = defaultValue;
		for (Long priority : modifierList.getKeySet())
		{
			for (ModInfo<T> modInfo : modifierList.getListFor(priority))
			{
				EvaluationManager thisManager =
						assertedManager.getWith(EvaluationManager.INPUT, result);
				thisManager = thisManager.getWith(EvaluationManager.INSTANCE,
					modInfo.getInstance());
				result = modInfo.getModifier().process(thisManager);
			}
		}
		return result;
	}

	/**
	 * Provides a "debugging" view of the operations taking place in this Solver. This
	 * returns a List of ProcessStep objects that are an ordered list of the steps taken
	 * and the value after each step.
	 * 
	 * @param evalManager
	 *            The EvaluationManager used to support evaluation of the Modifiers in
	 *            this Solver
	 * @return A list of ProcessStep objects indicating the operations that take place in
	 *         this Solver when process() is called
	 */
	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	public List<ProcessStep<T>> diagnose(EvaluationManager evalManager)
	{
		EvaluationManager assertedManager = evalManager.getWith(
			EvaluationManager.ASSERTED, Optional.of(formatManager));
		List<ProcessStep<T>> steps = new ArrayList<ProcessStep<T>>();
		T stepResult = defaultValue;
		steps.add(new ProcessStep<T>(
			new SetModifier<>(formatManager, defaultValue),
			new DefaultValue(formatManager.getIdentifierType()), stepResult));
		if (!modifierList.isEmpty())
		{
			for (Long priority : modifierList.getKeySet())
			{
				for (ModInfo<T> modInfo : modifierList.getListFor(priority))
				{
					EvaluationManager thisManager =
							assertedManager.getWith(EvaluationManager.INPUT, stepResult);
					thisManager = thisManager.getWith(EvaluationManager.INSTANCE,
						modInfo.getInstance());
					stepResult = modInfo.getModifier().process(thisManager);
					@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
					ProcessStep<T> step = new ProcessStep<T>(modInfo.getModifier(),
						modInfo.getInstance(), stepResult);
					steps.add(step);
				}
			}
		}
		return steps;
	}

	/**
	 * Carries the Default Value information for display in diagnosis.
	 * 
	 * @param <T> The format (class) of object contained in this SetModifier
	 */
	private static final class SetModifier<T> implements Modifier<T>
	{
		/**
		 * The FormatManager that this SetModifier is representing.
		 */
		private final FormatManager<T> formatManager;
		
		/**
		 * The value that this SetModifier contains.
		 */
		private final T value;
		
		/**
		 * Constructs a new SetModifier with the given FormatManager and value.
		 * 
		 * @param formatManager
		 *            The FormatManager that this SetModifier is representing
		 * @param value
		 *            The value that this SetModifier contains
		 */
		public SetModifier(FormatManager<T> formatManager, T value)
		{
			this.formatManager = formatManager;
			this.value = value;
		}

		@Override
		public T process(EvaluationManager manager)
		{
			return value;
		}

		@Override
		public void getDependencies(DependencyManager fdm)
		{
		}

		@Override
		public long getPriority()
		{
			return 0;
		}

		@Override
		public FormatManager<T> getVariableFormat()
		{
			return formatManager;
		}

		@Override
		public String getIdentification()
		{
			return "DEFAULT";
		}

		@Override
		public String getInstructions()
		{
			return formatManager.unconvert(value);
		}
		
	}

	/**
	 * Carries the Default Value Source information for display in diagnosis.
	 */
	private final class DefaultValue implements Identified
	{
		/**
		 * The format name of the format that this DefaultValue is representing
		 */
		private final String formatName;
		
		private DefaultValue(String formatName)
		{
			this.formatName = formatName;
		}
		
		@Override
		public String getIdentification()
		{
			return "Default Value for " + formatName;
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
		private final ScopeInstance inst;

		private ModInfo(Modifier<IT> modifier, ScopeInstance source)
		{
			this.modifier = Objects.requireNonNull(modifier);
			this.inst = Objects.requireNonNull(source);
		}

		public Modifier<IT> getModifier()
		{
			return modifier;
		}

		public ScopeInstance getInstance()
		{
			return inst;
		}

		@Override
		public int hashCode()
		{
			return modifier.hashCode() ^ inst.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ModInfo)
			{
				ModInfo<?> other = (ModInfo<?>) obj;
				return modifier.equals(other.modifier) && inst.equals(other.inst);
			}
			return false;
		}

	}

	/**
	 * Creates a replacement Solver for this Solver, giving it independent modifier and
	 * source lists.
	 * 
	 * @return A replacement Solver for this Solver, with independent modifier and source
	 *         lists.
	 */
	public Solver<T> createReplacement()
	{
		Solver<T> replacement = new Solver<>(formatManager, defaultValue);
		replacement.modifierList.addAllLists(modifierList);
		replacement.sourceList.addAllLists(sourceList);
		return replacement;
	}
}
