/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;

public class SimpleFunctionLibraryTest extends TestCase
{
	private SimpleFunctionLibrary library;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		library = new SimpleFunctionLibrary();
	}

	@Test
	public void testInvalidNull()
	{
		try
		{
			library.addFunction(null);
			fail("Expected null function to be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//Yep
		}
	}

	@Test
	public void testInvalidNullName()
	{
		try
		{
			Function f = getPseudoFunction(null);
			library.addFunction(f);
			fail("Expected function with null name to be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//Yep
		}
	}

	@Test
	public void testSimpleFunctionSetGet()
	{
		Function abs = getPseudoFunction("Abs");
		library.addFunction(abs);
		//case insensitive
		assertEquals(abs, library.getFunction("ABS"));
		assertEquals(abs, library.getFunction("Abs"));
		assertEquals(abs, library.getFunction("abs"));
	}

	@Test
	public void testInvalidDupeNameSimpleFunction()
	{
		Function abs = getPseudoFunction("Abs");
		library.addFunction(abs);
		try
		{
			Function pseudoFunction = getPseudoFunction("ABS");
			library.addFunction(pseudoFunction);
			fail("Should not have been able to add function twice");
		}
		catch (IllegalArgumentException e)
		{
			//Yep, but make sure it didn't overwrite
			assertEquals(abs, library.getFunction("ABS"));
		}
		try
		{
			Function pseudoFunction = getPseudoFunction("abs");
			library.addFunction(pseudoFunction);
			fail("Should not have been able to add function twice, regardless of case");
		}
		catch (IllegalArgumentException e)
		{
			//Yep, but make sure it didn't overwrite
			assertEquals(abs, library.getFunction("ABS"));
		}
	}

	@Test
	public void testInvalidBracketNull()
	{
		try
		{
			library.addBracketFunction(null);
			fail("Expected null function to be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//Yep
		}
	}

	@Test
	public void testInvalidBracketNullName()
	{
		try
		{
			Function f = getPseudoFunction(null);
			library.addBracketFunction(f);
			fail("Expected function with null name to be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//Yep
		}
	}

	@Test
	public void testBracketFunctionSetGet()
	{
		Function count = getPseudoFunction("Count");
		library.addBracketFunction(count);
		//case insensitive
		assertEquals(count, library.getBracketFunction("COUNT"));
		assertEquals(count, library.getBracketFunction("Count"));
		assertEquals(count, library.getBracketFunction("count"));
	}

	@Test
	public void testInvalidDupeNameBracketFunction()
	{
		Function count = getPseudoFunction("Count");
		library.addBracketFunction(count);
		try
		{
			Function pseudoFunction = getPseudoFunction("COUNT");
			library.addBracketFunction(pseudoFunction);
			fail("Should not have been able to add function twice");
		}
		catch (IllegalArgumentException e)
		{
			//Yep, but make sure it didn't overwrite
			assertEquals(count, library.getBracketFunction("count"));
		}
		try
		{
			Function pseudoFunction = getPseudoFunction("count");
			library.addBracketFunction(pseudoFunction);
			fail("Should not have been able to add function twice, regardless of case");
		}
		catch (IllegalArgumentException e)
		{
			//Yep, but make sure it didn't overwrite
			assertEquals(count, library.getBracketFunction("count"));
		}
	}

	@Test
	public void testCrossLeakage()
	{
		Function abs = getPseudoFunction("Abs");
		library.addFunction(abs);
		//case insensitive
		assertEquals(abs, library.getFunction("abs"));
		assertNull(library.getBracketFunction("abs"));
		Function count = getPseudoFunction("Count");
		library.addBracketFunction(count);
		assertEquals(count, library.getBracketFunction("count"));
		assertNull(library.getFunction("count"));
	}

	@Test
	public void testAllowOverlap()
	{
		Function fabs = getPseudoFunction("Abs");
		library.addFunction(fabs);
		//case insensitive
		assertEquals(fabs, library.getFunction("abs"));
		assertNull(library.getBracketFunction("abs"));
		Function babs = getPseudoFunction("Abs");
		library.addBracketFunction(babs);
		assertEquals(babs, library.getBracketFunction("abs"));
		assertEquals(fabs, library.getFunction("abs"));
	}

	private Function getPseudoFunction(final String name)
	{
		return new Function()
		{

			@Override
			public String getFunctionName()
			{
				return name;
			}

			@Override
			public Boolean isStatic(StaticVisitor visitor, Node[] args)
			{
				return null;
			}

			@Override
			public void allowArgs(SemanticsVisitor visitor, Node[] args, FormulaSemantics fs)
			{
			}

			@Override
			public Double evaluate(EvaluateVisitor visitor, Node[] args, Class<?> assertedFormat)
			{
				return null;
			}

			@Override
			public void getDependencies(DependencyVisitor visitor,
				Class<?> assertedFormat, Node[] args)
			{
			}
		};
	}
}
