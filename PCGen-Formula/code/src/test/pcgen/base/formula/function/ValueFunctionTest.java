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
package pcgen.base.formula.function;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;
import pcgen.base.util.FormatManager;

public class ValueFunctionTest extends AbstractFormulaTestCase
{

	@Test
	public void testInvalidTooManyArg()
	{
		String formula = "value(3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testInvalidNoInputFormat()
	{
		String formula = "value()";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, FormatUtilities.NUMBER_MANAGER, Optional.empty());
	}

	@Test
	public void testIntegerPositive()
	{
		String formula = "value()";
		SimpleNode node = TestUtilities.doParse(formula);
		Optional<FormatManager<?>> assertedFormat = Optional.empty();
		Objects.requireNonNull(assertedFormat);
		//My isValid due to need to set INPUT_FORMAT
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = getManagerFactory()
			.generateFormulaSemantics(getFormulaManager(), getInstanceFactory().getScope("Global"));
		semantics = semantics.getWith(FormulaSemantics.INPUT_FORMAT,
			Optional.of(FormatUtilities.NUMBER_MANAGER));
		semanticsVisitor.visit(node, semantics);
		//end my isValid
		isStatic(formula, node, false);
		EvaluationManager manager = generateManager().getWith(EvaluationManager.INPUT, 1);
		performEvaluation(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(1), manager);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}


	@Test
	public void testWeirdUsage()
	{
		String formula = "if(4==value(),5,6)";
		SimpleNode node = TestUtilities.doParse(formula);
		//My isValid due to need to set INPUT_FORMAT
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = getManagerFactory().generateFormulaSemantics(
			getFormulaManager(), getInstanceFactory().getScope("Global"));
		semantics = semantics.getWith(FormulaSemantics.INPUT_FORMAT,
			Optional.of(FormatUtilities.NUMBER_MANAGER));
		semanticsVisitor.visit(node, semantics);
		//end my isValid
		isStatic(formula, node, false);
		EvaluationManager manager = generateManager().getWith(EvaluationManager.INPUT, 1);
		performEvaluation(FormatUtilities.NUMBER_MANAGER, formula, node, Integer.valueOf(6), manager);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}
}
