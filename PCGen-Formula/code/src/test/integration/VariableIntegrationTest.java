/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
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
package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.factory.ShadowingScopeManager;
import pcgen.base.formula.factory.SimpleManagerFactory;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.DelegatingVariableStore;
import pcgen.base.formula.inst.FormulaUtilities;
import pcgen.base.formula.inst.ImplementedScopeLibrary;
import pcgen.base.formula.inst.MonitorableVariableStore;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.formula.inst.SimpleScopeInstanceFactory;
import pcgen.base.formula.inst.VariableManager;
import pcgen.base.solver.AggressiveStrategy;
import pcgen.base.solver.DynamicSolverDependencyManager;
import pcgen.base.solver.GeneralSolverSystem;
import pcgen.base.solver.Modifier;
import pcgen.base.solver.SimpleSolverManager;
import pcgen.base.solver.SolverDependencyManager;
import pcgen.base.solver.SolverStrategy;
import pcgen.base.solver.SolverUtilities;
import pcgen.base.solver.SupplierValueStore;
import pcgen.base.solver.testsupport.AbstractModifier;
import pcgen.base.testsupport.GlobalVarScoped;

public class VariableIntegrationTest
{


	private OperatorLibrary opLibrary;
	private FunctionLibrary functionLib;
	private SupplierValueStore valueStore;
	private MonitorableVariableStore backingStore;
	private WriteableVariableStore varStore;
	private ImplementedScopeLibrary scopeManager;
	private ScopeInstanceFactory instanceFactory;
	private VariableManager varLibrary;
	private ManagerFactory managerFactory;

	private GeneralSolverSystem manager;
	private GeneralSolverSystem backmanager;

	@BeforeEach
	protected void setUp()
	{
		opLibrary = FormulaUtilities.loadBuiltInOperators(new SimpleOperatorLibrary());
		functionLib = functionSetup(
			FormulaUtilities.loadBuiltInFunctions(new SimpleFunctionLibrary()));
		valueStore = new SupplierValueStore();
		valueStore.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 0);
		valueStore.addSolverFormat(FormatUtilities.STRING_MANAGER, () -> "");
		valueStore.addSolverFormat(FormatUtilities.BOOLEAN_MANAGER, () -> false);
		backingStore = new MonitorableVariableStore();
		varStore = new DelegatingVariableStore(backingStore);
		ShadowingScopeManager ssm = new ShadowingScopeManager();
		ssm.registerGlobalScope("Global");
		ssm.registerGlobalScope("Static");
		ssm.registerScope("Global", "Global.Local");
		ssm.registerScope("Static", "Static.Local");
		ssm.linkScope("Static", "Global");
		ssm.linkScope("Static.Local", "Global.Local");
		scopeManager = ssm.instantiate().get();

		instanceFactory = new SimpleScopeInstanceFactory(scopeManager);
		varLibrary = new VariableManager(scopeManager, scopeManager, instanceFactory, valueStore);
		managerFactory = new SimpleManagerFactory(scopeManager, opLibrary, varLibrary, functionLib, varStore, instanceFactory);
		backmanager = SolverUtilities.buildDynamicSolverSystem(varLibrary, managerFactory, valueStore, backingStore);
		//manager = SolverUtilities.buildDynamicSolverSystem(varLibrary, managerFactory, valueStore, varStore);
		SimpleSolverManager newSolver =
				new SimpleSolverManager(varLibrary::isLegalVariableID,
					managerFactory, valueStore, varStore);
		SolverDependencyManager dm =
				new DynamicSolverDependencyManager(managerFactory, varStore);
		SolverStrategy strategy = new AggressiveStrategy(dm::processForChildren,
			newSolver::processSolver);
		backingStore.addGeneralListener(event -> strategy.processValueUpdated(event.getVarID()));
		manager = new GeneralSolverSystem(newSolver, dm, strategy);
	}

	protected FunctionLibrary functionSetup(WriteableFunctionLibrary wfl)
	{
		return wfl;
	}

	@AfterEach
	protected void tearDown()
	{
		scopeManager = null;
		valueStore = null;
		opLibrary = null;
		functionLib = null;
		managerFactory = null;
		instanceFactory = null;
		varStore = null;
		varLibrary = null;
	}

	@Test
	public void testAddRemoveBackStoreFirst()
	{
		GlobalVarScoped gvs = new GlobalVarScoped("Global");
		GlobalVarScoped svs = new GlobalVarScoped("Static");
		MultiVarScoped thing = new MultiVarScoped("Craft");
		thing.addLocalScope("Global.Local");
		thing.addLocalScope("Static.Local");
		thing.addParent("Global", gvs);
		thing.addParent("Static", svs);
		
		ScopeInstance globalInst = instanceFactory.get("Global", gvs);
		ScopeInstance staticInst = instanceFactory.get("Static", svs);
		ScopeInstance thingGlobalInst = instanceFactory.get("Global.Local", thing);
		ScopeInstance thingStaticInst = instanceFactory.get("Static.Local", thing);
		
		ImplementedScope globalScope = scopeManager.getImplementedScope("Global");
		ImplementedScope staticScope = scopeManager.getImplementedScope("Static");
		ImplementedScope gLocalScope = scopeManager.getImplementedScope("Global.Local");
		ImplementedScope sLocalScope = scopeManager.getImplementedScope("Static.Local");
		varLibrary.assertLegalVariableID("test", globalScope,
			FormatUtilities.NUMBER_MANAGER);
		varLibrary.assertLegalVariableID("backtest", staticScope,
			FormatUtilities.NUMBER_MANAGER);
		varLibrary.assertLegalVariableID("itemvar", gLocalScope,
			FormatUtilities.NUMBER_MANAGER);
		varLibrary.assertLegalVariableID("backitemvar", sLocalScope,
			FormatUtilities.NUMBER_MANAGER);

		@SuppressWarnings("unchecked")
		VariableID<Number> testID =
				(VariableID<Number>) varLibrary.getVariableID(globalInst, "test");
		@SuppressWarnings("unchecked")
		VariableID<Number> backID =
				(VariableID<Number>) varLibrary.getVariableID(staticInst, "backtest");
		@SuppressWarnings("unchecked")
		VariableID<Number> itemID =
				(VariableID<Number>) varLibrary.getVariableID(thingGlobalInst, "itemvar");
		@SuppressWarnings("unchecked")
		VariableID<Number> backitemID =
				(VariableID<Number>) varLibrary.getVariableID(thingStaticInst, "backitemvar");

		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("backitemvar+test+backtest", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> itemMod = AbstractModifier.add(formula, 100);

		ComplexNEPFormula<Number> add5 =
				new ComplexNEPFormula<>("5", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> add5mod = AbstractModifier.add(add5, 100);

		ComplexNEPFormula<Number> add3 =
				new ComplexNEPFormula<>("3", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> add3mod = AbstractModifier.add(add3, 100);

		ComplexNEPFormula<Number> add11 =
				new ComplexNEPFormula<>("11", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> add11mod = AbstractModifier.add(add11, 100);

		assertEquals(null, varStore.get(itemID));
		backmanager.addModifier(backID, add3mod, thingStaticInst);
		backmanager.addModifier(backitemID, add11mod, staticInst);
		manager.addModifier(itemID, itemMod, thingGlobalInst);
		assertEquals(14, varStore.get(itemID));
		manager.addModifier(testID, add5mod, globalInst);
		assertEquals(19, varStore.get(itemID));
	}

	@Test
	public void testAddRemoveOutOfOrder()
	{
		GlobalVarScoped gvs = new GlobalVarScoped("Global");
		GlobalVarScoped svs = new GlobalVarScoped("Static");
		MultiVarScoped thing = new MultiVarScoped("Craft");
		thing.addLocalScope("Global.Local");
		thing.addLocalScope("Static.Local");
		thing.addParent("Global", gvs);
		thing.addParent("Static", svs);
		
		ScopeInstance globalInst = instanceFactory.get("Global", gvs);
		ScopeInstance staticInst = instanceFactory.get("Static", svs);
		ScopeInstance thingGlobalInst = instanceFactory.get("Global.Local", thing);
		ScopeInstance thingStaticInst = instanceFactory.get("Static.Local", thing);
		
		ImplementedScope globalScope = scopeManager.getImplementedScope("Global");
		ImplementedScope staticScope = scopeManager.getImplementedScope("Static");
		ImplementedScope gLocalScope = scopeManager.getImplementedScope("Global.Local");
		ImplementedScope sLocalScope = scopeManager.getImplementedScope("Static.Local");
		varLibrary.assertLegalVariableID("test", globalScope,
			FormatUtilities.NUMBER_MANAGER);
		varLibrary.assertLegalVariableID("backtest", staticScope,
			FormatUtilities.NUMBER_MANAGER);
		varLibrary.assertLegalVariableID("itemvar", gLocalScope,
			FormatUtilities.NUMBER_MANAGER);
		varLibrary.assertLegalVariableID("backitemvar", sLocalScope,
			FormatUtilities.NUMBER_MANAGER);

		@SuppressWarnings("unchecked")
		VariableID<Number> testID =
				(VariableID<Number>) varLibrary.getVariableID(globalInst, "test");
		@SuppressWarnings("unchecked")
		VariableID<Number> backID =
				(VariableID<Number>) varLibrary.getVariableID(staticInst, "backtest");
		@SuppressWarnings("unchecked")
		VariableID<Number> itemID =
				(VariableID<Number>) varLibrary.getVariableID(thingGlobalInst, "itemvar");
		@SuppressWarnings("unchecked")
		VariableID<Number> backitemID =
				(VariableID<Number>) varLibrary.getVariableID(thingStaticInst, "backitemvar");

		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("backitemvar+test+backtest", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> itemMod = AbstractModifier.add(formula, 100);

		ComplexNEPFormula<Number> add5 =
				new ComplexNEPFormula<>("5", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> add5mod = AbstractModifier.add(add5, 100);

		ComplexNEPFormula<Number> add3 =
				new ComplexNEPFormula<>("3", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> add3mod = AbstractModifier.add(add3, 100);

		ComplexNEPFormula<Number> add11 =
				new ComplexNEPFormula<>("11", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> add11mod = AbstractModifier.add(add11, 100);

		assertEquals(null, varStore.get(itemID));
		manager.addModifier(itemID, itemMod, thingGlobalInst);
		assertEquals(0, varStore.get(itemID));

		backmanager.addModifier(backID, add3mod, thingStaticInst);
		assertEquals(3, varStore.get(itemID));

		backmanager.addModifier(backitemID, add11mod, staticInst);
		assertEquals(14, varStore.get(itemID));

		manager.addModifier(testID, add5mod, globalInst);
		assertEquals(19, varStore.get(itemID));
	}

	public class MultiVarScoped implements VarScoped
	{

		public String name;
		public Set<String> scopeName = new HashSet<>();
		public Map<String, VarScoped> parentMap = new HashMap<>();

		public MultiVarScoped(String name)
		{
			super();
			this.name = Objects.requireNonNull(name);
		}
		
		public void addLocalScope(String string)
		{
			scopeName.add(string);
		}

		public void addParent(String scope, VarScoped parent)
		{
			parentMap.put(scope, parent);
		}

		@Override
		public String getKeyName()
		{
			return name;
		}

		@Override
		public String toString()
		{
			return "SVS:" + name;
		}

		@Override
		public VarScoped getProviderFor(ImplementedScope implScope)
		{
			String implScopeName = implScope.getName();
			if (scopeName.contains(implScopeName))
			{
				return this;
			}
			return Objects.requireNonNull(parentMap.get(implScopeName));
		}
	}
}
