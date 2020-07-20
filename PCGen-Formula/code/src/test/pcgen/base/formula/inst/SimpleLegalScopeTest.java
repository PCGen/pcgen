/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import org.junit.Test;

import junit.framework.TestCase;

public class SimpleLegalScopeTest extends TestCase
{

	private SimpleLegalScope scope;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scope = new SimpleLegalScope("Global");
	}

	@Test
	public void testDoubleConstructor()
	{
		try
		{
			scope = new SimpleLegalScope(scope, null);
			fail("null name must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testIsValid()
	{
		SimpleLegalScope local = new SimpleLegalScope(scope, "Local");
		assertTrue(local.getParentScope().isPresent());
		assertEquals(scope, local.getParentScope().get());
		assertEquals("Local", local.getName());
		assertEquals("Global.Local", local.toString());
	}
}
