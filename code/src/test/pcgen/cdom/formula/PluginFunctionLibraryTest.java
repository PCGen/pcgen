/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

import org.junit.jupiter.api.Test;

/**
 * Pins the singleton + plugin-load contract of {@link PluginFunctionLibrary}.
 *
 * Single-threaded behavioural coverage; thread-safety of the holder idiom used
 * by {@link PluginFunctionLibrary#getInstance()} is a JVM-spec guarantee
 * (class initialisation is single-threaded and publishes a happens-before edge),
 * not testable from a single-threaded JUnit run.
 */
public class PluginFunctionLibraryTest
{

	@Test
	public void getInstanceReturnsSameSingleton()
	{
		PluginFunctionLibrary first = PluginFunctionLibrary.getInstance();
		assertNotNull(first);
		assertSame(first, PluginFunctionLibrary.getInstance());
	}

	@Test
	public void pluginClassesIsFormulaFunction()
	{
		assertArrayEquals(new Class<?>[]{FormulaFunction.class},
			PluginFunctionLibrary.getInstance().getPluginClasses());
	}

	@Test
	public void loadPluginRegistersFormulaFunction() throws Exception
	{
		PluginFunctionLibrary lib = PluginFunctionLibrary.getInstance();
		int before = lib.getFunctions().size();
		lib.loadPlugin(TestFunctionAlpha.class);
		List<FormulaFunction> after = lib.getFunctions();
		assertEquals(before + 1, after.size());
		assertTrue(after.stream().anyMatch(TestFunctionAlpha.class::isInstance));
	}

	@Test
	public void loadPluginIgnoresNonFormulaFunctionClass() throws Exception
	{
		PluginFunctionLibrary lib = PluginFunctionLibrary.getInstance();
		int before = lib.getFunctions().size();
		lib.loadPlugin(NotAFormulaFunction.class);
		assertEquals(before, lib.getFunctions().size());
	}

	@Test
	public void loadPluginRejectsDuplicateByNameButDoesNotThrow() throws Exception
	{
		PluginFunctionLibrary lib = PluginFunctionLibrary.getInstance();
		lib.loadPlugin(TestFunctionBeta.class);
		int afterFirst = lib.getFunctions().size();
		// Same getFunctionName(), different class — second registration is dropped (logged, not thrown).
		lib.loadPlugin(TestFunctionBetaDuplicate.class);
		assertEquals(afterFirst, lib.getFunctions().size());
	}

	@Test
	public void loadPluginRejectsNonInstantiableClass()
	{
		PluginFunctionLibrary lib = PluginFunctionLibrary.getInstance();
		// PrivateCtorFunction has no accessible no-arg constructor; reflection must fail
		// with a checked ReflectiveOperationException (we don't depend on the exact subtype).
		assertThrows(ReflectiveOperationException.class,
			() -> lib.loadPlugin(PrivateCtorFunction.class));
	}

	@Test
	public void getFunctionsReturnsUnmodifiableView()
	{
		List<FormulaFunction> functions = PluginFunctionLibrary.getInstance().getFunctions();
		assertThrows(UnsupportedOperationException.class,
			() -> functions.add(new TestFunctionAlpha()));
	}

	// ── Test fixtures ────────────────────────────────────────────────────────

	/**
	 * Stub FormulaFunction; concrete subclasses just override {@link #getFunctionName()}.
	 * All other methods return harmless values — none of them are exercised here.
	 */
	private abstract static class StubFunction implements FormulaFunction
	{
		@Override
		public Boolean isStatic(StaticVisitor visitor, Node[] args)
		{
			return Boolean.TRUE;
		}

		@Override
		public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args, FormulaSemantics semantics)
		{
			return null;
		}

		@Override
		public Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager manager)
		{
			return Integer.valueOf(0);
		}

		@Override
		public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
			DependencyManager manager, Node[] args)
		{
			return Optional.empty();
		}
	}

	public static class TestFunctionAlpha extends StubFunction
	{
		@Override
		public String getFunctionName()
		{
			return "PluginFunctionLibraryTest_Alpha";
		}
	}

	public static class TestFunctionBeta extends StubFunction
	{
		@Override
		public String getFunctionName()
		{
			return "PluginFunctionLibraryTest_Beta";
		}
	}

	/** Same getFunctionName() as TestFunctionBeta — must be rejected as duplicate. */
	public static class TestFunctionBetaDuplicate extends StubFunction
	{
		@Override
		public String getFunctionName()
		{
			return "PluginFunctionLibraryTest_Beta";
		}
	}

	/** Not a FormulaFunction — loadPlugin must silently ignore. */
	public static class NotAFormulaFunction
	{
	}

	/** No accessible no-arg constructor — loadPlugin must throw a ReflectiveOperationException. */
	public static class PrivateCtorFunction extends StubFunction
	{
		private PrivateCtorFunction()
		{
		}

		@Override
		public String getFunctionName()
		{
			return "PluginFunctionLibraryTest_Private";
		}
	}
}
