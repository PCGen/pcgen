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

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.inst.SimpleScopeInstanceFactory;

public class VariableIDTest extends TestCase
{

	private NumberManager numberManager = FormatUtilities.NUMBER_MANAGER;
	private ScopeManagerInst legalScopeManager;
	private ScopeInstanceFactory instanceFactory;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		legalScopeManager = new ScopeManagerInst();
		legalScopeManager.registerScope(new SimpleLegalScope("Global"));
		instanceFactory = new SimpleScopeInstanceFactory(legalScopeManager);
	}

	@SuppressWarnings("unused")
	@Test
	public void testDoubleConstructor()
	{
		try
		{
			new VariableID<>(null, null, null);
			fail("nulls must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		try
		{
			new VariableID<>(globalInst, numberManager, null);
			fail("null name must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new VariableID<>(globalInst, null, "VAR");
			fail("null FormatManager must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new VariableID<>(null, numberManager, "VAR");
			fail("null scope must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new VariableID<>(globalInst, numberManager, "");
			fail("empty name must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new VariableID<>(globalInst, numberManager, " test");
			fail("padded name must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testGlobal()
	{
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		VariableID<Number> vid = new VariableID<>(globalInst, numberManager, "test");
		assertEquals("test", vid.getName());
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());
	}

	public void testEquals()
	{
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		legalScopeManager.registerScope(new SimpleLegalScope("Global2"));
		ScopeInstance globalInst2 = instanceFactory.getGlobalInstance("Global2");
		VariableID<Number> vid1 = new VariableID<>(globalInst, numberManager, "test");
		VariableID<Number> vid2 = new VariableID<>(globalInst, numberManager, "test");
		VariableID<Number> vid3 = new VariableID<>(globalInst, numberManager, "test2");
		VariableID<Number> vid4 = new VariableID<>(globalInst2, numberManager, "test");
		assertFalse(vid1.equals(null));
		assertFalse(vid1.equals(new Object()));
		assertTrue(vid1.equals(vid1));
		assertTrue(vid1.equals(vid2));
		assertTrue(vid2.equals(vid1));
		assertFalse(vid1.equals(vid3));
		assertFalse(vid1.equals(vid4));
	}

	public void testHashCode()
	{
		ScopeInstance globalInst = instanceFactory.getGlobalInstance("Global");
		legalScopeManager.registerScope(new SimpleLegalScope("Global2"));
		ScopeInstance globalInst2 = instanceFactory.getGlobalInstance("Global2");
		VariableID<Number> vid1 = new VariableID<>(globalInst, numberManager, "test");
		VariableID<Number> vid2 = new VariableID<>(globalInst, numberManager, "test");
		VariableID<Number> vid3 = new VariableID<>(globalInst, numberManager, "bummer");
		VariableID<Number> vid4 = new VariableID<>(globalInst2, numberManager, "test");
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
