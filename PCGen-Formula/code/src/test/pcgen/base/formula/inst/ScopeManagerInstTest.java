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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formula.base.ImplementedScope;

public class ScopeManagerInstTest
{

	private SimpleLegalScope globalScope = new SimpleLegalScope("Global");
	private SimpleLegalScope subScope = new SimpleLegalScope(globalScope, "SubScope");
	private SimpleLegalScope otherScope = new SimpleLegalScope(globalScope, "OtherScope");

	@Test
	public void testNullRegister()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		assertThrows(NullPointerException.class, () -> scopeManager.registerScope(null));
	}

	@Test
	public void testBadRegister()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		assertThrows(NullPointerException.class, () -> scopeManager.registerScope(new BadLegalScope1()));
		assertThrows(NullPointerException.class, () -> scopeManager.registerScope(new BadLegalScope2()));
		assertThrows(IllegalArgumentException.class, () -> scopeManager.registerScope(subScope));
		scopeManager.registerScope(globalScope);
		scopeManager.registerScope(subScope);
		assertThrows(IllegalArgumentException.class, () -> scopeManager.registerScope(new SimpleLegalScope(globalScope, "SubScope")));
		assertThrows(IllegalArgumentException.class, () -> scopeManager.registerScope(new SimpleLegalScope(globalScope, "Sub.Scope")));
	}
	
	@Test
	public void testDupeOkay()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		scopeManager.registerScope(globalScope);
		scopeManager.registerScope(subScope);
		//This is okay
		scopeManager.registerScope(subScope);
	}

	@Test
	public void testNullGet()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		assertThrows(NullPointerException.class, () -> scopeManager.getChildScopes(null));
	}

	@Test
	public void testGetScope()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		List<ImplementedScope> children = scopeManager.getChildScopes(globalScope);
		if (children != null)
		{
			assertEquals(0, children.size());
		}
		scopeManager.registerScope(globalScope);
		scopeManager.registerScope(subScope);
		//test getScope
		assertEquals(globalScope, scopeManager.getImplementedScope("Global"));
		assertEquals(subScope, scopeManager.getImplementedScope("Global.SubScope"));
		assert(scopeManager.getImplementedScope("OtherScope") == null);
		assert(scopeManager.getImplementedScope(null) == null);
	}

	@Test
	public void testGetChildScopes()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		List<ImplementedScope> children = scopeManager.getChildScopes(globalScope);
		if (children != null)
		{
			assertEquals(0, children.size());
		}
		scopeManager.registerScope(globalScope);
		scopeManager.registerScope(subScope);
		//test getChildScopes
		children = scopeManager.getChildScopes(globalScope);
		assertEquals(1, children.size());
		assertEquals(subScope, children.get(0));
		//test independence of children list
		children.add(globalScope);
		// Ensure not saved in Library
		List<ImplementedScope> children2 = scopeManager.getChildScopes(globalScope);
		assertEquals(1, children2.size());
		assertEquals(2, children.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		scopeManager.registerScope(otherScope);
		assertEquals(1, children2.size());
		assertEquals(2, children.size());
		List<ImplementedScope> children3 = scopeManager.getChildScopes(globalScope);
		assertEquals(2, children3.size());
		assertTrue(children3.contains(subScope));
		assertTrue(children3.contains(otherScope));
	}


	@Test
	public void testGetImplementedScopes()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		Collection<ImplementedScope> legal = scopeManager.getImplementedScopes();
		assertEquals(0, legal.size());
		scopeManager.registerScope(globalScope);
		scopeManager.registerScope(subScope);
		//test getChildScopes
		legal = scopeManager.getImplementedScopes();
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
			Collection<ImplementedScope> children2 = scopeManager.getImplementedScopes();
			assertEquals(2, children2.size());
			assertEquals(3, legal.size());
			// And ensure references are not kept the other direction to be altered
			// by changes in the underlying DoubleKeyMap
			scopeManager.registerScope(otherScope);
			assertEquals(1, children2.size());
			assertEquals(2, legal.size());
			Collection<ImplementedScope> children3 = scopeManager.getImplementedScopes();
			assertEquals(2, children3.size());
			assertTrue(children3.contains(subScope));
			assertTrue(children3.contains(otherScope));
		}
	}

	private class BadLegalScope1 implements ImplementedScope
	{

		@Override
		public Optional<ImplementedScope> getParentScope()
		{
			return Optional.empty();
		}

		@Override
		public String getName()
		{
			return null;
		}

	}

	private class BadLegalScope2 implements ImplementedScope
	{

		@Override
		public Optional<ImplementedScope> getParentScope()
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
