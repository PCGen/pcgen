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

import java.util.Optional;

import org.junit.Test;

import junit.framework.TestCase;

public class SimpleScopeInstanceTest extends TestCase
{

	private static final GlobalVarScoped LOCAL_VS = new GlobalVarScoped("Local");
	private static final GlobalVarScoped GLOBAL_VS = new GlobalVarScoped("Global");

	private SimpleLegalScope scope;
	private SimpleScopeInstance scopeInst;
	private SimpleLegalScope local;
	private SimpleScopeInstance localInst;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scope = new SimpleLegalScope("Global");
		scopeInst = new SimpleScopeInstance(Optional.empty(), scope, GLOBAL_VS);
		local = new SimpleLegalScope(scope, "Local");
		localInst = new SimpleScopeInstance(Optional.of(scopeInst), local, LOCAL_VS);
	}

	@SuppressWarnings("unused")
	@Test
	public void testConstructor()
	{
		try
		{
			new SimpleScopeInstance(Optional.of(scopeInst), null, GLOBAL_VS);
			fail("null scope must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(Optional.of(scopeInst), scope, new GlobalVarScoped("Ignored"));
			fail("mismatch of inst, built scope must be rejected (scope parent == null)");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(Optional.of(localInst), local, new GlobalVarScoped("Ignored"));
			fail("mismatch of inst, built scope must be rejected (neither parent null)");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(null, local, new GlobalVarScoped("Ignored"));
			fail("non global scope without parent instance must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		SimpleLegalScope sublocal = new SimpleLegalScope(local, "SubLocal");
		try
		{
			new SimpleScopeInstance(null, local, new GlobalVarScoped("Ignored"));
			fail("Instance should require a parent if not global");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(Optional.empty(), local, new GlobalVarScoped("Ignored"));
			fail("non global scope without parent instance must be rejected");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(Optional.empty(), local, new GlobalVarScoped("Ignored"));
			fail("Instance should require a parent if not global");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		assertEquals(scopeInst, localInst.getParentScope().get());
		assertEquals(local, localInst.getLegalScope());
		try
		{
			new SimpleScopeInstance(Optional.of(scopeInst), null, new GlobalVarScoped("Ignored"));
			fail("LegalScope cannot be null");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(Optional.of(scopeInst), local, null);
			fail("Description cannot be null");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		try
		{
			new SimpleScopeInstance(Optional.of(scopeInst), sublocal, new GlobalVarScoped("Ignored"));
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
		assertEquals(scopeInst, localInst.getParentScope().get());
	}

}
