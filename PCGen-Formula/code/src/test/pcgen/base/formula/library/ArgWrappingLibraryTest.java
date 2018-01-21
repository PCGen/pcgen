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

import org.junit.Test;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.inst.SimpleFunctionLibrary;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.util.FormatManager;

public class ArgWrappingLibraryTest extends AbstractFormulaTestCase
{
	private SimpleFunctionLibrary underlying;
	private ArgWrappingLibrary library;
	private Node[] args;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		underlying = new SimpleFunctionLibrary();
		ASTNum zero = new ASTNum(0);
		zero.setToken("0");
		args = new Node[]{zero};
		ASTNum onenode = new ASTNum(0);
		onenode.setToken("1");
		Node[] masterargs = {onenode};
		library = new ArgWrappingLibrary(underlying, masterargs);
	}

	@Test
	public void testInvalidNull()
	{
		try
		{
			library.addFunction(null);
			fail("Expected null function to be rejected");
		}
		catch (IllegalArgumentException | UnsupportedOperationException e)
		{
			//Yep
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
		Function arg = getPseudoFunction("arg");
		underlying.addFunction(arg);
		assertTrue(arg == underlying.getFunction("arg"));
		assertFalse(arg == library.getFunction("arg"));
		EvaluateVisitor visitor = new EvaluateVisitor();
		assertEquals(3.3, underlying.getFunction("arg").evaluate(visitor, args, null));
		assertEquals(1, library.getFunction("arg").evaluate(visitor, args, null));
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
			public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
				FormulaSemantics fs)
			{
				return null;
			}

			@Override
			public Double evaluate(EvaluateVisitor visitor, Node[] args,
				EvaluationManager manager)
			{
				return 3.3;
			}

			@Override
			public FormatManager<?> getDependencies(DependencyVisitor visitor,
				DependencyManager manager, Node[] args)
			{
				return null;
			}
		};
	}
}
