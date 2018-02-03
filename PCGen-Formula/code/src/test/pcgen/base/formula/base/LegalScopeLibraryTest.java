package pcgen.base.formula.base;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.formula.inst.SimpleLegalScope;

public class LegalScopeLibraryTest extends TestCase
{

	private LegalScopeLibrary library;
	SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
	SimpleLegalScope subScope = new SimpleLegalScope(globalScope, "SubScope");
	SimpleLegalScope otherScope = new SimpleLegalScope(globalScope, "OtherScope");

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		library = new LegalScopeLibrary();
	}

	@Test
	public void testNullRegister()
	{
		try
		{
			library.registerScope(null);
			fail("null must be rejected in registerScope");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testBadRegister()
	{
		try
		{
			library.registerScope(new BadLegalScope());
			fail("null name must be rejected in registerScope");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
		library.registerScope(subScope);
		try
		{
			library.registerScope(new SimpleLegalScope(globalScope, "SubScope"));
			fail("dupe name be rejected in registerScope");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		//But this is okay
		library.registerScope(subScope);
	}

	@Test
	public void testNullGet()
	{
		try
		{
			library.getChildScopes(null);
			fail("null must be rejected in getChildScopes");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//ok
		}
	}

	@Test
	public void testGetScope()
	{
		List<LegalScope> children = library.getChildScopes(globalScope);
		if (children != null)
		{
			assertEquals(0, children.size());
		}
		//Note order is significant - child first to check it works
		library.registerScope(subScope);
		library.registerScope(globalScope);
		//test getScope
		assertEquals(globalScope, library.getScope("Global"));
		assertEquals(subScope, library.getScope("Global.SubScope"));
		assert(library.getScope("OtherScope") == null);
		assert(library.getScope(null) == null);
	}

	@Test
	public void testGetChildScopes()
	{
		List<LegalScope> children = library.getChildScopes(globalScope);
		if (children != null)
		{
			assertEquals(0, children.size());
		}
		//Note order is significant - child first to check it works
		library.registerScope(subScope);
		library.registerScope(globalScope);
		//test getChildScopes
		children = library.getChildScopes(globalScope);
		assertEquals(1, children.size());
		assertEquals(subScope, children.get(0));
		//test independence of children list
		children.add(globalScope);
		// Ensure not saved in Library
		List<LegalScope> children2 = library.getChildScopes(globalScope);
		assertEquals(1, children2.size());
		assertEquals(2, children.size());
		// And ensure references are not kept the other direction to be altered
		// by changes in the underlying DoubleKeyMap
		library.registerScope(otherScope);
		assertEquals(1, children2.size());
		assertEquals(2, children.size());
		List<LegalScope> children3 = library.getChildScopes(globalScope);
		assertEquals(2, children3.size());
		assertTrue(children3.contains(subScope));
		assertTrue(children3.contains(otherScope));
	}


	@Test
	public void testGetLegalScopes()
	{
		Collection<LegalScope> legal = library.getLegalScopes();
		assertEquals(0, legal.size());
		//Note order is significant - child first to check it works
		library.registerScope(subScope);
		library.registerScope(globalScope);
		//test getChildScopes
		legal = library.getLegalScopes();
		assertEquals(2, legal.size());
		assertTrue(legal.contains(globalScope));
		assertTrue(legal.contains(subScope));
		//test independence of children list
		boolean canAdd;
		try
		{
			legal.add(otherScope);
			canAdd = true;
		}
		catch (UnsupportedOperationException e)
		{
			//ok too
			canAdd = false;
		}
		if (canAdd)
		{
			//Check this stuff only if we could add - otherwise dependence is ok
			// Ensure not saved in Library
			Collection<LegalScope> children2 = library.getLegalScopes();
			assertEquals(2, children2.size());
			assertEquals(3, legal.size());
			// And ensure references are not kept the other direction to be altered
			// by changes in the underlying DoubleKeyMap
			library.registerScope(otherScope);
			assertEquals(1, children2.size());
			assertEquals(2, legal.size());
			Collection<LegalScope> children3 = library.getLegalScopes();
			assertEquals(2, children3.size());
			assertTrue(children3.contains(subScope));
			assertTrue(children3.contains(otherScope));
		}
	}

	private class BadLegalScope implements LegalScope
	{

		@Override
		public LegalScope getParentScope()
		{
			return null;
		}

		@Override
		public String getName()
		{
			return null;
		}

	}

}
