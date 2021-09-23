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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.DynamicDependency;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.TrainingStrategy;
import pcgen.base.formula.base.VarIDResolver;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.solver.testsupport.AbstractModifier;
import pcgen.base.solver.testsupport.AbstractSolverManagerTest;
import pcgen.base.testsupport.SimpleVarScoped;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

public class DynamicSolverManagerTest extends AbstractSolverManagerTest
{
	private SolverSystem manager;

	@BeforeEach
	@Override
	protected void setUp()
	{
		super.setUp();
		manager = SolverUtilities.buildDynamicSolverSystem(getVariableLibrary(),
			getManagerFactory(), getValueStore(), getVariableStore());
	}
	
	@AfterEach
	@Override
	protected void tearDown()
	{
		super.tearDown();
		manager = null;
	}

	@Override
	protected SolverSystem getManager()
	{
		return manager;
	}

	@Override
	protected FunctionLibrary functionSetup(WriteableFunctionLibrary wfl)
	{
		wfl.addFunction(new Dynamic());
		return wfl;
	}

	@Test
	public void testDynamic()
	{
		ScopeInstance source = getGlobalScopeInst();

		LimbManager limbManager = new LimbManager();
		getValueStore().addSolverFormat(limbManager,
			() -> limbManager.convert("Head"));

		getScopeManager().registerScope("Global", "Global.LIMB");
		assertLegalVariable("active", "Global", limbManager);
		assertLegalVariable("quantity", "Global.LIMB",
			FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("result", "Global", FormatUtilities.NUMBER_MANAGER);

		ComplexNEPFormula<Number> dynamicformula =
				new ComplexNEPFormula<Number>("dynamic(active, quantity)", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> dynamicMod = AbstractModifier.add(dynamicformula, 100);

		@SuppressWarnings("unchecked")
		VariableID<Limb> active = (VariableID<Limb>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), "Active");
		@SuppressWarnings("unchecked")
		VariableID<Number> result = (VariableID<Number>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), "Result");

		Limb hands = limbManager.convert("Hands");
		ScopeInstance handsInst = getScopeInstance("Global.LIMB", hands);
		Limb fingers = limbManager.convert("Fingers");
		ScopeInstance fingersInst = getScopeInstance("Global.LIMB", fingers);

		@SuppressWarnings("unchecked")
		VariableID<Number> handsID =
				(VariableID<Number>) getVariableLibrary().getVariableID(handsInst, "Quantity");
		@SuppressWarnings("unchecked")
		VariableID<Number> fingersID = (VariableID<Number>) getVariableLibrary()
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

		getManager().addModifier(result, dynamicMod, handsInst);

		assertEquals(2, store.get(result));

		getManager().addModifier(active, useFingers, source);

		assertEquals(10, store.get(result));

		getManager().removeModifier(result, dynamicMod, handsInst);

		assertEquals(0, store.get(result));
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
				current = new Limb(inputStr, getGlobalVarScoped());
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
		public Optional<FormatManager<?>> getComponentManager()
		{
			return Optional.empty();
		}

		@Override
		public boolean isDirect()
		{
			return false;
		}

	}

	private final class Limb extends SimpleVarScoped
	{
		private Limb(String name, VarScoped parent)
		{
			super(name, parent, "Global.LIMB");
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

	public class Dynamic implements FormulaFunction
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

			return (FormatManager<?>) args[1].jjtAccept(visitor, semantics);
		}

		@Override
		public Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager em)
		{
			VarScoped vs = (VarScoped) args[0].jjtAccept(visitor, em);
			ScopeInstanceFactory siFactory = em.get(EvaluationManager.SIFACTORY);
			ScopeInstance scopeInst = siFactory.get("Global.LIMB", vs);
			//Rest of Equation
			return args[1].jjtAccept(visitor,
				em.getWith(EvaluationManager.INSTANCE, scopeInst));
		}

		@Override
		public Optional<FormatManager<?>> getDependencies(
			DependencyVisitor visitor, DependencyManager dm, Node[] args)
		{
			String varName = ((SimpleNode) args[0]).getText();
			String name = ((SimpleNode) args[1]).getText();
			TrainingStrategy ts = new TrainingStrategy();
			DependencyManager trainer = dm.getWith(DependencyManager.VARSTRATEGY, Optional.of(ts));
			visitor.visitVariable(varName, trainer);

			DynamicDependency dd =
					new DynamicDependency(buildResolver(),
						ts.getControlVar(), "Global.LIMB");
			DependencyManager dynamic = dm.getWith(DependencyManager.VARSTRATEGY, Optional.of(dd));
			FormatManager<?> returnFormat = visitor.visitVariable(name, dynamic);
			dm.get(DependencyManager.DYNAMIC).addDependency(dd);
			return Optional.of(returnFormat);
		}
	}

	private VarIDResolver buildResolver()
	{
		return new VarIDResolver()
		{
			@Override
			public VariableID<?> resolve(String sourceScopeName,
				VarScoped sourceObject, String sourceVarName)
			{
				ScopeInstance scopeInst =
						getScopeInstance(sourceScopeName, sourceObject);
				return getVariableLibrary().getVariableID(scopeInst, sourceVarName);
			}
		};
	}
	
	@Test
	public void testAnother()
	{
		LimbManager limbManager = new LimbManager();
		getValueStore().addSolverFormat(limbManager,
			() -> limbManager.convert("Head"));

		ScopeInstance source = getGlobalScopeInst();

		getScopeManager().registerScope("Global", "Global.LIMB");

		assertLegalVariable("LocalVar", "Global.LIMB", FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("ResultVar", "Global", FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("EquipVar", "Global", limbManager);

		WriteableVariableStore store = getVariableStore();

		@SuppressWarnings("unchecked")
		VariableID<Limb> activeID = (VariableID<Limb>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), "EquipVar");
		@SuppressWarnings("unchecked")
		VariableID<Number> resultID = (VariableID<Number>) getVariableLibrary()
			.getVariableID(getGlobalScopeInst(), "ResultVar");

		Limb equip = limbManager.convert("EquipKey");
		ScopeInstance equipInst = getScopeInstance("Global.LIMB", equip);
		Limb equipalt  = limbManager.convert("EquipAlt");
		ScopeInstance altInst = getScopeInstance("Global.LIMB", equipalt);

		@SuppressWarnings("unchecked")
		VariableID<Number> equipID =
				(VariableID<Number>) getVariableLibrary().getVariableID(equipInst, "LocalVar");
		@SuppressWarnings("unchecked")
		VariableID<Number> altID =
				(VariableID<Number>) getVariableLibrary().getVariableID(altInst, "LocalVar");

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

		ComplexNEPFormula<Number> dynamicformula =
				new ComplexNEPFormula<Number>("dynamic(equipVar, localVar)", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> dynamicMod = AbstractModifier.add(dynamicformula, 100);

		manager.addModifier(resultID, dynamicMod, equipInst);
		assertEquals(2, store.get(resultID));

		manager.addModifier(activeID, useAlt, source);
		assertEquals(3, store.get(resultID));

		manager.addModifier(altID, four, altInst);
		assertEquals(4, store.get(resultID));
	}
}
