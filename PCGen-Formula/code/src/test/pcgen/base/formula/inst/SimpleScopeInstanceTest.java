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
		SimpleLegalScope scope = new SimpleLegalScope("Global");
		SimpleScopeInstance scopeInst = new SimpleScopeInstance(
			Optional.empty(), scope, new GlobalVarScoped("Global"));
		SimpleLegalScope local = new SimpleLegalScope(scope, "Local");
		SimpleScopeInstance localInst = new SimpleScopeInstance(
			Optional.of(scopeInst), local, new GlobalVarScoped("Local"));

		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), null, new GlobalVarScoped("Global")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), scope, new GlobalVarScoped("Ignored")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.of(localInst), local, new GlobalVarScoped("Ignored")));
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(null, local, new GlobalVarScoped("Ignored")));
		SimpleLegalScope sublocal = new SimpleLegalScope(local, "SubLocal");
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(null, local, new GlobalVarScoped("Ignored")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.empty(), local, new GlobalVarScoped("Ignored")));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.empty(), local, new GlobalVarScoped("Ignored")));
		assertEquals(scopeInst, localInst.getParentScope().get());
		assertEquals(local, localInst.getLegalScope());
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), null, new GlobalVarScoped("Ignored")));
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), local, null));
		assertThrows(IllegalArgumentException.class, () -> new SimpleScopeInstance(Optional.of(scopeInst), sublocal, new GlobalVarScoped("Ignored")));
	}

	@Test
	public void testIsValid()
	{
		SimpleLegalScope scope = new SimpleLegalScope("Global");
		SimpleScopeInstance scopeInst = new SimpleScopeInstance(
			Optional.empty(), scope, new GlobalVarScoped("Global"));
		SimpleLegalScope local = new SimpleLegalScope(scope, "Local");
		SimpleScopeInstance localInst = new SimpleScopeInstance(
			Optional.of(scopeInst), local, new GlobalVarScoped("Local"));

		assertEquals(local, localInst.getLegalScope());
		assertEquals(scopeInst, localInst.getParentScope().get());
	}

}
