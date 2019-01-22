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

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.solver.SimpleSolverFactory;
import pcgen.base.solver.SolverFactory;
import pcgen.base.solver.SupplierValueStore;

public class SimpleFormulaManagerTest extends TestCase
{

	private VariableLibrary variableLibrary;
	private SimpleOperatorLibrary opLibrary;
	private SimpleVariableStore resultsStore;
	private SolverFactory defaultStore;
	private ScopeInstanceFactory siFactory;
	private SupplierValueStore valueStore;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		valueStore = new SupplierValueStore();
		defaultStore = new SimpleSolverFactory(valueStore);
		LegalScopeManager legalScopeManager = new ScopeManagerInst();
		variableLibrary = new VariableManager(legalScopeManager, valueStore);
		opLibrary = new SimpleOperatorLibrary();
		resultsStore = new SimpleVariableStore();
		siFactory = new SimpleScopeInstanceFactory(legalScopeManager);
		defaultStore.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 0);
		defaultStore.addSolverFormat(FormatUtilities.STRING_MANAGER, () -> "");
	}

	@SuppressWarnings("unused")
	@Test
	public void testDoubleConstructor()
	{
		try
		{
			new SimpleFormulaManager(null, null, null, null, null);
			fail("nulls must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(null, variableLibrary, siFactory,
				resultsStore, valueStore);
			fail("null op lib must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(opLibrary, null, siFactory, resultsStore,
				valueStore);
			fail("null var lib must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(opLibrary, variableLibrary, null,
				resultsStore, valueStore);
			fail("null var siFactory must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(opLibrary, variableLibrary, siFactory, null,
				valueStore);
			fail("null results must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(opLibrary, variableLibrary, siFactory,
				resultsStore, null);
			fail("null defaults must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testGetDefault()
	{
		FormulaManager formulaManager = new SimpleFormulaManager(opLibrary, variableLibrary, siFactory,
			resultsStore, valueStore);
		assertEquals(0,formulaManager.getDefault(FormatUtilities.NUMBER_MANAGER));
		assertEquals("", formulaManager.getDefault(FormatUtilities.STRING_MANAGER));
		Object[] array = formulaManager.getDefault(new ArrayFormatManager<>(FormatUtilities.NUMBER_MANAGER, '\n', ','));
		assertEquals(0, array.length);
	}
}
