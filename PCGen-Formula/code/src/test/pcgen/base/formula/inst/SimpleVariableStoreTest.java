/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;

public class SimpleVariableStoreTest
{
	@Test
	public void testNulls()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		scopeManager.registerScope(new SimpleLegalScope("Global"));
		ScopeInstanceFactory instanceFactory =
				new SimpleScopeInstanceFactory(scopeManager);
		SimpleVariableStore varStore = new SimpleVariableStore();
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		VariableID<Number> vid = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		assertThrows(NullPointerException.class, () -> varStore.put(null, Integer.valueOf(4)));
		assertThrows(NullPointerException.class, () -> varStore.put(vid, null));
	}

	@Test
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void testGenericsViolation()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		scopeManager.registerScope(new SimpleLegalScope("Global"));
		ScopeInstanceFactory instanceFactory =
				new SimpleScopeInstanceFactory(scopeManager);
		SimpleVariableStore varStore = new SimpleVariableStore();
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		VariableID vid = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		//Intentionally break generics
		assertThrows(IllegalArgumentException.class, () -> varStore.put(vid, "NotANumber!"));
	}

	@Test
	public void testGlobal()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		scopeManager.registerScope(new SimpleLegalScope("Global"));
		ScopeInstanceFactory instanceFactory =
				new SimpleScopeInstanceFactory(scopeManager);
		SimpleVariableStore varStore = new SimpleVariableStore();
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		VariableID<Number> vid = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		assertFalse(varStore.containsVariable(vid));
		assertNull(varStore.put(vid, Integer.valueOf(9)));
		assertTrue(varStore.containsVariable(vid));
		assertEquals(Integer.valueOf(9), varStore.get(vid));
		assertEquals(Integer.valueOf(9), varStore.put(vid, Integer.valueOf(4)));
		assertTrue(varStore.containsVariable(vid));
		assertEquals(Integer.valueOf(4), varStore.get(vid));
	}

	@Test
	public void testIndependence()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		scopeManager.registerScope(new SimpleLegalScope("Global"));
		ScopeInstanceFactory instanceFactory =
				new SimpleScopeInstanceFactory(scopeManager);
		SimpleVariableStore varStore = new SimpleVariableStore();
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		VariableID<Number> vid1 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		VariableID<Number> vid2 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		VariableID<Number> vid3 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test2");
		scopeManager.registerScope(new SimpleLegalScope("Global2"));
		ScopeInstance globalInst2 = instanceFactory.getGlobalInstance("Global2");
		VariableID<Number> vid4 = new VariableID<>(globalInst2, FormatUtilities.NUMBER_MANAGER, "test");
		assertNull(varStore.put(vid1, Integer.valueOf(9)));
		assertTrue(varStore.containsVariable(vid1));
		assertTrue(varStore.containsVariable(vid2));
		assertFalse(varStore.containsVariable(vid3));
		assertFalse(varStore.containsVariable(vid4));
		assertEquals(Integer.valueOf(9), varStore.put(vid2, Integer.valueOf(4)));
		assertTrue(varStore.containsVariable(vid1));
		assertTrue(varStore.containsVariable(vid2));
		assertFalse(varStore.containsVariable(vid3));
		assertFalse(varStore.containsVariable(vid4));
		assertEquals(Integer.valueOf(4), varStore.get(vid1));
		assertNull(varStore.put(vid4, Integer.valueOf(3)));
		assertTrue(varStore.containsVariable(vid1));
		assertTrue(varStore.containsVariable(vid2));
		assertFalse(varStore.containsVariable(vid3));
		assertTrue(varStore.containsVariable(vid4));
		assertEquals(Integer.valueOf(4), varStore.get(vid1));
		assertEquals(Integer.valueOf(3), varStore.get(vid4));
	}

}
