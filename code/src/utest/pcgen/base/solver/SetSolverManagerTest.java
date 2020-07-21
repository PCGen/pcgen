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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.calculation.BasicCalculation;
import pcgen.base.calculation.CalculationModifier;
import pcgen.base.calculation.FormulaModifier;
import pcgen.base.calculation.NEPCalculation;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.formula.inst.SimpleFormulaManager;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.inst.SimpleOperatorLibrary;
import pcgen.base.formula.inst.SimpleScopeInstanceFactory;
import pcgen.base.formula.inst.VariableManager;
import pcgen.base.solver.testsupport.TrackingVariableCache;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.ProcessCalculation;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.formula.local.ModifierDecoration;
import pcgen.cdom.formula.scope.EquipmentScope;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.formula.scope.SkillScope;
import pcgen.core.Skill;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.token.ModifierFactory;

import plugin.function.GetOtherFunction;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SetSolverManagerTest
{

	private final PCGenScope globalScope = new GlobalPCScope();
	private TrackingVariableCache vc;
	private ScopeManagerInst vsLib;
	private VariableManager sl;
	private FormulaManager fm;
	private DynamicSolverManager manager;
	private FormatManager<Skill> skillManager;
	private ArrayFormatManager<String> arrayManager;
	private ScopeInstanceFactory siFactory;
	private RuntimeLoadContext context;
	private MyManagerFactory managerFactory;

	@BeforeEach
	void setUp() throws Exception
	{
		SupplierValueStore mvs = new SupplierValueStore();
		WriteableFunctionLibrary fl = new SimpleFunctionLibrary();
		fl.addFunction(new GetOtherFunction());
		OperatorLibrary ol = new SimpleOperatorLibrary();
		vc = new TrackingVariableCache();
		vsLib = new ScopeManagerInst();
		vsLib.registerScope(globalScope);
		EquipmentScope equipScope = new EquipmentScope();
		equipScope.setParent(globalScope);
		vsLib.registerScope(equipScope);
		SkillScope skillScope = new SkillScope();
		skillScope.setParent(globalScope);
		vsLib.registerScope(skillScope);
		sl = new VariableManager(vsLib, mvs);
		arrayManager = new ArrayFormatManager<>(FormatUtilities.STRING_MANAGER, '\n', ',');
		context = new RuntimeLoadContext(
			RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		skillManager = context.getReferenceContext().getManufacturer(Skill.class);
		managerFactory = new MyManagerFactory(context);
		siFactory = new SimpleScopeInstanceFactory(vsLib);
		fm = new SimpleFormulaManager(ol, sl, siFactory, vc, mvs);
		fm = fm.getWith(FormulaManager.FUNCTION, fl);
		SolverFactory solverFactory = new SimpleSolverFactory(mvs);
		solverFactory.addSolverFormat(arrayManager, () -> new String[0]);
		
		Skill defaultSkill = new Skill();
		solverFactory.addSolverFormat(skillManager, () -> defaultSkill);

		manager = new DynamicSolverManager(fm, managerFactory, solverFactory, vc);
		solverFactory.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 0);
		solverFactory.addSolverFormat(FormatUtilities.STRING_MANAGER, () -> "");
	}

	//TODO Tear Down

	@Test
	public void testProcessDependentSet()
	{
		sl.assertLegalVariableID("Regions", globalScope, arrayManager);
		ScopeInstance scopeInst = siFactory.getGlobalInstance(globalScope.getName());
		VariableID<String[]> regions =
				(VariableID<String[]>) sl.getVariableID(scopeInst, "Regions");
		manager.createChannel(regions);
		Object[] array = vc.get(regions);
		List<Object> list;
		assertEquals(0, array.length);
		assertTrue(vc.set.contains(regions));
		assertEquals(1, vc.set.size());
		vc.reset();

		ModifierFactory am1 = new plugin.modifier.set.AddModifierFactory<>();
		FormulaModifier mod = am1.getModifier("France,England", arrayManager);
		mod.addAssociation("PRIORITY=2000");
		manager.addModifier(regions, new ModifierDecoration<>(mod), scopeInst);
		array = vc.get(regions);
		MatcherAssert.assertThat(2, is(array.length));
		list = Arrays.asList(array);
		assertTrue(list.contains("England"));
		assertTrue(list.contains("France"));
		assertTrue(vc.set.contains(regions));
		assertEquals(1, vc.set.size());
		vc.reset();

		ModifierFactory am2 = new plugin.modifier.set.AddModifierFactory<>();
		mod = am2.getModifier("Greece,England", arrayManager);
		mod.addAssociation("PRIORITY=3000");
		manager.addModifier(regions, new ModifierDecoration<>(mod), scopeInst);
		array = vc.get(regions);
		MatcherAssert.assertThat(3, is(array.length));
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
		LegalScope skillScope = vsLib.getScope("PC.SKILL");
		sl.assertLegalVariableID("LocalVar", skillScope, FormatUtilities.NUMBER_MANAGER);
		sl.assertLegalVariableID("ResultVar", globalScope, FormatUtilities.NUMBER_MANAGER);
		sl.assertLegalVariableID("SkillVar", globalScope, skillManager);

		Skill skill = new Skill();
		skill.setName("SkillKey");
		Skill skillalt = new Skill();
		skillalt.setName("SkillAlt");

		ScopeInstance scopeInste = siFactory.get("PC.SKILL", Optional.of(skill));
		VariableID varIDe = sl.getVariableID(scopeInste, "LocalVar");
		manager.createChannel(varIDe);
		vc.put(varIDe, 2);
		ScopeInstance scopeInsta = siFactory.get("PC.SKILL", Optional.of(skillalt));
		VariableID varIDa = sl.getVariableID(scopeInsta, "LocalVar");
		manager.createChannel(varIDa);
		vc.put(varIDa, 3);
		ScopeInstance globalInst =
				siFactory.getGlobalInstance(GlobalPCScope.GLOBAL_SCOPE_NAME);
		VariableID varIDq = sl.getVariableID(globalInst, "SkillVar");
		manager.createChannel(varIDq);
		VariableID varIDr = sl.getVariableID(globalInst, "ResultVar");
		manager.createChannel(varIDr);
		
		ModifierFactory am1 = new plugin.modifier.number.SetModifierFactory();
		ModifierFactory amString = new plugin.modifier.string.SetModifierFactory();
		FormulaModifier mod2 =
				am1.getModifier("2", FormatUtilities.NUMBER_MANAGER);
		mod2.addAssociation("PRIORITY=2000");
		FormulaModifier mod3 =
				am1.getModifier("3", FormatUtilities.NUMBER_MANAGER);
		mod3.addAssociation("PRIORITY=2000");
		FormulaModifier mod4 =
				am1.getModifier("4", FormatUtilities.NUMBER_MANAGER);
		mod4.addAssociation("PRIORITY=3000");
		String formula = "getOther(\"PC.SKILL\",SkillVar,LocalVar)";
		context.getReferenceContext().importObject(skill);
		context.getReferenceContext().importObject(skillalt);
		FormulaModifier modf = am1.getModifier(formula, FormatUtilities.NUMBER_MANAGER);
		modf.addAssociation("PRIORITY=2000");
		
		NEPCalculation calc1 = new ProcessCalculation<>(skill,
				new BasicSet(), skillManager);
		CalculationModifier mods1 = new CalculationModifier<>(calc1, skillManager);

		NEPCalculation calc2 = new ProcessCalculation<>(skillalt,
				new BasicSet(), skillManager);
		CalculationModifier mods2 = new CalculationModifier<>(calc2, skillManager);

		manager.addModifier(varIDe, new ModifierDecoration<>(mod2), scopeInste);
		manager.addModifier(varIDa, new ModifierDecoration<>(mod3), scopeInsta);
		assertEquals(2, vc.get(varIDe));
		assertEquals(3, vc.get(varIDa));
		assertEquals(0, vc.get(varIDr));

		manager.addModifier(varIDq, new ModifierDecoration<>(mods1), globalInst);
		manager.addModifier(varIDr, new ModifierDecoration<>(modf), globalInst);
		assertEquals(2, vc.get(varIDr));

		manager.addModifier(varIDq, new ModifierDecoration<>(mods2), globalInst);
		assertEquals(3, vc.get(varIDr));

		manager.addModifier(varIDa, new ModifierDecoration<>(mod4), scopeInsta);
		assertEquals(4, vc.get(varIDr));
	}

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
	
	/**
	 * A custom ManagerFactory to inject the LoadContext.
	 */
	private static class MyManagerFactory implements ManagerFactory
	{
		private final LoadContext context;

		private MyManagerFactory(LoadContext context)
		{
			this.context = Objects.requireNonNull(context);
		}

		@Override
		public DependencyManager generateDependencyManager(FormulaManager formulaManager,
			ScopeInstance scopeInst)
		{
			DependencyManager dm = ManagerFactory.super.generateDependencyManager(
				formulaManager, scopeInst);
			return dm.getWith(ManagerKey.CONTEXT, context);
		}

		@Override
		public FormulaSemantics generateFormulaSemantics(FormulaManager manager,
			LegalScope legalScope)
		{
			FormulaSemantics fs =
					ManagerFactory.super.generateFormulaSemantics(manager, legalScope);
			return fs.getWith(ManagerKey.CONTEXT, context);
		}

		@Override
		public EvaluationManager generateEvaluationManager(FormulaManager formulaManager)
		{
			EvaluationManager em =
					ManagerFactory.super.generateEvaluationManager(formulaManager);
			return em.getWith(ManagerKey.CONTEXT, context);
		}
	}
}
