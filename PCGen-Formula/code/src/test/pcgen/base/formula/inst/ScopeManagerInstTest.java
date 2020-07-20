/*
 * Copyright 2014-16 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.inst;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.formula.base.LegalScope;

public class ScopeManagerInstTest extends TestCase
{

	private ScopeManagerInst legalScopeManager;
	private SimpleLegalScope globalScope = new SimpleLegalScope("Global");
	private SimpleLegalScope subScope = new SimpleLegalScope(globalScope, "SubScope");
	private SimpleLegalScope otherScope = new SimpleLegalScope(globalScope, "OtherScope");

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		legalScopeManager = new ScopeManagerInst();
	}

	@Test
	public void testNullRegister()
	{
		try
		{
			legalScopeManager.registerScope(null);
			fail("null must be rejected in registerScope");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testBadRegister()
	{
		try
		{
			legalScopeManager.registerScope(new BadLegalScope1());
			fail("null name must be rejected in registerScope");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			legalScopeManager.registerScope(new BadLegalScope2());
			fail("null parent must be rejected in registerScope");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			legalScopeManager.registerScope(subScope);
			fail("should reject because parent has not been added");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		legalScopeManager.registerScope(globalScope);
		legalScopeManager.registerScope(subScope);
		try
		{
			legalScopeManager.registerScope(new SimpleLegalScope(globalScope, "SubScope"));
			fail("dupe name be rejected in registerScope");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			legalScopeManager.registerScope(new SimpleLegalScope(globalScope, "Sub.Scope"));
			fail("dupe name be rejected in registerScope");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
	
	@Test
	public void testDupeOkay()
	{
		legalScopeManager.registerScope(globalScope);
		legalScopeManager.registerScope(subScope);
		//This is okay
		legalScopeManager.registerScope(subScope);
	}

	@Test
	public void testNullGet()
	{
		try
		{
			legalScopeManager.getChildScopes(null);
			fail("null must be rejected in getChildScopes");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testGetScope()
	{
		List<LegalScope> children = legalScopeManager.getChildScopes(globalScope);
		if (children != null)
		{
			assertEquals(0, children.size());
		}
		legalScopeManager.registerScope(globalScope);
		legalScopeManager.registerScope(subScope);
		//test getScope
		assertEquals(globalScope, legalScopeManager.getScope("Global"));
		assertEquals(subScope, legalScopeManager.getScope("Global.SubScope"));
		assert(legalScopeManager.getScope("OtherScope") == null);
		assert(legalScopeManager.getScope(null) == null);
	}

	@Test
	public void testGetChildScopes()
	{
		List<LegalScope> children = legalScopeManager.getChildScopes(globalScope);
		if (children != null)
		{
			assertEquals(0, children.size());
		}
		legalScopeManager.registerScope(globalScope);
		legalScopeManager.registerScope(subScope);
		//test getChildScopes
		children = legalScopeManager.getChildScopes(globalScope);
		assertEquals(1, children.size());
		assertEquals(subScope, children.get(0));
		//test independence of children list
		children.add(globalScope);
		// Ensure not saved in Library
		List<LegalScope> children2 = legalScopeManager.getChildScopes(globalScope);
		assertEquals(1, children2.size());
		assertEquals(2, children.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		legalScopeManager.registerScope(otherScope);
		assertEquals(1, children2.size());
		assertEquals(2, children.size());
		List<LegalScope> children3 = legalScopeManager.getChildScopes(globalScope);
		assertEquals(2, children3.size());
		assertTrue(children3.contains(subScope));
		assertTrue(children3.contains(otherScope));
	}


	@Test
	public void testGetLegalScopes()
	{
		Collection<LegalScope> legal = legalScopeManager.getLegalScopes();
		assertEquals(0, legal.size());
		legalScopeManager.registerScope(globalScope);
		legalScopeManager.registerScope(subScope);
		//test getChildScopes
		legal = legalScopeManager.getLegalScopes();
		assertEquals(2, legal.size());
		assertTrue(legal.contains(globalScope));
		assertTrue(legal.contains(subScope));
		//test independence of children list
		boolean canAdd;
		try
		{
			legal.add(otherScope);
			canAdd = true;
		}
		catch (UnsupportedOperationException e)
		{
			//ok too
			canAdd = false;
		}
		if (canAdd)
		{
			//Check this stuff only if we could add - otherwise dependence is ok
			// Ensure not saved in Library
			Collection<LegalScope> children2 = legalScopeManager.getLegalScopes();
			assertEquals(2, children2.size());
			assertEquals(3, legal.size());
			// And ensure references are not kept the other direction to be altered
			// by changes in the underlying DoubleKeyMap
			legalScopeManager.registerScope(otherScope);
			assertEquals(1, children2.size());
			assertEquals(2, legal.size());
			Collection<LegalScope> children3 = legalScopeManager.getLegalScopes();
			assertEquals(2, children3.size());
			assertTrue(children3.contains(subScope));
			assertTrue(children3.contains(otherScope));
		}
	}

	private class BadLegalScope1 implements LegalScope
	{

		@Override
		public Optional<LegalScope> getParentScope()
		{
			return Optional.empty();
		}

		@Override
		public String getName()
		{
			return null;
		}

	}

	private class BadLegalScope2 implements LegalScope
	{

		@Override
		public Optional<LegalScope> getParentScope()
		{
			return null;
		}

		@Override
		public String getName()
		{
			return "Something";
		}

	}
}
