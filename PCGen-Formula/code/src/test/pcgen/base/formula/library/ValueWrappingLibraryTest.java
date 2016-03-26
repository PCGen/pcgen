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
package pcgen.base.formula.library;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;

public class ValueWrappingLibraryTest extends TestCase
{
	private SimpleFunctionLibrary underlying;
	private ValueWrappingLibrary library;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		underlying = new SimpleFunctionLibrary();
		library = new ValueWrappingLibrary(underlying, 1);
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
		catch (UnsupportedOperationException e)
		{
			//Yep, works, too
		}
	}

	@Test
	public void testCantAddFunction()
	{
		try
		{
			Function f = getPseudoFunction("Abs");
			library.addFunction(f);
			fail("Expected function add to be rejected");
		}
		catch (UnsupportedOperationException e)
		{
			//Yep
		}
	}

	@Test
	public void testSimpleFunctionGetDelegated()
	{
		Function abs = getPseudoFunction("Abs");
		underlying.addFunction(abs);
		//case insensitive
		assertEquals(abs, library.getFunction("ABS"));
		assertEquals(abs, library.getFunction("Abs"));
		assertEquals(abs, library.getFunction("abs"));
	}

	@Test
	public void testValueFunction()
	{
		Function value = getPseudoFunction("value");
		underlying.addFunction(value);
		assertTrue(value == underlying.getFunction("value"));
		assertFalse(value == library.getFunction("value"));
		assertEquals(3.3, underlying.getFunction("value").evaluate(null, null, null));
		assertEquals(1, library.getFunction("value").evaluate(null, null, null));
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
			public void allowArgs(SemanticsVisitor visitor, Node[] args,
				FormulaSemantics fs)
			{
			}

			@Override
			public Double evaluate(EvaluateVisitor visitor, Node[] args,
				Class<?> assertedFormat)
			{
				return 3.3;
			}

			@Override
			public void getDependencies(DependencyVisitor visitor,
				Class<?> assertedFormat, Node[] args)
			{
			}
		};
	}
}
