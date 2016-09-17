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

public class SimpleScopeInstanceTest extends TestCase
{

	private SimpleLegalScope scope;
	private SimpleScopeInstance scopeInst;
	private SimpleLegalScope local;
	private SimpleScopeInstance localInst;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scope = new SimpleLegalScope(null, "Global");
		scopeInst = new SimpleScopeInstance(null, scope);
		local = new SimpleLegalScope(scope, "Local");
		localInst = new SimpleScopeInstance(scopeInst, local);
	}

	@Test
	public void testConstructor()
	{
		try
		{
			new SimpleScopeInstance(scopeInst, null);
			fail("null scope must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(scopeInst, scope);
			fail("mismatch of inst, built scope must be rejected (scope parent == null)");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(localInst, local);
			fail("mismatch of inst, built scope must be rejected (neither parent null)");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(null, local);
			fail("non global scope without parent instance must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		SimpleLegalScope sublocal = new SimpleLegalScope(local, "SubLocal");
		SimpleScopeInstance globalInst = new SimpleScopeInstance(null, scope);
		try
		{
			new SimpleScopeInstance(null, local);
			fail("Instance should require a parent if not global");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		SimpleScopeInstance localInst = new SimpleScopeInstance(globalInst, local);
		assertEquals(globalInst, localInst.getParentScope());
		assertEquals(local, localInst.getLegalScope());
		try
		{
			new SimpleScopeInstance(globalInst, null);
			fail("LegalScope cannot be null");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(globalInst, sublocal);
			fail("LegalScope must be a direct child of the scope of the provided instance");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testIsValid()
	{
		assertEquals(local, localInst.getLegalScope());
		assertEquals(scopeInst, localInst.getParentScope());
	}

}
