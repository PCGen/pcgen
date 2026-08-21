/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.enumeration;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;

/**
 * Measures the cost of sorting a CDOMObject list by name. Compares the shared-
 * Collator {@link CDOMObject#P_OBJECT_NAME_COMP} against an equivalent comparator
 * that constructs a Collator per comparison (the prior behaviour), so the delta
 * from hoisting Collator.getInstance() is visible within a single run.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class SortBenchmark
{

	@Param({"200"})
	private int size;

	/** Master unsorted list, rebuilt fresh into `work` before each invocation. */
	private List<Ability> master;
	private List<Ability> work;

	/** Prior behaviour: a fresh Collator per comparison (sort-key -> display-name). */
	private static final Comparator<CDOMObject> PER_COMPARE_COLLATOR = (o1, o2) -> {
		Collator collator = Collator.getInstance();
		String key1 = o1.get(StringKey.SORT_KEY);
		if (key1 == null)
		{
			key1 = o1.getDisplayName();
		}
		String key2 = o2.get(StringKey.SORT_KEY);
		if (key2 == null)
		{
			key2 = o2.getDisplayName();
		}
		if (!key1.equals(key2))
		{
			return collator.compare(key1, key2);
		}
		if (!o1.getDisplayName().equals(o2.getDisplayName()))
		{
			return collator.compare(o1.getDisplayName(), o2.getDisplayName());
		}
		return collator.compare(o1.getKeyName(), o2.getKeyName());
	};

	@Setup(Level.Trial)
	public void buildMaster()
	{
		master = new ArrayList<>(size);
		// Deterministic pseudo-shuffle so the input isn't pre-sorted: multiply the
		// index by a value coprime to `size` and wrap. Distinct display names, so
		// the collator does real work (no equals short-circuit).
		for (int i = 0; i < size; i++)
		{
			int scrambled = (int) (((long) i * 7919) % size);
			Ability a = new Ability();
			a.setName("Ability_" + String.format("%05d", scrambled));
			master.add(a);
		}
	}

	@Setup(Level.Invocation)
	public void freshCopy()
	{
		work = new ArrayList<>(master);
	}

	@Benchmark
	public List<Ability> sharedCollator()
	{
		work.sort(CDOMObject.P_OBJECT_NAME_COMP);
		return work;
	}

	@Benchmark
	public List<Ability> perCompareCollator()
	{
		work.sort(PER_COMPARE_COLLATOR);
		return work;
	}
}
