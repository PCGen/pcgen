package pcgen.base.formula.inst;

import java.util.List;

import junit.framework.TestCase;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.solver.IndividualSetup;
import pcgen.base.solver.SplitFormulaSetup;

public class ComplexNEPFormulaTest extends TestCase
{

	public void testConstructor()
	{
		try
		{
			new ComplexNEPFormula(null);
			fail("Expected null formula text to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula("3+*5");
			fail("Expected bad formula text to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testToString()
	{
		assertEquals("3+5", new ComplexNEPFormula("3+5").toString());
		assertEquals("3*5", new ComplexNEPFormula("3*5").toString());
		assertEquals("(3+5)*7", new ComplexNEPFormula("(3+5)*7").toString());
		assertEquals("a-b", new ComplexNEPFormula("a-b").toString());
		assertEquals("if(a>=b,5,9)",
			new ComplexNEPFormula("if(a>=b,5,9)").toString());
		assertEquals("if(a==b,5,-9)",
			new ComplexNEPFormula("if(a==b,5,-9)").toString());
		assertEquals("if(a||b,\"A\",\"B\")", new ComplexNEPFormula(
			"if(a||b,\"A\",\"B\")").toString());
		assertEquals("value()", new ComplexNEPFormula("value()").toString());
		assertEquals("3^5", new ComplexNEPFormula("3^5").toString());
		assertEquals("process[THIS]",
			new ComplexNEPFormula("process[THIS]").toString());
	}

	public void testIsValid()
	{
		SplitFormulaSetup setup = new SplitFormulaSetup();
		setup.loadBuiltIns();
		LegalScopeLibrary scopeLib = setup.getLegalScopeLibrary();
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		scopeLib.registerScope(globalScope);
		IndividualSetup indSetup = new IndividualSetup(setup, "Global");

		FormulaManager fm = indSetup.getFormulaManager();
		NumberManager numberMgr = new NumberManager();
		BooleanManager booleanMgr = new BooleanManager();
		StringManager stringMgr = new StringManager();

		FormulaSemantics fs;
		try
		{
			new ComplexNEPFormula("3+5").isValid(null, globalScope, numberMgr,
				null);
			fail("Expected null FormulaManager to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula("3+5").isValid(fm, null, numberMgr, null);
			fail("Expected null LegalScope to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new ComplexNEPFormula("3+5").isValid(fm, globalScope, null, null);
			fail("Expected null FormatManager to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}

		fs =
				new ComplexNEPFormula("3+5").isValid(fm, globalScope,
					numberMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());
		fs =
				new ComplexNEPFormula("3*5").isValid(fm, globalScope,
					numberMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());
		fs =
				new ComplexNEPFormula("(3+5)*7").isValid(fm, globalScope,
					numberMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());

		setup.getVariableLibrary().assertLegalVariableID("a", globalScope,
			numberMgr);
		setup.getVariableLibrary().assertLegalVariableID("b", globalScope,
			numberMgr);
		fs =
				new ComplexNEPFormula("a-b").isValid(fm, globalScope,
					numberMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());
		fs =
				new ComplexNEPFormula("if(a>=b,5,9)").isValid(fm, globalScope,
					numberMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());
		fs =
				new ComplexNEPFormula("if(a==b,5,-9)").isValid(fm, globalScope,
					numberMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());

		setup.getVariableLibrary().assertLegalVariableID("c", globalScope,
			booleanMgr);
		setup.getVariableLibrary().assertLegalVariableID("d", globalScope,
			booleanMgr);
		fs =
				new ComplexNEPFormula("if(c||d,\"A\",\"B\")").isValid(fm,
					globalScope, stringMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());

		setup.getFunctionLibrary().addFunction(new Function()
		{

			@Override
			public String getFunctionName()
			{
				return "value";
			}

			@Override
			public Boolean isStatic(StaticVisitor visitor, Node[] args)
			{
				return false;
			}

			@Override
			public void allowArgs(SemanticsVisitor visitor, Node[] args,
				FormulaSemantics semantics)
			{
				if (args.length == 0)
				{
					semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT,
						Number.class);
				}
			}

			@Override
			public Object evaluate(EvaluateVisitor visitor, Node[] args,
				Class<?> assertedFormat)
			{
				return 4;
			}

			@Override
			public void getDependencies(DependencyVisitor visitor,
				DependencyManager manager, Node[] args)
			{
			}
		});
		fs =
				new ComplexNEPFormula("value()").isValid(fm, globalScope,
					numberMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());
		fs =
				new ComplexNEPFormula("3^5").isValid(fm, globalScope,
					numberMgr, null);
		assertEquals(true, fs.getInfo(FormulaSemanticsUtilities.SEM_VALID)
			.isValid());
	}

	public void testGetDependencies()
	{
		SplitFormulaSetup setup = new SplitFormulaSetup();
		setup.loadBuiltIns();
		LegalScopeLibrary scopeLib = setup.getLegalScopeLibrary();
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		scopeLib.registerScope(globalScope);
		IndividualSetup indSetup = new IndividualSetup(setup, "Global");

		ScopeInformation scopeInfo = indSetup.getScopeInfo();
		ScopeInstance globalInst = indSetup.getGlobalScopeInst();
		DependencyManager depManager = setupDM(indSetup);

		NumberManager numberMgr = new NumberManager();
		BooleanManager booleanMgr = new BooleanManager();

		new ComplexNEPFormula("3+5").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("3*5").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("(3+5)*7").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		setup.getVariableLibrary().assertLegalVariableID("a", globalScope,
			numberMgr);
		setup.getVariableLibrary().assertLegalVariableID("b", globalScope,
			numberMgr);

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("a-b").getDependencies(depManager);
		List<VariableID<?>> variables = depManager.getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"a")));
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"b")));
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("if(a>=b,5,9)").getDependencies(depManager);
		variables = depManager.getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"a")));
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"b")));
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("if(a==b,5,-9)").getDependencies(depManager);
		variables = depManager.getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"a")));
		assertTrue(variables.contains(new VariableID<>(globalInst, numberMgr,
			"b")));
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		setup.getVariableLibrary().assertLegalVariableID("c", globalScope,
			booleanMgr);
		setup.getVariableLibrary().assertLegalVariableID("d", globalScope,
			booleanMgr);

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("if(c||d,\"A\",\"B\")")
			.getDependencies(depManager);
		variables = depManager.getVariables();
		assertEquals(2, variables.size());
		assertTrue(variables.contains(new VariableID<>(globalInst, booleanMgr,
			"c")));
		assertTrue(variables.contains(new VariableID<>(globalInst, booleanMgr,
			"d")));
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		setup.getFunctionLibrary().addFunction(new Function()
		{

			@Override
			public String getFunctionName()
			{
				return "value";
			}

			@Override
			public Boolean isStatic(StaticVisitor visitor, Node[] args)
			{
				return false;
			}

			@Override
			public void allowArgs(SemanticsVisitor visitor, Node[] args,
				FormulaSemantics semantics)
			{
				if (args.length == 0)
				{
					semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT,
						Number.class);
				}
			}

			@Override
			public Object evaluate(EvaluateVisitor visitor, Node[] args,
				Class<?> assertedFormat)
			{
				return 4;
			}

			@Override
			public void getDependencies(DependencyVisitor visitor,
				DependencyManager manager, Node[] args)
			{
			}
		});

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("value()").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());

		depManager = setupDM(indSetup);
		new ComplexNEPFormula("3^5").getDependencies(depManager);
		assertTrue(depManager.getVariables().isEmpty());
		assertEquals(-1, depManager.peek(ArgumentDependencyManager.KEY)
			.getMaximumArgument());
	}

	private DependencyManager setupDM(IndividualSetup indSetup)
	{
		DependencyManager dm =
				DependencyManager.generate(indSetup.getFormulaManager(),
					indSetup.getGlobalScopeInst(), null);
		dm.set(ArgumentDependencyManager.KEY, new ArgumentDependencyManager());
		return dm;
	}

	public void testResolve()
	{
		SplitFormulaSetup setup = new SplitFormulaSetup();
		setup.loadBuiltIns();
		LegalScopeLibrary scopeLib = setup.getLegalScopeLibrary();
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		scopeLib.registerScope(globalScope);
		IndividualSetup indSetup = new IndividualSetup(setup, "Global");

		ScopeInformation scopeInfo = indSetup.getScopeInfo();
		ScopeInstance globalInst = indSetup.getGlobalScopeInst();
		try
		{
			new ComplexNEPFormula("3+5").resolve(null, null, null);
			fail("Expected null FormulaManager to fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}

		NumberManager numberMgr = new NumberManager();
		BooleanManager booleanMgr = new BooleanManager();
		StringManager stringMgr = new StringManager();

		assertEquals(8,
			new ComplexNEPFormula("3+5").resolve(scopeInfo, null, null));
		assertEquals(15,
			new ComplexNEPFormula("3*5").resolve(scopeInfo, null, null));
		assertEquals(56,
			new ComplexNEPFormula("(3+5)*7").resolve(scopeInfo, null, null));

		setup.getVariableLibrary().assertLegalVariableID("a", globalScope,
			numberMgr);
		setup.getVariableLibrary().assertLegalVariableID("b", globalScope,
			numberMgr);

		indSetup.getVariableStore().put(
			new VariableID<>(globalInst, numberMgr, "a"), 4);
		indSetup.getVariableStore().put(
			new VariableID<>(globalInst, numberMgr, "b"), 1);
		assertEquals(3,
			new ComplexNEPFormula("a-b").resolve(scopeInfo, null, null));

		assertEquals(5, new ComplexNEPFormula("if(a>=b,5,9)").resolve(
			scopeInfo, null, null));

		assertEquals(-9, new ComplexNEPFormula("if(a==b,5,-9)").resolve(
			scopeInfo, null, null));

		setup.getVariableLibrary().assertLegalVariableID("c", globalScope,
			booleanMgr);
		setup.getVariableLibrary().assertLegalVariableID("d", globalScope,
			booleanMgr);
		indSetup.getVariableStore().put(
			new VariableID<>(globalInst, booleanMgr, "c"), false);
		indSetup.getVariableStore().put(
			new VariableID<>(globalInst, booleanMgr, "d"), true);

		assertEquals("A",
			new ComplexNEPFormula("if(c||d,\"A\",\"B\")").resolve(scopeInfo,
				null, null));

		setup.getFunctionLibrary().addFunction(new Function()
		{

			@Override
			public String getFunctionName()
			{
				return "value";
			}

			@Override
			public Boolean isStatic(StaticVisitor visitor, Node[] args)
			{
				return false;
			}

			@Override
			public void allowArgs(SemanticsVisitor visitor, Node[] args,
				FormulaSemantics semantics)
			{
				if (args.length == 0)
				{
					semantics.setInfo(FormulaSemanticsUtilities.SEM_FORMAT,
						Number.class);
				}
			}

			@Override
			public Object evaluate(EvaluateVisitor visitor, Node[] args,
				Class<?> assertedFormat)
			{
				return 4;
			}

			@Override
			public void getDependencies(DependencyVisitor visitor,
				DependencyManager manager, Node[] args)
			{
			}
		});

		assertEquals(4,
			new ComplexNEPFormula("value()").resolve(scopeInfo, null, null));

		assertEquals(243.0,
			new ComplexNEPFormula("3^5").resolve(scopeInfo, null, null));
	}
}
