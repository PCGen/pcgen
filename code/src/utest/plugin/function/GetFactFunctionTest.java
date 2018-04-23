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
import pcgen.base.format.StringManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.util.BasicIndirect;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.formula.scope.GlobalScope;
import pcgen.core.Skill;
import pcgen.util.enumeration.Visibility;
import plugin.function.testsupport.AbstractFormulaTestCase;

public class GetFactFunctionTest extends AbstractFormulaTestCase
{

	private static final StringManager STRING_MANAGER = new StringManager();

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		SimpleFormatManagerLibrary formatLibrary = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(formatLibrary);
		getFunctionLibrary().addFunction(new GetFactFunction());
		getOperatorLibrary().addAction(new NumberMinus());
	}

	@Test
	public void testInvalidWrongArgCount()
	{
		String formula = "getFact(\"SKILL\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
		String s = "getFact(\"SKILL\", \"Foo\", 4, 5)";
		SimpleNode simpleNode = TestUtilities.doParse(s);
		isNotValid(s, simpleNode, numberManager, null);
	}

	@Test
	public void testInvalidWrongArgType()
	{
		LegalScope skillScope = context.getVariableContext().getScope("PC.SKILL");
		getVariableLibrary().assertLegalVariableID("LocalVar", skillScope, numberManager);
		String s = "getFact(\"SKILL\",\"SkillKey\",LocalVar)";
		SimpleNode simpleNode = TestUtilities.doParse(s);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		try
		{
			semanticsVisitor.visit(simpleNode,
				semantics.getWith(ManagerKey.CONTEXT, context));
			TestCase.fail("Expected Invalid Formula: " + s + " but was valid");
		}
		catch (SemanticsFailureException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidWrongFormat1()
	{
		String formula = "getFact(3,\"SkillKey\",3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat2()
	{
		String formula = "getFact(\"SKILL\",3,3)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		try
		{
			semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
			TestCase.fail("Expected Invalid Formula: " + formula + " but was valid");
		}
		catch (SemanticsFailureException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidWrongFormat3()
	{
		String formula =
				"getFact(\"SKILL\", \"SkillKey\",\"Stuff\")";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		try
		{
			Object result = semanticsVisitor.visit(node,
				semantics.getWith(ManagerKey.CONTEXT, context));
			if (result instanceof Number)
			{
				TestCase.fail("Expected Invalid Formula: " + formula + " but was valid");
			}
		}
		catch (SemanticsFailureException e)
		{
			//Expected
		}
	}

	@Test
	public void testBasic()
	{
		FactDefinition fd = new FactDefinition();
		fd.setName("SKILL.Stuff");
		fd.setFactName("Stuff");
		fd.setUsableLocation(Skill.class);
		fd.setFormatManager(STRING_MANAGER);
		fd.setVisibility(Visibility.HIDDEN);

		context.getReferenceContext().importObject(fd);
		String formula =
				"getFact(\"SKILL\",\"SkillKey\",\"Stuff\")";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
		isStatic(formula, node, true);
		Skill equip = new Skill();
		equip.setName("SkillKey");
		FactKey<String> fk = FactKey.getConstant("Stuff", STRING_MANAGER);
		equip.put(fk, new BasicIndirect(STRING_MANAGER, "Wow!"));

		context.getReferenceContext().importObject(equip);
		evaluatesTo(formula, node, "Wow!");
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
	}

	@Test
	public void testDynamic()
	{
		VariableLibrary vl = getVariableLibrary();
		LegalScope globalScope =
				context.getVariableContext().getScope(GlobalScope.GLOBAL_SCOPE_NAME);
		vl.assertLegalVariableID("SkillVar", globalScope,
			context.getManufacturer("SKILL"));

		FactDefinition fd = new FactDefinition();
		fd.setName("SKILL.Stuff");
		fd.setFactName("Stuff");
		fd.setUsableLocation(Skill.class);
		fd.setFormatManager(STRING_MANAGER);
		fd.setVisibility(Visibility.HIDDEN);
		context.getReferenceContext().importObject(fd);

		String formula =
				"getFact(\"SKILL\",SkillVar,\"Stuff\")";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(null);
		semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
		isStatic(formula, node, false);
		Skill skill = new Skill();
		skill.setName("SkillKey");
		Skill skillalt = new Skill();
		skillalt.setName("SkillAlt");
		ScopeInstance globalInst = getFormulaManager().getScopeInstanceFactory()
			.getGlobalInstance(GlobalScope.GLOBAL_SCOPE_NAME);
		VariableID varIDq = vl.getVariableID(globalInst, "SkillVar");
		getVariableStore().put(varIDq, skill);
		context.getReferenceContext().importObject(skill);
		context.getReferenceContext().importObject(skillalt);
		FactKey<String> fk = FactKey.getConstant("Stuff", STRING_MANAGER);
		skill.put(fk, new BasicIndirect(STRING_MANAGER, "Wow!"));
		skillalt.put(fk, new BasicIndirect(STRING_MANAGER, "Zers!"));

		evaluatesTo(formula, node, "Wow!");
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
		getVariableStore().put(varIDq, skillalt);
		evaluatesTo(formula, node, "Zers!");
	}
}
