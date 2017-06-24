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
package plugin.function;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.core.Equipment;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;

import junit.framework.TestCase;
import org.junit.Test;

public class DropIntoContextFunctionTest extends AbstractFormulaTestCase
{

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		SimpleFormatManagerLibrary formatLibrary = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(formatLibrary);
		getFunctionLibrary().addFunction(new DropIntoContext());
		getOperatorLibrary().addAction(new NumberMinus());
	}

	@Test
	public void testInvalidWrongArg()
	{
		String formula = "dropIntoContext(\"EQUIPMENT\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
		String s = "dropIntoContext(\"EQUIPMENT\", \"Foo\", 4, 5)";
		SimpleNode simpleNode = TestUtilities.doParse(s);
		isNotValid(s, simpleNode, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat1()
	{
		String formula = "dropIntoContext(3,\"EquipKey\",3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat2()
	{
		String formula = "dropIntoContext(\"EQUIPMENT\",3,3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat3()
	{
		String formula =
				"dropIntoContext(\"EQUIPMENT\", \"EquipKey\",\"Stuff\")";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics
			(getFormulaManager(), getGlobalScope(), null);
		Object result = semanticsVisitor.visit(node, semantics);
		if (semantics.isValid() && (result instanceof Number))
		{
			TestCase.fail(
				"Expected Invalid Formula: " + formula + " but was valid");
		}
	}

	@Test
	public void testBasic()
	{
		VariableLibrary vl = getVariableLibrary();
		LegalScope equipScope = getScopeLibrary().getScope("EQUIPMENT");
		vl.assertLegalVariableID("LocalVar", equipScope, numberManager);

		String formula =
				"dropIntoContext(\"EQUIPMENT\",\"EquipKey\",LocalVar)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(
			getFormulaManager(), getGlobalScope(), null);
		semanticsVisitor.visit(node, semantics);
		if (!semantics.isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: " + semantics.getReport());
		}
		isStatic(formula, node, false);
		Equipment equip = new Equipment();
		equip.setName("EquipKey");
		ScopeInstance scopeInst = getInstanceFactory().get("EQUIPMENT", equip);
		VariableID varID = vl.getVariableID(scopeInst, "LocalVar");
		getVariableStore().put(varID, 2);
		LoadContext context = new RuntimeLoadContext(new RuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		context.getReferenceContext().importObject(equip);
		evaluatesTo(formula, node, 2, context);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
	}

	@Test
	public void testDynamic()
	{
		VariableLibrary vl = getVariableLibrary();
		LegalScope equipScope = getScopeLibrary().getScope("EQUIPMENT");
		LegalScope globalScope = getScopeLibrary().getScope("Global");
		vl.assertLegalVariableID("LocalVar", equipScope, numberManager);
		vl.assertLegalVariableID("EquipVar", globalScope, stringManager);

		String formula =
				"dropIntoContext(\"EQUIPMENT\",EquipVar,LocalVar)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(
			getFormulaManager(), getGlobalScope(), null);
		semanticsVisitor.visit(node, semantics);
		if (!semantics.isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: " + semantics.getReport());
		}
		isStatic(formula, node, false);
		Equipment equip = new Equipment();
		equip.setName("EquipKey");
		Equipment equipalt = new Equipment();
		equipalt.setName("EquipAlt");
		ScopeInstance scopeInste = getInstanceFactory().get("EQUIPMENT", equip);
		VariableID varIDe = vl.getVariableID(scopeInste, "LocalVar");
		getVariableStore().put(varIDe, 2);
		ScopeInstance scopeInsta = getInstanceFactory().get("EQUIPMENT", equipalt);
		VariableID varIDa = vl.getVariableID(scopeInsta, "LocalVar");
		getVariableStore().put(varIDa, 3);
		ScopeInstance globalInst = getInstanceFactory().getGlobalInstance("Global");
		VariableID varIDq = vl.getVariableID(globalInst, "EquipVar");
		getVariableStore().put(varIDq, "EquipKey");
		LoadContext context = new RuntimeLoadContext(new RuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		context.getReferenceContext().importObject(equip);
		context.getReferenceContext().importObject(equipalt);
		evaluatesTo(formula, node, 2, context);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
		getVariableStore().put(varIDq, "EquipAlt");
		evaluatesTo(formula, node, 3, context);
	}
}
