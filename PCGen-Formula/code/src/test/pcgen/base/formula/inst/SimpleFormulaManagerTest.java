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
import pcgen.base.formula.base.LegalScopeLibrary;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.solver.SolverFactory;

public class SimpleFormulaManagerTest extends TestCase
{

	private VariableLibrary varLibrary;
	private SimpleOperatorLibrary opLibrary;
	private SimpleVariableStore resultsStore;
	private SolverFactory defaultStore;
	private ScopeInstanceFactory siFactory;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		LegalScopeLibrary scopeLibrary = new LegalScopeLibrary();
		varLibrary = new VariableLibrary(scopeLibrary);
		opLibrary = new SimpleOperatorLibrary();
		resultsStore = new SimpleVariableStore();
		defaultStore = new SolverFactory();
		siFactory = new ScopeInstanceFactory(scopeLibrary);
	}

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
			new SimpleFormulaManager(null, varLibrary, siFactory,
				resultsStore, defaultStore);
			fail("null op lib must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(opLibrary, null, siFactory, resultsStore,
				defaultStore);
			fail("null var lib must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(opLibrary, varLibrary, null,
				resultsStore, defaultStore);
			fail("null var siFactory must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(opLibrary, varLibrary, siFactory, null,
				defaultStore);
			fail("null results must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleFormulaManager(opLibrary, varLibrary, siFactory,
				resultsStore, null);
			fail("null defaults must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

}
