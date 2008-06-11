package pcgen.base.lang;

import junit.framework.TestCase;

import org.junit.Test;

public class CaseInsensitiveStringTest extends TestCase
{

	@Test
	public void testNullConstructor()
	{
		try
		{
			new CaseInsensitiveString(null);
			fail("Expected CaseInsensitiveString to reject null argument in constructor");
		}
		catch (IllegalArgumentException e)
		{
			//OK
		}
	}
	
	@Test
	public void testIdentical()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	public void testMixedCase()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("FooGoo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("fOoGOO");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	public void testMixedCaseNotFirstLetter()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("FoO");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo");
		assertTrue(cis1.equals(cis2));
		assertTrue(cis2.equals(cis1));
		assertEquals(cis1.hashCode(), cis2.hashCode());
	}

	@Test
	public void testSpace()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo ");
		assertFalse(cis1.equals(cis2));
		assertFalse(cis2.equals(cis1));
	}

	@Test
	public void testDifferent()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foe");
		assertFalse(cis1.equals(cis2));
		assertFalse(cis2.equals(cis1));
	}

	@Test
	public void testString()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		assertFalse(cis1.equals("Foo"));
	}

	@Test
	public void testToString()
	{
		CaseInsensitiveString cis1 = new CaseInsensitiveString("Foo");
		assertEquals("Foo", cis1.toString());
		CaseInsensitiveString cis2 = new CaseInsensitiveString("Foo ");
		assertEquals("Foo ", cis2.toString());
	}
}
