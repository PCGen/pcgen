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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.testsupport.GlobalVarScoped;
import pcgen.base.testsupport.NaiveScopeManager;
import pcgen.base.testsupport.SimpleVarScoped;

public class SimpleScopeInstanceFactoryTest
{

	private ScopeInstanceFactory factory;
	private NaiveScopeManager scopeManager;
	private ScopeInstance scopeInst;
	private GlobalVarScoped gvs;

	@BeforeEach
	void setUp()
	{
		scopeManager = new NaiveScopeManager();
		factory = new SimpleScopeInstanceFactory(scopeManager);
		gvs = new GlobalVarScoped("Global");
		scopeInst = factory.get("Global", gvs);
		scopeManager.registerScope("Global", "Local");
	}
	
	@AfterEach
	void tearDown()
	{
		factory = null;
		scopeManager = null;
		scopeInst = null;
		gvs = null;
	}

	@Test
	public void testConstructor()
	{
		assertThrows(NullPointerException.class, () -> new SimpleScopeInstanceFactory(null));
	}

	@Test
	public void testGetGlobalInstance()
	{
		ScopeInstance globalInst = factory.get("Global", gvs);
		assertTrue(globalInst.getImplementedScope().drawsFrom().isEmpty());
		assertEquals("Global", globalInst.getImplementedScope().getName());
		assertEquals(scopeInst, globalInst);
	}

	@Test
	public void testGet()
	{
		ScopeInstance gsi = factory.get("Global", gvs);
		assertTrue(gsi.getImplementedScope().drawsFrom().isEmpty());
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
		VarScoped nvs = new SimpleVarScoped("NVar", gvs, null);
		ScopeInstance si = factory.get("Global", nvs);
		assertTrue(si.getImplementedScope().drawsFrom().isEmpty());
		assertEquals("Global", si.getImplementedScope().getName());
		assertEquals(si, gsi);
		assertTrue(si == gsi);

		VarScoped lvs = new SimpleVarScoped("LVar", gvs, "Global.Local");
		ScopeInstance lsi = factory.get("Global.Local", lvs);
		assertTrue("Global.Local".equals(lsi.getImplementedScope().getName()));
		assertEquals("Global.Local", lsi.getImplementedScope().getName());

		scopeManager.registerScope("Local", "SubLocal");
		VarScoped slvs = new SimpleVarScoped("SVar", lvs, "Global.Local.SubLocal");
		ScopeInstance slsi = factory.get("Global.Local.SubLocal", slvs);
		assertTrue("Global.Local.SubLocal".equals(slsi.getImplementedScope().getName()));
		assertEquals("Global.Local.SubLocal", slsi.getImplementedScope().getName());
	}
}
