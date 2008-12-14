package pcgen.cdom.enumeration;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;


public class TypeTest  extends TestCase
{
	
	@Test
	public void TestSortable()
	{
		try
		{
			Set<Type> typeset = new HashSet<Type>();
			typeset.add(Type.getConstant("testitem 1"));
			typeset.add(Type.getConstant("testitem 2"));
		}
		catch(ClassCastException cce)
		{
			fail();
		}
		
	}

}
