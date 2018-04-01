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

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.formula.scope.GlobalScope;
import pcgen.core.Skill;
import pcgen.rules.context.VariableContext;
import plugin.function.testsupport.AbstractFormulaTestCase;

/**
 * Test getOther() function in the new formula system
 */
public class GetOtherFunctionTest extends AbstractFormulaTestCase
{

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		SimpleFormatManagerLibrary formatLibrary = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(formatLibrary);
		getFunctionLibrary().addFunction(new GetOtherFunction());
		getOperatorLibrary().addAction(new NumberMinus());
	}

	@Test
	public void testInvalidWrongArg()
	{
		String formula = "getOther(\"PC.SKILL\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
		String s = "getOther(\"PC.SKILL\", \"Foo\", 4, 5)";
		SimpleNode simpleNode = TestUtilities.doParse(s);
		isNotValid(s, simpleNode, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat1()
	{
		String formula = "getOther(3,\"SkillKey\",3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat2()
	{
		String formula = "getOther(\"PC.SKILL\",3,3)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
		if (semantics.isValid())
		{
			TestCase.fail(
				"Expected Invalid Formula: " + formula + " but was valid");
		}
	}

	@Test
	public void testInvalidWrongFormat3()
	{
		String formula =
				"getOther(\"PC.SKILL\", \"SkillKey\",\"Stuff\")";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		Object result = semanticsVisitor.visit(node,
			semantics.getWith(ManagerKey.CONTEXT, context));
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
		LegalScope skillScope = context.getVariableContext().getScope("PC.SKILL");
		vl.assertLegalVariableID("LocalVar", skillScope, numberManager);

		String formula =
				"getOther(\"PC.SKILL\",\"SkillKey\",LocalVar)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
		if (!semantics.isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: " + semantics.getReport());
		}
		isStatic(formula, node, false);
		Skill skill = new Skill();
		skill.setName("SkillKey");
		ScopeInstance scopeInst =
				getFormulaManager().getScopeInstanceFactory().get("PC.SKILL", skill);
		VariableID varID = vl.getVariableID(scopeInst, "LocalVar");
		getVariableStore().put(varID, 2);
		context.getReferenceContext().importObject(skill);
		evaluatesTo(formula, node, 2);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
	}

	@Test
	public void testDynamic()
	{
		VariableLibrary vl = getVariableLibrary();
		VariableContext variableContext = context.getVariableContext();
		LegalScope skillScope = variableContext.getScope("PC.SKILL");
		LegalScope globalScope = variableContext.getScope(GlobalScope.GLOBAL_SCOPE_NAME);
		vl.assertLegalVariableID("LocalVar", skillScope, numberManager);
		vl.assertLegalVariableID("SkillVar", globalScope, context.getManufacturer("SKILL"));

		String formula =
				"getOther(\"PC.SKILL\",SkillVar,LocalVar)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
		if (!semantics.isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: " + semantics.getReport());
		}
		isStatic(formula, node, false);
		Skill skill = new Skill();
		skill.setName("SkillKey");
		Skill skillalt = new Skill();
		skillalt.setName("SkillAlt");
		ScopeInstanceFactory scopeInstanceFactory =
				getFormulaManager().getScopeInstanceFactory();
		ScopeInstance scopeInste = scopeInstanceFactory.get("PC.SKILL", skill);
		VariableID varIDe = vl.getVariableID(scopeInste, "LocalVar");
		getVariableStore().put(varIDe, 2);
		ScopeInstance scopeInsta = scopeInstanceFactory.get("PC.SKILL", skillalt);
		VariableID varIDa = vl.getVariableID(scopeInsta, "LocalVar");
		getVariableStore().put(varIDa, 3);
		ScopeInstance globalInst =
				scopeInstanceFactory.getGlobalInstance(GlobalScope.GLOBAL_SCOPE_NAME);
		VariableID varIDq = vl.getVariableID(globalInst, "SkillVar");
		getVariableStore().put(varIDq, skill);
		context.getReferenceContext().importObject(skill);
		context.getReferenceContext().importObject(skillalt);
		evaluatesTo(formula, node, 2);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
		getVariableStore().put(varIDq, skillalt);
		evaluatesTo(formula, node, 3);
	}
}
