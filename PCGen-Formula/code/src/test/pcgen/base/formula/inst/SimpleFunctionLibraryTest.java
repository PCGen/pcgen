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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

public class SimpleFunctionLibraryTest
{

	@Test
	public void testInvalidNull()
	{
		SimpleFunctionLibrary library = new SimpleFunctionLibrary();
		assertThrows(NullPointerException.class, () -> library.addFunction(null));
	}

	@Test
	public void testInvalidNullName()
	{
		SimpleFunctionLibrary library = new SimpleFunctionLibrary();
		assertThrows(NullPointerException.class, () -> library.addFunction(getPseudoFunction(null)));
	}

	@Test
	public void testSimpleFunctionSetGet()
	{
		SimpleFunctionLibrary library = new SimpleFunctionLibrary();
		FormulaFunction abs = getPseudoFunction("Abs");
		library.addFunction(abs);
		//case insensitive
		assertEquals(abs, library.getFunction("ABS"));
		assertEquals(abs, library.getFunction("Abs"));
		assertEquals(abs, library.getFunction("abs"));
	}

	@Test
	public void testInvalidDupeNameSimpleFunction()
	{
		SimpleFunctionLibrary library = new SimpleFunctionLibrary();
		FormulaFunction abs = getPseudoFunction("Abs");
		library.addFunction(abs);
		try
		{
			library.addFunction(getPseudoFunction("ABS"));
			fail("Should not have been able to add function twice");
		}
		catch (IllegalArgumentException e)
		{
			//Yep, but make sure it didn't overwrite
			assertEquals(abs, library.getFunction("ABS"));
		}
		try
		{
			library.addFunction(getPseudoFunction("abs"));
			fail("Should not have been able to add function twice, regardless of case");
		}
		catch (IllegalArgumentException e)
		{
			//Yep, but make sure it didn't overwrite
			assertEquals(abs, library.getFunction("ABS"));
		}
	}

	private FormulaFunction getPseudoFunction(final String name)
	{
		return new FormulaFunction()
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
			public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args, FormulaSemantics fs)
			{
				return null;
			}

			@Override
			public Double evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager manager)
			{
				return null;
			}

			@Override
			public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
				DependencyManager manager, Node[] args)
			{
				return Optional.empty();
			}
		};
	}
}
