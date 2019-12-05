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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static plugin.function.testsupport.TestUtilities.doParse;

import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.core.Skill;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.VariableContext;
import plugin.function.testsupport.AbstractFormulaTestCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.FormatSupport;

/**
 * Test getOther() function in the new formula system
 */
public class GetOtherFunctionTest extends AbstractFormulaTestCase
{

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        SimpleFormatManagerLibrary formatLibrary = new SimpleFormatManagerLibrary();
        FormatUtilities.loadDefaultFormats(formatLibrary);
        AbstractReferenceContext refContext = context.getReferenceContext();
        refContext.constructNowIfNecessary(Skill.class, "NONE");
        FormatSupport.addNoneAsDefault(context, refContext.getManufacturer(Skill.class));
        getFunctionLibrary().addFunction(new GetOtherFunction());
        getOperatorLibrary().addAction(new NumberMinus());
    }

    @Test
    public void testInvalidWrongArg()
    {
        String formula = "getOther(\"PC.SKILL\")";
        SimpleNode node = doParse(formula);
        isNotValid(formula, node);
        String s = "getOther(\"PC.SKILL\", \"Foo\", 4, 5)";
        SimpleNode simpleNode = doParse(s);
        isNotValid(s, simpleNode);
    }

    @Test
    public void testInvalidWrongFormat1()
    {
        String formula = "getOther(3,\"SkillKey\",3)";
        SimpleNode node = doParse(formula);
        isNotValid(formula, node);
    }

    @Test
    public void testInvalidWrongFormat2()
    {
        String formula = "getOther(\"PC.SKILL\",3,3)";
        SimpleNode node = doParse(formula);
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(null);
        assertThrows(SemanticsFailureException.class,
                () -> semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context)));
    }

    @Test
    public void testInvalidWrongFormat3()
    {
        String formula =
                "getOther(\"PC.SKILL\", \"SkillKey\",\"Stuff\")";
        SimpleNode node = doParse(formula);
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(null);
        Object result = semanticsVisitor.visit(node,
                semantics.getWith(ManagerKey.CONTEXT, context));
        if (result instanceof Number)
        {
            fail(() -> "Expected Invalid Formula: " + formula + " but was valid");
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
        SimpleNode node = doParse(formula);
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(null);
        semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
        isStatic(formula, node, false);
        Skill skill = new Skill();
        skill.setName("SkillKey");
        ScopeInstance scopeInst =
                getFormulaManager().getScopeInstanceFactory().get("PC.SKILL", Optional.of(skill));
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
        LegalScope globalScope = variableContext.getScope(GlobalPCScope.GLOBAL_SCOPE_NAME);
        vl.assertLegalVariableID("LocalVar", skillScope, numberManager);
        vl.assertLegalVariableID("SkillVar", globalScope, context.getManufacturer("SKILL"));

        String formula =
                "getOther(\"PC.SKILL\",SkillVar,LocalVar)";
        SimpleNode node = doParse(formula);
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(null);
        semanticsVisitor.visit(node, semantics.getWith(ManagerKey.CONTEXT, context));
        isStatic(formula, node, false);
        Skill skill = new Skill();
        skill.setName("SkillKey");
        Skill skillalt = new Skill();
        skillalt.setName("SkillAlt");
        ScopeInstanceFactory scopeInstanceFactory =
                getFormulaManager().getScopeInstanceFactory();
        ScopeInstance scopeInste = scopeInstanceFactory.get("PC.SKILL", Optional.of(skill));
        VariableID varIDe = vl.getVariableID(scopeInste, "LocalVar");
        getVariableStore().put(varIDe, 2);
        ScopeInstance scopeInsta = scopeInstanceFactory.get("PC.SKILL", Optional.of(skillalt));
        VariableID varIDa = vl.getVariableID(scopeInsta, "LocalVar");
        getVariableStore().put(varIDa, 3);
        ScopeInstance globalInst =
                scopeInstanceFactory.getGlobalInstance(GlobalPCScope.GLOBAL_SCOPE_NAME);
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
