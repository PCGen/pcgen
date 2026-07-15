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

import pcgen.cdom.calculation.BasicCalculation;
import pcgen.cdom.calculation.CalculationModifier;
import pcgen.cdom.calculation.FormulaModifier;
import pcgen.cdom.calculation.NEPCalculation;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.solver.testsupport.TrackingVariableCache;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.ProcessCalculation;
import pcgen.cdom.formula.local.ModifierDecoration;
import pcgen.cdom.formula.scope.EquipmentScope;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.formula.scope.GlobalPCVarScoped;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.formula.scope.SkillScope;
import pcgen.core.Skill;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.context.VariableContext;
import pcgen.rules.persistence.token.ModifierFactory;

import plugin.function.GetOtherFunction;
import util.FormatSupport;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SetSolverManagerTest
{

	private TrackingVariableCache vc;
	private SolverManager manager;
	private FormatManager<Skill> skillManager;
	private ArrayFormatManager<String> arrayManager;
	private ScopeInstanceFactory siFactory;
	private RuntimeLoadContext context;
	private VariableLibrary sl;

	@BeforeEach
	void setUp() throws Exception
	{
		context = new RuntimeLoadContext(
			RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		FormatSupport.addBasicDefaults(context);
		VariableContext variableContext = context.getVariableContext();
		variableContext.addFunction(new GetOtherFunction());

		PCGenScope globalScope = new GlobalPCScope();
		variableContext.registerScope(globalScope);
		EquipmentScope equipScope = new EquipmentScope();
		equipScope.setParent(globalScope);
		variableContext.registerScope(equipScope);
		SkillScope skillScope = new SkillScope();
		skillScope.setParent(globalScope);
		variableContext.registerScope(skillScope);

		arrayManager = new ArrayFormatManager<>(FormatUtilities.STRING_MANAGER, '\n', ',');
		variableContext.addDefault(arrayManager, () -> new String[0]);
		skillManager = context.getReferenceContext().getManufacturer(Skill.class);
		Skill defaultSkill = new Skill();
		variableContext.addDefault(skillManager, () -> defaultSkill);

		sl = variableContext;
		vc = new TrackingVariableCache();
		siFactory = variableContext.getScopeInstanceFactory();
		manager = variableContext.generateSolverManager(vc);
	}

	@Test
	void testProcessDependentSet()
	{
		PCGenScope globalScope = context.getVariableContext().getScope(GlobalPCScope.GLOBAL_SCOPE_NAME);
		sl.assertLegalVariableID("Regions", globalScope, arrayManager);
		ScopeInstance scopeInst = siFactory.get(GlobalPCScope.GLOBAL_SCOPE_NAME,
			new GlobalPCVarScoped(GlobalPCScope.GLOBAL_SCOPE_NAME));
		VariableID<String[]> regions =
				(VariableID<String[]>) sl.getVariableID(scopeInst, "Regions");
		manager.processSolver(regions);
		Object[] array = vc.get(regions);
		List<Object> list;
		assertEquals(0, array.length);
		assertTrue(vc.contains(regions));
		assertEquals(1, vc.size());
		vc.reset();

		ModifierFactory am1 = new plugin.modifier.set.AddModifierFactory<>();
		FormulaModifier mod = am1.getModifier("France,England", arrayManager);
		mod.addAssociation("PRIORITY=2000");
		manager.addModifier(regions, new ModifierDecoration<>(mod), scopeInst);
		manager.processSolver(regions);
		array = vc.get(regions);
		MatcherAssert.assertThat(2, is(array.length));
		list = Arrays.asList(array);
		assertTrue(list.contains("England"));
		assertTrue(list.contains("France"));
		assertTrue(vc.contains(regions));
		assertEquals(1, vc.size());
		vc.reset();

		ModifierFactory am2 = new plugin.modifier.set.AddModifierFactory<>();
		mod = am2.getModifier("Greece,England", arrayManager);
		mod.addAssociation("PRIORITY=3000");
		manager.addModifier(regions, new ModifierDecoration<>(mod), scopeInst);
		manager.processSolver(regions);
		array = vc.get(regions);
		MatcherAssert.assertThat(3, is(array.length));
		list = Arrays.asList(array);
		assertTrue(list.contains("England"));
		assertTrue(list.contains("France"));
		assertTrue(list.contains("Greece"));
		assertTrue(vc.contains(regions));
		assertEquals(1, vc.size());
		vc.reset();
	}

	@Test
	void testProcessDynamicSet()
	{
		VariableContext variableContext = context.getVariableContext();
		PCGenScope globalScope = variableContext.getScope(GlobalPCScope.GLOBAL_SCOPE_NAME);
		PCGenScope skillScope = variableContext.getScope("PC.SKILL");
		sl.assertLegalVariableID("LocalVar", skillScope, FormatUtilities.NUMBER_MANAGER);
		sl.assertLegalVariableID("ResultVar", globalScope, FormatUtilities.NUMBER_MANAGER);
		sl.assertLegalVariableID("SkillVar", globalScope, skillManager);

		Skill skill = new Skill();
		skill.setName("SkillKey");
		Skill skillalt = new Skill();
		skillalt.setName("SkillAlt");

		ScopeInstance scopeInste = siFactory.get("PC.SKILL", skill);
		VariableID varIDe = sl.getVariableID(scopeInste, "LocalVar");
		manager.processSolver(varIDe);
		vc.put(varIDe, 2);
		ScopeInstance scopeInsta = siFactory.get("PC.SKILL", skillalt);
		VariableID varIDa = sl.getVariableID(scopeInsta, "LocalVar");
		manager.processSolver(varIDa);
		vc.put(varIDa, 3);
		ScopeInstance globalInst = siFactory.get(GlobalPCScope.GLOBAL_SCOPE_NAME,
			new GlobalPCVarScoped(GlobalPCScope.GLOBAL_SCOPE_NAME));
		VariableID varIDq = sl.getVariableID(globalInst, "SkillVar");
		manager.processSolver(varIDq);
		VariableID varIDr = sl.getVariableID(globalInst, "ResultVar");
		manager.processSolver(varIDr);

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
		manager.processSolver(varIDe);
		manager.processSolver(varIDa);
		assertEquals(2, vc.get(varIDe));
		assertEquals(3, vc.get(varIDa));
		assertEquals(0, vc.get(varIDr));

		manager.addModifier(varIDq, new ModifierDecoration<>(mods1), globalInst);
		manager.addModifier(varIDr, new ModifierDecoration<>(modf), globalInst);
		manager.processSolver(varIDq);
		manager.processSolver(varIDr);
		assertEquals(2, vc.get(varIDr));

		manager.addModifier(varIDq, new ModifierDecoration<>(mods2), globalInst);
		manager.processSolver(varIDq);
		manager.processSolver(varIDr);
		assertEquals(3, vc.get(varIDr));

		manager.addModifier(varIDa, new ModifierDecoration<>(mod4), scopeInsta);
		manager.processSolver(varIDa);
		manager.processSolver(varIDr);
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
}
