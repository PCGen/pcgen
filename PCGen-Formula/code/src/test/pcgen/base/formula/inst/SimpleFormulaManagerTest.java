/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.inst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.solver.SupplierValueStore;
import pcgen.base.testsupport.TestUtilities;

public class SimpleFormulaManagerTest
{

	private VariableLibrary variableLibrary;
	private SimpleVariableStore resultsStore;
	private ScopeInstanceFactory siFactory;
	private SupplierValueStore valueStore;

	@BeforeEach
	void setUp()
	{
		valueStore = new SupplierValueStore();
		valueStore.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 0);
		valueStore.addSolverFormat(FormatUtilities.STRING_MANAGER, () -> "");
		valueStore.validateDefaults();
		resultsStore = new SimpleVariableStore();
		ScopeManager scopeManager = new ScopeManagerInst();
		variableLibrary = new VariableManager(scopeManager, valueStore);
		siFactory = new SimpleScopeInstanceFactory(scopeManager);
	}
	
	@AfterEach
	void tearDown()
	{
		variableLibrary = null;
		resultsStore = null;
		siFactory = null;
		valueStore = null;
	}

	@Test
	public void testDoubleConstructor()
	{
		assertThrows(NullPointerException.class, () -> new SimpleFormulaManager(null, null, null, null));
		assertThrows(NullPointerException.class, () -> new SimpleFormulaManager(null, siFactory, resultsStore, valueStore));
		assertThrows(NullPointerException.class, () -> new SimpleFormulaManager(variableLibrary, null, resultsStore, valueStore));
		assertThrows(NullPointerException.class, () -> new SimpleFormulaManager(variableLibrary, siFactory, null, valueStore));
		assertThrows(NullPointerException.class, () -> new SimpleFormulaManager(variableLibrary, siFactory, resultsStore, null));
	}

	@Test
	public void testGetDefault()
	{
		FormulaManager formulaManager = new SimpleFormulaManager(variableLibrary, siFactory,
			resultsStore, valueStore);
		assertEquals(0,formulaManager.getDefault(FormatUtilities.NUMBER_MANAGER));
		assertEquals("", formulaManager.getDefault(FormatUtilities.STRING_MANAGER));
		Object[] array = formulaManager.getDefault(TestUtilities.NUMBER_ARRAY_MANAGER);
		assertEquals(0, array.length);
	}
}
