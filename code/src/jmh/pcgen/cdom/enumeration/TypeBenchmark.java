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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import pcgen.core.Ability;

/**
 * Baseline for the {@code PObject.isType} / {@code Type.getConstant} hot path
 * that dominates PCGen campaign-load CPU (see the startup flame graph). Re-run
 * after optimizing that path to measure the delta.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class TypeBenchmark
{

	/** An Ability (a plain PObject subclass) preloaded with a handful of types. */
	private Ability ability;

	/** Pre-interned Type constants, as the optimized callers already hold. */
	private Type weapon;
	private Type melee;

	/** A small population of objects, mimicking resolveGroupReferences' object loop. */
	private List<Ability> population;

	/** A two-token group key, as stored in a manufacturer's typeReferences. */
	private String[] groupTokens;
	private Type[] internedGroupTokens;

	@Setup
	public void setUp()
	{
		weapon = Type.getConstant("Weapon");
		melee = Type.getConstant("Melee");
		ability = new Ability();
		ability.addToListFor(ListKey.TYPE, weapon);
		ability.addToListFor(ListKey.TYPE, melee);
		ability.addToListFor(ListKey.TYPE, Type.getConstant("Standard"));

		groupTokens = new String[]{"Weapon", "Melee"};
		internedGroupTokens = new Type[]{weapon, melee};
		population = new ArrayList<>();
		for (int i = 0; i < 50; i++)
		{
			Ability a = new Ability();
			a.addToListFor(ListKey.TYPE, weapon);
			// Half match both tokens, half fail the second — exercises both branches.
			a.addToListFor(ListKey.TYPE, (i % 2 == 0) ? melee : Type.getConstant("Ranged"));
			population.add(a);
		}
	}

	/** Interned-name lookup: the computeIfAbsent + CaseInsensitiveString hash/equals cost. */
	@Benchmark
	public Type getConstant_hit()
	{
		return Type.getConstant("Weapon");
	}

	/** Single token: uppercase + tokenize + one getConstant + containsInList. */
	@Benchmark
	public boolean isType_singleToken()
	{
		return ability.isType("Weapon");
	}

	/** Multi token: two getConstant calls, both matching. */
	@Benchmark
	public boolean isType_multiToken()
	{
		return ability.isType("Weapon.Melee");
	}

	/** Miss: interns the name, then fails containsInList on the first token. */
	@Benchmark
	public boolean isType_miss()
	{
		return ability.isType("Nonexistent");
	}

	/** Optimized fast path: caller already holds the interned Type (single). */
	@Benchmark
	public boolean isTypeFast_singleToken()
	{
		return ability.isType(weapon);
	}

	/** Optimized fast path over two held Types (compare against isType_multiToken). */
	@Benchmark
	public boolean isTypeFast_multiToken()
	{
		return ability.isType(weapon) && ability.isType(melee);
	}

	/**
	 * Old resolveGroupReferences inner loop: re-intern each String token per
	 * object via isType(String).
	 */
	@Benchmark
	public int groupResolve_stringPath()
	{
		int matches = 0;
		for (Ability a : population)
		{
			boolean ok = true;
			for (String token : groupTokens)
			{
				if (!a.isType(token))
				{
					ok = false;
					break;
				}
			}
			if (ok)
			{
				matches++;
			}
		}
		return matches;
	}

	/**
	 * New resolveGroupReferences inner loop: group tokens interned once, then
	 * isType(Type) per object.
	 */
	@Benchmark
	public int groupResolve_internedTypePath()
	{
		int matches = 0;
		for (Ability a : population)
		{
			boolean ok = true;
			for (Type token : internedGroupTokens)
			{
				if (!a.isType(token))
				{
					ok = false;
					break;
				}
			}
			if (ok)
			{
				matches++;
			}
		}
		return matches;
	}

	/**
	 * Combined path in one shot so JMH's per-benchmark fixed cost doesn't hide the
	 * relative differences; results consumed via Blackhole to defeat DCE.
	 */
	@Benchmark
	public void mixed(Blackhole bh)
	{
		bh.consume(ability.isType("Weapon"));
		bh.consume(ability.isType("Weapon.Melee"));
		bh.consume(ability.isType("Nonexistent"));
	}
}
