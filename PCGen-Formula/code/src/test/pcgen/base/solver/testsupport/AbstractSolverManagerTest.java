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
package pcgen.base.solver.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.inst.ComplexNEPFormula;
import pcgen.base.formula.inst.SimpleVariableStore;
import pcgen.base.formula.inst.VariableManager;
import pcgen.base.solver.Modifier;
import pcgen.base.solver.SimpleSolverManager;
import pcgen.base.solver.SolverManager;
import pcgen.base.solver.SolverSystem;
import pcgen.base.testsupport.AbstractFormulaTestCase;

public abstract class AbstractSolverManagerTest extends AbstractFormulaTestCase
{

	private SolverManager solverManager;
	private VariableLibrary varLibrary;
	private WriteableVariableStore store;
	private ImplementedScope globalScope;
	private ScopeInstance globalScopeInst;

	@BeforeEach
	@Override
	protected void setUp()
	{
		super.setUp();
		solverManager = new SimpleSolverManager(
			getFormulaManager().getFactory()::isLegalVariableID,
			getFormulaManager(), getManagerFactory(), getValueStore(),
			getVariableStore());
		varLibrary = getVariableLibrary();
		store = getVariableStore();
		globalScope = getScopeManager().getImplementedScope("Global");
		globalScopeInst = getGlobalScopeInst();
	}

	@AfterEach
	@Override
	protected void tearDown()
	{
		super.tearDown();
		solverManager = null;
		varLibrary = null;
		store = null;
		globalScope = null;
		globalScopeInst = null;
	}

	protected abstract SolverSystem getManager();

	@Test
	public void testIllegalAddModifier()
	{
		assertLegalVariable("HP", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> hp =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "HP");
		AbstractModifier<Number> modifier = AbstractModifier.setNumber(6, 5);
		ScopeInstance source = globalScopeInst;
		assertThrows(NullPointerException.class, () -> getManager().addModifier(null, modifier, source));
		assertThrows(NullPointerException.class, () -> getManager().addModifier(hp, null, source));
		assertThrows(NullPointerException.class, () -> getManager().addModifier(hp, modifier, null));
		//Invalid ID very bad
		VariableLibrary alternateLibrary = new VariableManager(getScopeManager(), getValueStore());
		alternateLibrary.assertLegalVariableID("brains", globalScope,
			FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> brains = (VariableID<Number>) alternateLibrary
			.getVariableID(globalScopeInst, "Brains");
		assertThrows(IllegalArgumentException.class, () -> getManager().addModifier(brains, modifier, source));
	}

	@Test
	public void testAddModifier()
	{
		assertLegalVariable("HP", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> hp =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "HP");
		assertEquals(null, store.get(hp));
		ScopeInstance source = globalScopeInst;
		AbstractModifier<Number> modifier = AbstractModifier.setNumber(6, 5);
		getManager().addModifier(hp, modifier, source);
		assertEquals(6, store.get(hp));

		//Create not required...
		assertLegalVariable("HitPoints", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> hitpoints = (VariableID<Number>) varLibrary
			.getVariableID(globalScopeInst, "HitPoints");
		assertEquals(null, store.get(hitpoints));
		getManager().addModifier(hitpoints, modifier, source);
		assertEquals(6, store.get(hitpoints));

		getScopeManager().registerScope("Global", "STAT");
		ScopeInstance strInst =
				getInstanceFactory().get("Global.STAT", Optional.of(new MockStat("Strength")));

		getManager().addModifier(hitpoints, AbstractModifier.setNumber(12, 3), strInst);
		assertEquals(6, store.get(hitpoints));
		getManager().removeModifier(hitpoints, modifier, source);
		assertEquals(12, store.get(hitpoints));
	}

	@Test
	public void testComplex()
	{
		ScopeInstance source = globalScopeInst;
		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("arms+legs", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> formulaMod = AbstractModifier.add(formula, 100);
		assertLegalVariable("Limbs", "Global", FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("arms", "Global", FormatUtilities.NUMBER_MANAGER);
		assertLegalVariable("legs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Limbs");
		assertEquals(null, store.get(limbs));
		getManager().addModifier(limbs, formulaMod, source);
		assertEquals(0, store.get(limbs));

		AbstractModifier<Number> two = AbstractModifier.setNumber(2, 5);
		@SuppressWarnings("unchecked")
		VariableID<Number> arms =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Arms");
		assertEquals(0, store.get(arms));
		getManager().addModifier(arms, two, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(limbs));

		AbstractModifier<Number> four = AbstractModifier.setNumber(4, 5);
		@SuppressWarnings("unchecked")
		VariableID<Number> legs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Legs");
		assertEquals(0, store.get(legs));
		getManager().addModifier(legs, four, source);
		assertEquals(2, store.get(arms));
		assertEquals(4, store.get(legs));
		assertEquals(6, store.get(limbs));

		getManager().removeModifier(arms, two, source);
		assertEquals(0, store.get(arms));
		assertEquals(4, store.get(legs));
		assertEquals(4, store.get(limbs));
	}

	@Test
	public void testChained()
	{
		ScopeInstance source = globalScopeInst;
		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("arms+legs", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> limbsMod = AbstractModifier.add(formula, 100);

		ComplexNEPFormula<Number> handsformula =
				new ComplexNEPFormula<>("fingers/5", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> handsMod = AbstractModifier.add(handsformula, 100);

		assertLegalVariable("Limbs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Limbs");
		assertLegalVariable("arms", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> arms =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Arms");
		assertLegalVariable("Fingers", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> fingers =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Fingers");
		assertLegalVariable("legs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> legs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Legs");
		assertEquals(null, store.get(limbs));
		getManager().addModifier(limbs, limbsMod, source);

		assertEquals(0, store.get(arms));
		getManager().addModifier(arms, handsMod, source);
		assertEquals(0, store.get(arms));

		AbstractModifier<Number> ten = AbstractModifier.setNumber(10, 5);
		getManager().addModifier(fingers, ten, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(limbs));

		AbstractModifier<Number> four = AbstractModifier.setNumber(2, 5);
		assertEquals(0, store.get(legs));
		getManager().addModifier(legs, four, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(legs));
		assertEquals(4, store.get(limbs));

		getManager().removeModifier(arms, handsMod, source);
		assertEquals(0, store.get(arms));
		assertEquals(2, store.get(legs));
		assertEquals(2, store.get(limbs));

	}

	@Test
	public void testIllegalRemoveModifier()
	{
		assertLegalVariable("HP", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> hp =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "HP");
		AbstractModifier<Number> modifier = AbstractModifier.setNumber(6, 5);
		ScopeInstance source = globalScopeInst;
		assertThrows(NullPointerException.class, () -> getManager().removeModifier(null, modifier, source));
		assertThrows(NullPointerException.class, () -> getManager().removeModifier(hp, null, source));
		assertThrows(NullPointerException.class, () -> getManager().removeModifier(hp, modifier, null));
		//Not present is Harmless
		getManager().removeModifier(hp, modifier, source);
		//Invalid ID very bad
		VariableLibrary alternateLibrary = new VariableManager(getScopeManager(), getValueStore());
		alternateLibrary.assertLegalVariableID("brains", globalScope,
			FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> brains = (VariableID<Number>) alternateLibrary
			.getVariableID(globalScopeInst, "Brains");
		assertThrows(IllegalArgumentException.class, () -> getManager().removeModifier(brains, modifier, source));
	}

	@Test
	public void testCircular()
	{
		ScopeInstance source = globalScopeInst;
		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("arms+legs", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> limbsMod = AbstractModifier.add(formula, 100);

		ComplexNEPFormula<Number> handsformula =
				new ComplexNEPFormula<>("fingers/5", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> handsMod = AbstractModifier.add(handsformula, 100);

		ComplexNEPFormula<Number> fingersformula =
				new ComplexNEPFormula<>("limbs*5", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> fingersMod = AbstractModifier.add(fingersformula, 100);

		assertLegalVariable("Limbs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Limbs");
		assertLegalVariable("arms", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> arms =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Arms");
		assertLegalVariable("Fingers", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> fingers =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Fingers");
		assertLegalVariable("legs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> legs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Legs");
		assertEquals(null, store.get(limbs));
		getManager().addModifier(limbs, limbsMod, source);

		assertEquals(0, store.get(arms));
		getManager().addModifier(arms, handsMod, source);
		assertEquals(0, store.get(arms));

		AbstractModifier<Number> ten = AbstractModifier.setNumber(10, 5);
		getManager().addModifier(fingers, ten, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(limbs));

		AbstractModifier<Number> four = AbstractModifier.setNumber(2, 5);
		assertEquals(0, store.get(legs));
		getManager().addModifier(legs, four, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(legs));
		assertEquals(4, store.get(limbs));

		try
		{
			getManager().addModifier(fingers, fingersMod, source);
			fail("How?");
		}
		catch (IllegalStateException e)
		{
			//yes, need to barf on infinite loop
		}

	}

	public SolverManager getSolverManager()
	{
		return solverManager;
	}

	@Test
	public void testIndependence()
	{
		ScopeInstance source = globalScopeInst;
		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("arms+legs", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> limbsMod = AbstractModifier.add(formula, 100);

		ComplexNEPFormula<Number> handsformula =
				new ComplexNEPFormula<>("fingers/5", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> handsMod = AbstractModifier.add(handsformula, 100);

		assertLegalVariable("Limbs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Limbs");
		assertLegalVariable("arms", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> arms =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Arms");
		assertLegalVariable("Fingers", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> fingers =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Fingers");
		assertLegalVariable("legs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> legs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Legs");
		assertEquals(null, store.get(limbs));
		getManager().addModifier(limbs, limbsMod, source);

		assertEquals(0, store.get(arms));
		getManager().addModifier(arms, handsMod, source);
		assertEquals(0, store.get(arms));

		AbstractModifier<Number> ten = AbstractModifier.setNumber(10, 5);
		getManager().addModifier(fingers, ten, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(limbs));

		SimpleVariableStore altstore = new SimpleVariableStore();
		SolverSystem alt = getManager().createReplacement(altstore);

		AbstractModifier<Number> four = AbstractModifier.setNumber(2, 5);
		assertEquals(0, store.get(legs));
		assertEquals(0, altstore.get(legs));
		getManager().addModifier(legs, four, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(legs));
		assertEquals(4, store.get(limbs));

		assertEquals(2, altstore.get(arms));
		assertEquals(2, altstore.get(limbs));

		alt.removeModifier(arms, handsMod, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(legs));
		assertEquals(4, store.get(limbs));

		assertEquals(0, altstore.get(arms));
		assertEquals(0, altstore.get(limbs));

		getManager().removeModifier(legs, four, source);
		assertEquals(2, store.get(arms));
		assertEquals(0, store.get(legs));
		assertEquals(2, store.get(limbs));

	}

	@Test
	public void testCleanRemove()
	{
		ScopeInstance source = globalScopeInst;
		ComplexNEPFormula<Number> formula =
				new ComplexNEPFormula<>("arms+legs", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> limbsMod = AbstractModifier.add(formula, 100);

		ComplexNEPFormula<Number> handsformula =
				new ComplexNEPFormula<>("arms", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> armMod = AbstractModifier.add(handsformula, 100);

		assertLegalVariable("Limbs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Limbs");
		assertLegalVariable("arms", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> arms =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Arms");
		assertLegalVariable("WithExtra", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> extra =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "WithExtra");
		assertLegalVariable("legs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> legs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Legs");
		assertEquals(null, store.get(limbs));
		getManager().addModifier(limbs, limbsMod, source);
		getManager().addModifier(extra, limbsMod, source);
		getManager().addModifier(extra, armMod, source);

		AbstractModifier<Number> two = AbstractModifier.setNumber(2, 5);
		getManager().addModifier(arms, two, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(limbs));
		assertEquals(4, store.get(extra));

		getManager().addModifier(legs, two, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(legs));
		assertEquals(4, store.get(limbs));
		assertEquals(6, store.get(extra));

		//This makes sure the remove of arms+legs doesn't disrupt that ANOTHER modifier has arms
		getManager().removeModifier(extra, limbsMod, source);
		assertEquals(2, store.get(arms));
		assertEquals(2, store.get(legs));
		assertEquals(4, store.get(limbs));
		assertEquals(2, store.get(extra));
	}

	@Test
	public void testAssertion()
	{
		ScopeInstance source = globalScopeInst;
		ComplexNEPFormula<Number> five =
				new ComplexNEPFormula<>("5", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> numMod = AbstractModifier.add(five, 100);

		ComplexNEPFormula<Number> fiveString =
				new ComplexNEPFormula<>("\"4\"", FormatUtilities.NUMBER_MANAGER);
		Modifier<Number> strMod = AbstractModifier.add(fiveString, 200);

		assertLegalVariable("Limbs", "Global", FormatUtilities.NUMBER_MANAGER);
		@SuppressWarnings("unchecked")
		VariableID<Number> limbs =
				(VariableID<Number>) varLibrary.getVariableID(globalScopeInst, "Limbs");
		assertEquals(null, store.get(limbs));
		getManager().addModifier(limbs, numMod, source);

		assertEquals(5, store.get(limbs));
		getManager().addModifier(limbs, strMod, source);
		assertEquals(9, store.get(limbs));
	}
	
	public ImplementedScope getGlobalImplementedScope()
	{
		return globalScope;
	}
}
