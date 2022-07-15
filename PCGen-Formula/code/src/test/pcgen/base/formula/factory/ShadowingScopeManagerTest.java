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
package pcgen.base.formula.factory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ShadowingScopeManagerTest
{

	@Test
	public void testNullRegister()
	{
		ShadowingScopeManager scopeManager = new ShadowingScopeManager();
		assertThrows(NullPointerException.class, () -> scopeManager.registerGlobalScope(null));
	}

	@Test
	public void testBadRegister()
	{
		ShadowingScopeManager scopeManager = new ShadowingScopeManager();
		//Fail because globalScope is not yet registered
		assertThrows(IllegalArgumentException.class, () -> scopeManager.registerScope("Global", "SubScope"));
		scopeManager.registerGlobalScope("Global");
		//Should work now
		scopeManager.registerScope("Global", "SubScope");
		assertTrue(scopeManager.instantiate().isSuccessful());
	}
	
	@Test
	public void testDupeOkay()
	{
		ShadowingScopeManager scopeManager = new ShadowingScopeManager();
		scopeManager.registerGlobalScope("Global");
		scopeManager.registerScope("Global", "SubScope");
		//This is okay
		scopeManager.registerScope("Global", "SubScope");
		//Should work now
		assertTrue(scopeManager.instantiate().isSuccessful());
	}
}
