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

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;

public class SimpleScopeInstanceFactoryTest
{

	private ScopeInstanceFactory factory;
	private ScopeManagerInst scopeManager;
	private ScopeInstance scopeInst;
	private SimpleDefinedScope local;

	@BeforeEach
	void setUp()
	{
		scopeManager = new ScopeManagerInst();
		factory = new SimpleScopeInstanceFactory(scopeManager);
		SimpleDefinedScope scope = new SimpleDefinedScope("Global");
		scopeManager.registerScope(scope);
		scopeInst = factory.getGlobalInstance("Global");
		local = new SimpleDefinedScope("Local");
		scopeManager.registerScope(scope, local);
	}
	
	@AfterEach
	void tearDown()
	{
		factory = null;
		scopeManager = null;
		scopeInst = null;
		local = null;
	}

	@Test
	public void testConstructor()
	{
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstanceFactory(null));
	}

	@Test
	public void testGetGlobalInstance()
	{
		assertThrows(IllegalArgumentException.class, () -> factory.getGlobalInstance("Local"));
		assertThrows(IllegalArgumentException.class, () -> factory.getGlobalInstance("Global.Local"));
		ScopeInstance globalInst = factory.getGlobalInstance("Global");
		assertTrue(globalInst.getParentScope().isEmpty());
		assertEquals("Global", globalInst.getImplementedScope().getName());
		assertEquals(scopeInst, globalInst);
	}

	@Test
	public void testGet()
	{
		ScopeInstance gsi = factory.get("Global", Optional.empty());
		assertTrue(gsi.getParentScope().isEmpty());
		assertEquals("Global", gsi.getImplementedScope().getName());

		/*
		 * This is subtle, but important.
		 * 
		 * The case above of "null" being the argument means that it is "truly"
		 * the global scope - it's global being requested as "known" global.
		 * 
		 * The case below represents a "local" object without a unique scope -
		 * so the only valid scope is the global scope. Therefore, it's
		 * requested with a VarScoped object, but we expect the returned value
		 * to still be the global scope. We can see that in the equals tests
		 * below of si and gsi.
		 */
		Scoped gvs = new Scoped("Var", null, null);
		ScopeInstance si = factory.get("Global", Optional.of(gvs));
		assertTrue(si.getParentScope().isEmpty());
		assertEquals("Global", si.getImplementedScope().getName());
		assertEquals(si, gsi);
		assertTrue(si == gsi);

		assertThrows(IllegalArgumentException.class, () -> factory.get("Local", Optional.of(gvs)));
		assertThrows(IllegalArgumentException.class, () -> factory.get("Local", null));
		Scoped lvs = new Scoped("LVar", "Global.Local", gvs);
		ScopeInstance lsi = factory.get("Global.Local", Optional.of(lvs));
		assertTrue(local.equals(lsi.getImplementedScope().getDefinedScope()));
		assertTrue(scopeInst.equals(lsi.getParentScope().get()));
		assertEquals("Local", lsi.getImplementedScope().getName());

		SimpleDefinedScope sublocal = new SimpleDefinedScope("SubLocal");
		scopeManager.registerScope(local, sublocal);
		Scoped slvs = new Scoped("SVar", "Global.Local.SubLocal", lvs);
		ScopeInstance slsi = factory.get("Global.Local.SubLocal", Optional.of(slvs));
		assertTrue(sublocal.equals(slsi.getImplementedScope().getDefinedScope()));
		assertTrue(scopeInst.equals(slsi.getParentScope().get().getParentScope().get()));
		assertEquals("SubLocal", slsi.getImplementedScope().getName());

	}

	public final class Scoped implements VarScoped
	{

		private final String name;
		private final String scopeName;
		private final VarScoped parent;

		public Scoped(String s, String scopeName, VarScoped parent)
		{
			name = s;
			this.scopeName = scopeName;
			this.parent = parent;
		}

		@Override
		public String getKeyName()
		{
			return name;
		}

		@Override
		public Optional<String> getLocalScopeName()
		{
			return Optional.ofNullable(scopeName);
		}

		@Override
		public Optional<VarScoped> getVariableParent()
		{
			return Optional.ofNullable(parent);
		}

	}
}
