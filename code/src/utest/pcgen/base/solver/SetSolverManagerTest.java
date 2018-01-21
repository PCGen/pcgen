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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pcgen.base.calculation.BasicCalculation;
import pcgen.base.calculation.CalculationModifier;
import pcgen.base.calculation.NEPCalculation;
import pcgen.base.calculation.PCGenModifier;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.inst.ScopeInstanceFactory;
import pcgen.base.formula.inst.SimpleFormulaManager;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.solver.testsupport.TrackingVariableCache;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.ProcessCalculation;
import pcgen.cdom.formula.scope.EquipmentScope;
import pcgen.cdom.reference.CDOMFactory;
import pcgen.cdom.reference.SimpleReferenceManufacturer;
import pcgen.core.Equipment;
import pcgen.rules.persistence.token.ModifierFactory;
import plugin.function.DropIntoContext;

public class SetSolverManagerTest
{
	private static final class BasicSet implements BasicCalculation
	{
		@Override
		public String getIdentification()
		{
			return "SET";
		}

		@Override
		public int getInherentPriority()
		{
			return 0;
		}

		@Override
		public Object process(Object previousValue, Object argument)
		{
			return argument;
		}
	}

	private final LegalScope globalScope = new SimpleLegalScope(null, "Global");
	private TrackingVariableCache vc;
	private LegalScopeLibrary vsLib;
	private VariableLibrary sl;
	private FormulaManager fm;
	private DynamicSolverManager manager;
	private final FormatManager<Number> numberManager = new NumberManager();
	private final FormatManager<String> stringManager = new StringManager();
	private final FormatManager<Equipment> equipmentManager = new SimpleReferenceManufacturer<>(new CDOMFactory<>(Equipment.class));
	private ArrayFormatManager<String> arrayManager;
	private ScopeInstanceFactory siFactory;

	@Before
	public void setUp() throws Exception
	{
		FunctionLibrary fl = new SimpleFunctionLibrary();
		fl.addFunction(new DropIntoContext());
		OperatorLibrary ol = new SimpleOperatorLibrary();
		vc = new TrackingVariableCache();
		vsLib = new LegalScopeLibrary();
		EquipmentScope equipScope = new EquipmentScope();
		equipScope.setParent(globalScope);
		vsLib.registerScope(equipScope);
		sl = new VariableLibrary(vsLib);
		arrayManager = new ArrayFormatManager<>(stringManager, ',');
		ManagerFactory managerFactory = new ManagerFactory()
		{
		};
		siFactory = new ScopeInstanceFactory(vsLib);
		fm = new SimpleFormulaManager(ol, sl, siFactory, vc, new SolverFactory());
		fm = fm.getWith(FormulaManager.FUNCTION, fl);
		SolverFactory solverFactory = new SolverFactory();
		ModifierFactory am1 = new plugin.modifier.set.SetModifierFactory<>();
		PCGenModifier emptyArrayMod =
				am1.getModifier("", managerFactory, null, globalScope, arrayManager);
		solverFactory.addSolverFormat(arrayManager.getManagedClass(), emptyArrayMod);
		
		NEPCalculation calc = new ProcessCalculation<>(new Equipment(),
				new BasicSet(), equipmentManager);
		CalculationModifier em = new CalculationModifier<>(calc, equipmentManager);
		solverFactory.addSolverFormat(Equipment.class, em);

		manager = new DynamicSolverManager(fm, managerFactory, solverFactory, vc);
		ModifierFactory mfn = new plugin.modifier.number.SetModifierFactory();
		PCGenModifier mod =
				mfn.getModifier("0", managerFactory, null, globalScope, numberManager);
		mod.addAssociation("PRIORITY=0");
		solverFactory.addSolverFormat(numberManager.getManagedClass(), mod);
		ModifierFactory mfs = new plugin.modifier.string.SetModifierFactory();
		Modifier mods =
				mfs.getModifier("", managerFactory, null, globalScope, stringManager);
		solverFactory.addSolverFormat(stringManager.getManagedClass(), mods);

	}

	@Test
	public void testProcessDependentSet()
	{
		sl.assertLegalVariableID("Regions", globalScope, arrayManager);
		ScopeInstance scopeInst = siFactory.getGlobalInstance(globalScope.getName());
		VariableID<String[]> regions =
				(VariableID<String[]>) sl.getVariableID(scopeInst, "Regions");
		manager.createChannel(regions);
		Object[] array = vc.get(regions);
		List<Object> list = Arrays.asList(array);
		assertEquals(0, array.length);
		assertTrue(vc.set.contains(regions));
		assertEquals(1, vc.set.size());
		vc.reset();

		ModifierFactory am1 = new plugin.modifier.set.AddModifierFactory<>();
		PCGenModifier mod = am1.getModifier("France,England", new ManagerFactory()
		{
		}, null, globalScope, arrayManager);
		mod.addAssociation("PRIORITY=2000");
		manager.addModifier(regions, mod, scopeInst);
		array = vc.get(regions);
		assertThat(2, is(array.length));
		list = Arrays.asList(array);
		assertTrue(list.contains("England"));
		assertTrue(list.contains("France"));
		assertTrue(vc.set.contains(regions));
		assertEquals(1, vc.set.size());
		vc.reset();

		ModifierFactory am2 = new plugin.modifier.set.AddModifierFactory<>();
		mod = am2.getModifier("Greece,England", new ManagerFactory()
		{
		}, null, globalScope, arrayManager);
		mod.addAssociation("PRIORITY=3000");
		manager.addModifier(regions, mod, scopeInst);
		array = vc.get(regions);
		assertThat(3, is(array.length));
		list = Arrays.asList(array);
		assertTrue(list.contains("England"));
		assertTrue(list.contains("France"));
		assertTrue(list.contains("Greece"));
		assertTrue(vc.set.contains(regions));
		assertEquals(1, vc.set.size());
		vc.reset();
	}

	@Test
	public void testProcessDynamicSet()
	{
		LegalScope equipScope = vsLib.getScope("EQUIPMENT");
		sl.assertLegalVariableID("LocalVar", equipScope, numberManager);
		sl.assertLegalVariableID("ResultVar", globalScope, numberManager);
		sl.assertLegalVariableID("EquipVar", globalScope, equipmentManager);

		Equipment equip = new Equipment();
		equip.setName("EquipKey");
		Equipment equipalt = new Equipment();
		equipalt.setName("EquipAlt");

		ScopeInstance scopeInste = siFactory.get("EQUIPMENT", equip);
		VariableID varIDe = sl.getVariableID(scopeInste, "LocalVar");
		manager.createChannel(varIDe);
		vc.put(varIDe, 2);
		ScopeInstance scopeInsta = siFactory.get("EQUIPMENT", equipalt);
		VariableID varIDa = sl.getVariableID(scopeInsta, "LocalVar");
		manager.createChannel(varIDa);
		vc.put(varIDa, 3);
		ScopeInstance globalInst = siFactory.getGlobalInstance("Global");
		VariableID varIDq = sl.getVariableID(globalInst, "EquipVar");
		manager.createChannel(varIDq);
		VariableID varIDr = sl.getVariableID(globalInst, "ResultVar");
		manager.createChannel(varIDr);
		
		ModifierFactory am1 = new plugin.modifier.number.SetModifierFactory();
		ModifierFactory amString = new plugin.modifier.string.SetModifierFactory();
		PCGenModifier mod2 = am1.getModifier("2", new ManagerFactory()
		{
		}, fm, globalScope, numberManager);
		mod2.addAssociation("PRIORITY=2000");
		PCGenModifier mod3 = am1.getModifier("3", new ManagerFactory()
		{
		}, fm, globalScope, numberManager);
		mod3.addAssociation("PRIORITY=2000");
		PCGenModifier mod4 = am1.getModifier("4", new ManagerFactory()
		{
		}, fm, globalScope, numberManager);
		mod4.addAssociation("PRIORITY=3000");
		String formula = "dropIntoContext(\"EQUIPMENT\",EquipVar,LocalVar)";
		PCGenModifier modf = am1.getModifier(formula, new ManagerFactory()
		{
		}, fm, globalScope, numberManager);
		modf.addAssociation("PRIORITY=2000");
		
		NEPCalculation calc1 = new ProcessCalculation<>(equip,
				new BasicSet(), equipmentManager);
		CalculationModifier mod_e1 = new CalculationModifier<>(calc1, equipmentManager);

		NEPCalculation calc2 = new ProcessCalculation<>(equipalt,
				new BasicSet(), equipmentManager);
		CalculationModifier mod_e2 = new CalculationModifier<>(calc2, equipmentManager);

		manager.addModifier(varIDe, mod2, scopeInste);
		manager.addModifier(varIDa, mod3, scopeInsta);
		assertEquals(2, vc.get(varIDe));
		assertEquals(3, vc.get(varIDa));
		assertEquals(0, vc.get(varIDr));

		manager.addModifier(varIDq, mod_e1, globalInst);
		manager.addModifier(varIDr, modf, globalInst);
		assertEquals(2, vc.get(varIDr));

		manager.addModifier(varIDq, mod_e2, globalInst);
		assertEquals(3, vc.get(varIDr));

		manager.addModifier(varIDa, mod4, scopeInsta);
		assertEquals(4, vc.get(varIDr));
	}

}
