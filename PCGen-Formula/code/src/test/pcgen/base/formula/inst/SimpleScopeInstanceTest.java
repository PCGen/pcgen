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

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class SimpleScopeInstanceTest
{
	@Test
	public void testConstructor()
	{
		SimpleDefinedScope scope = new SimpleDefinedScope("Global");
		SimpleImplementedScope implementedScope = new SimpleImplementedScope(scope);
		SimpleScopeInstance scopeInst = new SimpleScopeInstance(
			Optional.empty(), implementedScope, new GlobalVarScoped("Global"));
		SimpleDefinedScope local = new SimpleDefinedScope("Local");
		SimpleImplementedScope localImplementedScope =
				new SimpleImplementedScope(implementedScope, local);
		SimpleScopeInstance localInst =
				new SimpleScopeInstance(Optional.of(scopeInst),
					localImplementedScope, new GlobalVarScoped("Local"));

		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), null, new GlobalVarScoped("Global")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), implementedScope, new GlobalVarScoped("Ignored")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.of(localInst), localImplementedScope, new GlobalVarScoped("Ignored")));
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(null, localImplementedScope, new GlobalVarScoped("Ignored")));
		SimpleDefinedScope sublocal = new SimpleDefinedScope("SubLocal");
		SimpleImplementedScope sublocalImplementedScope = new SimpleImplementedScope(localImplementedScope, sublocal);
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(null, localImplementedScope, new GlobalVarScoped("Ignored")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.empty(), localImplementedScope, new GlobalVarScoped("Ignored")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.empty(), localImplementedScope, new GlobalVarScoped("Ignored")));
		assertEquals(scopeInst, localInst.getParentScope().get());
		assertEquals(localImplementedScope, localInst.getImplementedScope());
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), null, new GlobalVarScoped("Ignored")));
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), localImplementedScope, null));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), sublocalImplementedScope, new GlobalVarScoped("Ignored")));
	}

	@Test
	public void testIsValid()
	{
		SimpleDefinedScope scope = new SimpleDefinedScope("Global");
		SimpleImplementedScope implementedScope = new SimpleImplementedScope(scope);
		SimpleScopeInstance scopeInst = new SimpleScopeInstance(
			Optional.empty(), implementedScope, new GlobalVarScoped("Global"));
		SimpleDefinedScope local = new SimpleDefinedScope("Local");
		SimpleImplementedScope localImplementedScope =
				new SimpleImplementedScope(implementedScope, local);
		SimpleScopeInstance localInst =
				new SimpleScopeInstance(Optional.of(scopeInst),
					localImplementedScope, new GlobalVarScoped("Local"));

		assertEquals(localImplementedScope, localInst.getImplementedScope());
		assertEquals(scopeInst, localInst.getParentScope().get());
	}

}
