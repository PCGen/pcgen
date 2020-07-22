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
package pcgen.base.formula.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.inst.SimpleScopeInstanceFactory;

public class VariableIDTest
{

	private ScopeManagerInst legalScopeManager;
	private ScopeInstanceFactory instanceFactory;

	@BeforeEach
	void setUp()
	{
		legalScopeManager = new ScopeManagerInst();
		legalScopeManager.registerScope(new SimpleLegalScope("Global"));
		instanceFactory = new SimpleScopeInstanceFactory(legalScopeManager);
	}

	@AfterEach
	void tearDown()
	{
		legalScopeManager = null;
		instanceFactory = null;
	}

	@Test
	public void testDoubleConstructor()
	{
		assertThrows(NullPointerException.class, () -> new VariableID<>(null, null, null));
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		assertThrows(NullPointerException.class, () -> new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, null));
		assertThrows(NullPointerException.class, () -> new VariableID<>(globalInst, null, "VAR"));
		assertThrows(NullPointerException.class, () -> new VariableID<>(null, FormatUtilities.NUMBER_MANAGER, "VAR"));
		assertThrows(IllegalArgumentException.class, () -> new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, ""));
		assertThrows(IllegalArgumentException.class, () -> new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, " test"));
	}

	@Test
	public void testGlobal()
	{
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		VariableID<Number> vid = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		assertEquals("test", vid.getName());
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());
	}

	@Test
	public void testEquals()
	{
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		legalScopeManager.registerScope(new SimpleLegalScope("Global2"));
		ScopeInstance globalInst2 = instanceFactory.getGlobalInstance("Global2");
		VariableID<Number> vid1 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		VariableID<Number> vid2 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		VariableID<Number> vid3 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test2");
		VariableID<Number> vid4 = new VariableID<>(globalInst2, FormatUtilities.NUMBER_MANAGER, "test");
		assertFalse(vid1.equals(null));
		assertFalse(vid1.equals(new Object()));
		assertTrue(vid1.equals(vid1));
		assertTrue(vid1.equals(vid2));
		assertTrue(vid2.equals(vid1));
		assertFalse(vid1.equals(vid3));
		assertFalse(vid1.equals(vid4));
	}

	@Test
	public void testHashCode()
	{
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		legalScopeManager.registerScope(new SimpleLegalScope("Global2"));
		ScopeInstance globalInst2 = instanceFactory.getGlobalInstance("Global2");
		VariableID<Number> vid1 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		VariableID<Number> vid2 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "test");
		VariableID<Number> vid3 = new VariableID<>(globalInst, FormatUtilities.NUMBER_MANAGER, "bummer");
		VariableID<Number> vid4 = new VariableID<>(globalInst2, FormatUtilities.NUMBER_MANAGER, "test");
		int hc1 = vid1.hashCode();
		int hc2 = vid2.hashCode();
		int hc3 = vid3.hashCode();
		int hc4 = vid4.hashCode();
		assertTrue(hc1 == hc2);
		assertFalse(hc2 == hc3);
		assertFalse(hc2 == hc4);
		assertFalse(hc3 == hc4);
	}

}
