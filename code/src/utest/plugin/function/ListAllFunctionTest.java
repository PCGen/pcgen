/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static plugin.function.testsupport.TestUtilities.doParse;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.util.FormatManager;
import pcgen.cdom.formula.ManagerKey;
import pcgen.core.Skill;
import plugin.function.testsupport.AbstractFormulaTestCase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test listAll() function in the new formula system
 */
public class ListAllFunctionTest extends AbstractFormulaTestCase
{

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        SimpleFormatManagerLibrary formatLibrary =
                new SimpleFormatManagerLibrary();
        FormatUtilities.loadDefaultFormats(formatLibrary);
        getFunctionLibrary().addFunction(new ListAllFunction());
    }

    @Test
    public void testInvalidWrongArg()
    {
        String formula = "listAll()";
        SimpleNode node = doParse(formula);
        isNotValid(formula, node);
        String s = "listAll(\"PC.SKILL\", \"Foo\", 4, 5)";
        SimpleNode simpleNode = doParse(s);
        isNotValid(s, simpleNode);
    }

    @Test
    public void testInvalidWrongFormat1()
    {
        String formula = "listAll(\"SKILL\",3)";
        SimpleNode node = doParse(formula);
        isNotValid(formula, node);
    }

    @Test
    public void testInvalidWrongFormat2()
    {
        String formula = "listAll(\"SKILL\",\"STUFF\")";
        SimpleNode node = doParse(formula);
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(null);
        assertThrows(SemanticsFailureException.class, () -> semanticsVisitor
                .visit(node, semantics.getWith(ManagerKey.CONTEXT, context)));
    }

    @Test
    public void testInvalidWrongFormat3()
    {
        String formula = "listAll(\"NOTAFORMAT\",\"DISPLAY\")";
        SimpleNode node = doParse(formula);
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(null);
        assertThrows(SemanticsFailureException.class, () -> semanticsVisitor
                .visit(node, semantics.getWith(ManagerKey.CONTEXT, context)));
    }

    @Test
    public void testValid()
    {
        String formula = "listAll(\"SKILL\")";
        SimpleNode node = doParse(formula);
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(null);
        @SuppressWarnings("unchecked")
        FormatManager<?> formatManager = (FormatManager<?>) semanticsVisitor
                .visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
        assertEquals(Skill[].class, formatManager.getManagedClass());
        isStatic(formula, node, true);
    }

    @Test
    public void testRoundRobin()
    {
        String formula = "listAll(\"SKILL\")";
        SimpleNode node = doParse(formula);
        Object rv =
                new ReconstructionVisitor().visit(node, new StringBuilder());
        assertEquals(formula, rv.toString());
    }

    @Test
    public void testBasic()
    {
        String formula = "listAll(\"SKILL\")";
        SimpleNode node = doParse(formula);

        EvaluationManager manager = generateManager();
        Skill[] expectedResult = new Skill[0];
        Skill[] result = (Skill[]) new EvaluateVisitor().visit(node, manager);
        Assertions.assertArrayEquals(expectedResult, result);

        Skill skill = new Skill();
        skill.setName("SkillKey");
        context.getReferenceContext().importObject(skill);

        expectedResult = new Skill[]{skill};
        result = (Skill[]) new EvaluateVisitor().visit(node, manager);
        Assertions.assertArrayEquals(expectedResult, result);

        Skill skill2 = new Skill();
        skill2.setName("AnotherSkillKey");
        context.getReferenceContext().importObject(skill2);

        expectedResult = new Skill[]{skill2, skill};
        result = (Skill[]) new EvaluateVisitor().visit(node, manager);
        Assertions.assertArrayEquals(expectedResult, result);
    }
}
