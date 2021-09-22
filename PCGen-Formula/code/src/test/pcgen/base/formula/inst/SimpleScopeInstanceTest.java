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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.testsupport.GlobalVarScoped;
import pcgen.base.testsupport.SimpleVarScoped;

public class SimpleScopeInstanceTest
{
	@Test
	public void testConstructor()
	{
		GlobalVarScoped gvs = new GlobalVarScoped("Global");
		SimpleImplementedScope implementedScope = new SimpleImplementedScope("Global", true);
		SimpleImplementedScope localImplementedScope =
				new SimpleImplementedScope("Global.Local", false);
		localImplementedScope.drawsFrom(implementedScope);
		SimpleScopeInstance localInst = new SimpleScopeInstance(
			localImplementedScope, new SimpleVarScoped("VS", gvs, "Global.Local"));

		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(null, new GlobalVarScoped("Global")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(implementedScope, new GlobalVarScoped("Ignored")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(localImplementedScope, new GlobalVarScoped("Ignored")));
		SimpleImplementedScope sublocalImplementedScope = new SimpleImplementedScope("Global.Local.SubLocal", false);
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(localImplementedScope, new GlobalVarScoped("Ignored")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(localImplementedScope, new GlobalVarScoped("Ignored")));
		assertTrue(localInst.getImplementedScope().drawsFrom().contains(implementedScope));
		assertEquals(localImplementedScope, localInst.getImplementedScope());
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(null, new GlobalVarScoped("Ignored")));
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(localImplementedScope, null));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(sublocalImplementedScope, new GlobalVarScoped("Ignored")));
	}

	@Test
	public void testIsValid()
	{
		GlobalVarScoped gvs = new GlobalVarScoped("Global");
		SimpleImplementedScope implementedScope = new SimpleImplementedScope("Global", true);
		SimpleImplementedScope localImplementedScope =
				new SimpleImplementedScope("Global.Local", false);
		localImplementedScope.drawsFrom(implementedScope);
		SimpleScopeInstance localInst = new SimpleScopeInstance(
			localImplementedScope, new SimpleVarScoped("VS", gvs, "Global.Local"));

		assertEquals(localImplementedScope, localInst.getImplementedScope());
		assertTrue(localInst.getImplementedScope().drawsFrom().contains(implementedScope));
	}

}
