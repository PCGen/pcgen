/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import org.junit.Test;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.DynamicDependency;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.TrainingStrategy;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.ScopeInstanceFactory;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.solver.testsupport.AbstractModifier;
import pcgen.base.solver.testsupport.AbstractSolverManagerTest;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.TypedKey;

public class DynamicSolverManagerTest extends AbstractSolverManagerTest
{
	private ManagerFactory managerFactory = new ManagerFactory()
	{
	};
	private DynamicSolverManager manager;
	private LimbManager limbManager;
	public static final TypedKey<ScopeInstanceFactory> SIFACTORY = new TypedKey<>();

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		manager = new DynamicSolverManager(getFormulaManager(), managerFactory,
			getSolverFactory(), getVariableStore());
		limbManager = new LimbManager();
		getSolverFactory().addSolverFormat(Limb.class, new Modifier()
		{

			@Override
			public Object process(EvaluationManager manager)
			{
				return limbManager.convert("Head");
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
			public FormatManager getVariableFormat()
			{
				return limbManager;
			}

			@Override
			public String getIdentification()
			{
				return "SET";
			}

			@Override
			public String getInstructions()
			{
				return "<null>";
			}
		});
	}

	@Test
	public void testIllegalConstruction()
	{
		try
		{
			new DynamicSolverManager(null, managerFactory, getSolverFactory(),
				getVariableStore());
			fail("No nulls in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		FormulaManager formulaManager = getFormulaManager();
		try
		{
			new DynamicSolverManager(formulaManager, null, getSolverFactory(),
				getVariableStore());
			fail("No nulls in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new DynamicSolverManager(formulaManager, managerFactory, null,
				getVariableStore());
			fail("No nulls in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new DynamicSolverManager(formulaManager, managerFactory, getSolverFactory(),
				null);
			fail("No nulls in constructor");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Override
	protected SolverManager getManager()
	{
		return manager;
	}

	@Test
	public void testDynamic()
	{
		ScopeInstance source = getGlobalScopeInst();
		getFunctionLibrary().addFunction(new Dynamic());
		LegalScope globalScope = getGlobalScope();

		SimpleLegalScope limbScope = new SimpleLegalScope(globalScope, "LIMB");
		getScopeLibrary().registerScope(limbScope);
		getVarLibrary().assertLegalVariableID("active", globalScope, limbManager);
		getVarLibrary().assertLegalVariableID("quantity", limbScope, numberManager);
		getVarLibrary().assertLegalVariableID("result", globalScope, numberManager);

		ComplexNEPFormula<Number> dynamicformula =
				new ComplexNEPFormula<Number>("dynamic(active, quantity)");
		Modifier<Number> dynamicMod = AbstractModifier.add(dynamicformula, 100);

		VariableID<Limb> active = (VariableID<Limb>) getVarLibrary()
			.getVariableID(getGlobalScopeInst(), "Active");
		VariableID<Number> result = (VariableID<Number>) getVarLibrary()
			.getVariableID(getGlobalScopeInst(), "Result");

		Limb hands = limbManager.convert("Hands");
		ScopeInstance handsInst = getScopeInstance("LIMB", hands);
		Limb fingers = limbManager.convert("Fingers");
		ScopeInstance fingersInst = getScopeInstance("LIMB", fingers);

		VariableID<Number> handsID =
				(VariableID<Number>) getVarLibrary().getVariableID(handsInst, "Quantity");
		VariableID<Number> fingersID = (VariableID<Number>) getVarLibrary()
			.getVariableID(fingersInst, "Quantity");

		AbstractModifier<Number> two = AbstractModifier.setNumber(2, 5);
		AbstractModifier<Number> ten = AbstractModifier.setNumber(10, 5);
		AbstractModifier<Limb> useHands = AbstractModifier.setObject(limbManager, hands, 3);
		AbstractModifier<Limb> useFingers = AbstractModifier.setObject(limbManager, fingers, 5);

		getManager().addModifier(handsID, two, source);
		getManager().addModifier(fingersID, ten, source);
		getManager().addModifier(active, useHands, source);

		WriteableVariableStore store = getVariableStore();

		assertEquals(2, store.get(handsID));
		assertEquals(10, store.get(fingersID));

		getManager().addModifier(result, dynamicMod, source);

		assertEquals(2, store.get(result));

		getManager().addModifier(active, useFingers, source);

		assertEquals(10, store.get(result));

		getManager().removeModifier(result, dynamicMod, source);

		assertEquals(0, store.get(result));
	}

	@Test
	public void testTrivial()
	{
		getVariableLibrary().assertLegalVariableID("Limbs", getGlobalScope(),
			numberManager);
		VariableID<Number> limbs = (VariableID<Number>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), "Limbs");
		manager.solveChildren(limbs);
	}

	public class LimbManager implements FormatManager<Limb>
	{

		private CaseInsensitiveMap<Limb> set = new CaseInsensitiveMap<Limb>();

		@Override
		public Limb convert(String inputStr)
		{
			Limb current = set.get(inputStr);
			if (current == null)
			{
				current = new Limb(inputStr);
				set.put(inputStr, current);
			}
			return current;
		}

		@Override
		public Indirect<Limb> convertIndirect(String inputStr)
		{
			return new BasicIndirect<>(this, convert(inputStr));
		}

		@Override
		public String unconvert(Limb obj)
		{
			return obj.getKeyName();
		}

		@Override
		public Class<Limb> getManagedClass()
		{
			return Limb.class;
		}

		@Override
		public String getIdentifierType()
		{
			return "LIMB";
		}

		@Override
		public FormatManager<?> getComponentManager()
		{
			return null;
		}

		@Override
		public boolean isDirect()
		{
			return false;
		}

	}

	private final class Limb implements VarScoped
	{
		private final String name;

		private Limb(String name)
		{
			this.name = name;
		}

		@Override
		public String getKeyName()
		{
			return name;
		}

		@Override
		public String getLocalScopeName()
		{
			return "LIMB";
		}

		@Override
		public VarScoped getVariableParent()
		{
			return null;
		}

		@Override
		public int hashCode()
		{
			return name.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			return (o instanceof Limb) && ((Limb) o).name.equals(name);
		}
	}

	public class Dynamic implements Function
	{

		@Override
		public String getFunctionName()
		{
			return "Dynamic";
		}

		@Override
		public Boolean isStatic(StaticVisitor visitor, Node[] args)
		{
			return false;
		}

		@Override
		public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
			FormulaSemantics semantics)
		{
			int argCount = args.length;
			if (argCount != 2)
			{
				return null;
			}

			//If True node
			@SuppressWarnings("PMD.PrematureDeclaration")
			FormatManager<?> tFormat =
					(FormatManager<?>) args[1].jjtAccept(visitor, semantics);
			if (!semantics.isValid())
			{
				return null;
			}
			return tFormat;
		}

		@Override
		public Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager em)
		{
			VarScoped vs = (VarScoped) args[0].jjtAccept(visitor, em);
			FormulaManager fManager = em.get(EvaluationManager.FMANAGER);
			ScopeInstanceFactory siFactory = fManager.getScopeInstanceFactory();
			ScopeInstance scopeInst = siFactory.get("LIMB", vs);
			//Rest of Equation
			return args[1].jjtAccept(visitor,
				em.getWith(EvaluationManager.INSTANCE, scopeInst));
		}

		@Override
		public FormatManager<?> getDependencies(DependencyVisitor visitor, DependencyManager dm,
			Node[] args)
		{
			String varName = ((SimpleNode) args[0]).getText();
			String name = ((SimpleNode) args[1]).getText();
			TrainingStrategy ts = new TrainingStrategy();
			DependencyManager trainer = dm.getWith(DependencyManager.VARSTRATEGY, ts);
			visitor.visitVariable(varName, trainer);
			DynamicDependency dd = new DynamicDependency(ts.getControlVar(), "LIMB");
			DependencyManager dynamic = dm.getWith(DependencyManager.VARSTRATEGY, dd);
			FormatManager<?> returnFormat = visitor.visitVariable(name, dynamic);
			dm.get(DependencyManager.DYNAMIC).addDependency(dd);
			return returnFormat;
		}
	}

	public void testAnother()
	{
		ScopeInstance source = getGlobalScopeInst();
		LegalScope globalScope = getGlobalScope();

		SimpleLegalScope limbScope = new SimpleLegalScope(globalScope, "LIMB");
		getScopeLibrary().registerScope(limbScope);

		getVarLibrary().assertLegalVariableID("LocalVar", limbScope, numberManager);
		getVarLibrary().assertLegalVariableID("ResultVar", globalScope, numberManager);
		getVarLibrary().assertLegalVariableID("EquipVar", globalScope, limbManager);

		WriteableVariableStore store = getVariableStore();

		VariableID<Limb> activeID = (VariableID<Limb>) getVarLibrary()
			.getVariableID(getGlobalScopeInst(), "EquipVar");
		VariableID<Number> resultID = (VariableID<Number>) getVarLibrary()
			.getVariableID(getGlobalScopeInst(), "ResultVar");

		Limb equip = limbManager.convert("EquipKey");
		ScopeInstance equipInst = getScopeInstance("LIMB", equip);
		Limb equipalt  = limbManager.convert("EquipAlt");
		ScopeInstance altInst = getScopeInstance("LIMB", equipalt);

		VariableID<Number> equipID =
				(VariableID<Number>) getVarLibrary().getVariableID(equipInst, "LocalVar");
		VariableID<Number> altID =
				(VariableID<Number>) getVarLibrary().getVariableID(altInst, "LocalVar");

		AbstractModifier<Number> two = AbstractModifier.setNumber(2, 5);
		AbstractModifier<Number> three = AbstractModifier.setNumber(3, 10);
		AbstractModifier<Number> four = AbstractModifier.setNumber(4, 15);
		AbstractModifier<Limb> useEquip = AbstractModifier.setObject(limbManager, equip, 3);
		AbstractModifier<Limb> useAlt = AbstractModifier.setObject(limbManager, equipalt, 5);

		manager.addModifier(equipID, two, equipInst);
		manager.addModifier(altID, three, altInst);
		assertEquals(2, store.get(equipID));
		assertEquals(3, store.get(altID));
		//assertEquals(0, store.get(resultID));

		manager.addModifier(activeID, useEquip, source);

		getFunctionLibrary().addFunction(new Dynamic());
		ComplexNEPFormula<Number> dynamicformula =
				new ComplexNEPFormula<Number>("dynamic(equipVar, localVar)");
		Modifier<Number> dynamicMod = AbstractModifier.add(dynamicformula, 100);

		manager.addModifier(resultID, dynamicMod, source);
		assertEquals(2, store.get(resultID));

		manager.addModifier(activeID, useAlt, source);
		assertEquals(3, store.get(resultID));

		manager.addModifier(altID, four, altInst);
		assertEquals(4, store.get(resultID));
	}
}
