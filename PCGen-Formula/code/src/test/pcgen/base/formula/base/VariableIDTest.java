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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.inst.SimpleLegalScope;
import pcgen.base.formula.inst.SimpleScopeInstance;

public class VariableIDTest extends TestCase
{

	NumberManager numberManager = FormatUtilities.NUMBER_MANAGER;

	@Test
	public void testDoubleConstructor()
	{
		try
		{
			new VariableID(null, null, null);
			fail("nulls must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = new SimpleScopeInstance(null, varScope);
		try
		{
			new VariableID(globalInst, numberManager, null);
			fail("null name must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		try
		{
			new VariableID(globalInst, null, "VAR");
			fail("null FormatManager must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		try
		{
			new VariableID(null, numberManager, "VAR");
			fail("null scope must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		try
		{
			new VariableID(globalInst, numberManager, "");
			fail("empty name must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		try
		{
			new VariableID(globalInst, numberManager, " test");
			fail("padded name must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
	}

	public void testGlobal()
	{
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = new SimpleScopeInstance(null, varScope);
		VariableID vid = new VariableID(globalInst, numberManager, "test");
		assertEquals("test", vid.getName());
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());
	}

	public void testEquals()
	{
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = new SimpleScopeInstance(null, varScope);
		ScopeInstance globalInst2 = new SimpleScopeInstance(null, varScope);
		VariableID vid1 = new VariableID(globalInst, numberManager, "test");
		VariableID vid2 = new VariableID(globalInst, numberManager, "test");
		VariableID vid3 = new VariableID(globalInst, numberManager, "test2");
		VariableID vid4 = new VariableID(globalInst2, numberManager, "test");
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
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = new SimpleScopeInstance(null, varScope);
		ScopeInstance globalInst2 = new SimpleScopeInstance(null, varScope);
		VariableID vid1 = new VariableID(globalInst, numberManager, "test");
		VariableID vid2 = new VariableID(globalInst, numberManager, "test");
		VariableID vid3 = new VariableID(globalInst, numberManager, "bummer");
		VariableID vid4 = new VariableID(globalInst2, numberManager, "test");
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
