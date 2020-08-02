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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pcgen.base.formula.base.DefinedScope;

public class ScopeManagerInstTest
{

	@Test
	public void testNullRegister()
	{
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		assertThrows(NullPointerException.class, () -> scopeManager.registerScope(null));
	}

	@Test
	public void testBadRegister()
	{
		SimpleDefinedScope globalScope = new SimpleDefinedScope("Global");
		SimpleDefinedScope subScope = new SimpleDefinedScope("SubScope");
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		assertThrows(NullPointerException.class, () -> scopeManager.registerScope(new BadLegalScope1()));
		assertThrows(IllegalArgumentException.class, () -> scopeManager.registerScope(globalScope, subScope));
		scopeManager.registerScope(globalScope);
		scopeManager.registerScope(globalScope, subScope);
		assertThrows(IllegalArgumentException.class, () -> scopeManager.registerScope(globalScope, new SimpleDefinedScope("SubScope")));
		assertThrows(IllegalArgumentException.class, () -> scopeManager.registerScope(globalScope, new SimpleDefinedScope("Sub.Scope")));
	}
	
	@Test
	public void testDupeOkay()
	{
		SimpleDefinedScope globalScope = new SimpleDefinedScope("Global");
		SimpleDefinedScope subScope = new SimpleDefinedScope("SubScope");
		ScopeManagerInst scopeManager = new ScopeManagerInst();
		scopeManager.registerScope(globalScope);
		scopeManager.registerScope(globalScope, subScope);
		//This is okay
		scopeManager.registerScope(globalScope, subScope);
	}

	private class BadLegalScope1 implements DefinedScope
	{
		@Override
		public String getName()
		{
			return null;
		}

	}
}
