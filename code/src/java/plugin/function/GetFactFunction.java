/*
 * Copyright 2016-7 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Arrays;
import java.util.Optional;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.formula.ManagerKey;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;

public class GetFactFunction implements FormulaFunction
{

    private static final Class<CDOMObject> CDOMOBJECT_CLASS = CDOMObject.class;

    @Override
    public String getFunctionName()
    {
        return "getFact";
    }

    @Override
    public Boolean isStatic(StaticVisitor visitor, Node[] args)
    {
        if (args.length == 3)
        {
            return (Boolean) args[1].jjtAccept(visitor, null);
        }
        return Boolean.TRUE;
    }

    @Override
    public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args, FormulaSemantics semantics)
    {
        if (args.length != 3)
        {
            throw new SemanticsFailureException("Function " + getFunctionName()
                    + " received incorrect # of arguments, expected: 3 got " + args.length + " " + Arrays.asList(args));
        }

        //Turn scope node into a scope name
        Node scopeNode = args[0];
        if (!(scopeNode instanceof ASTQuotString))
        {
            throw new SemanticsFailureException("Parse Error: Invalid Format Node: " + scopeNode.getClass().getName()
                    + " found in location requiring a" + " Static String (class cannot be evaluated)");
        }
        String formatName = ((ASTQuotString) scopeNode).getText();
        LoadContext context = semantics.get(ManagerKey.CONTEXT);
        FormatManager<?> formatManager = context.getManufacturer(formatName);
        FormatManager<?> objectFormat = (FormatManager<?>) args[1].jjtAccept(visitor,
                semantics.getWith(FormulaSemantics.ASSERTED, Optional.of(formatManager)));

        if (!formatManager.equals(objectFormat))
        {
            throw new SemanticsFailureException(
                    "Parse Error: Invalid Object Format: " + objectFormat.getIdentifierType()
                            + " found in a getFact call that asserted " + formatManager.getIdentifierType());
        }
        if (!CDOMOBJECT_CLASS.isAssignableFrom(objectFormat.getManagedClass()))
        {
            throw new SemanticsFailureException(
                    "Parse Error: Invalid Object Format: " + objectFormat + " is not capable of holding a Fact");
        }
        Node factNode = args[2];
        if (!(factNode instanceof ASTQuotString))
        {
            throw new SemanticsFailureException("Parse Error: Invalid Fact Node: " + factNode.getClass().getName()
                    + " found in location requiring a" + " Static String (class cannot be evaluated)");
        }
        String factName = ((ASTQuotString) factNode).getText();
        FactDefinition<?, ?> factDef = context.getReferenceContext()
                .silentlyGetConstructedCDOMObject(FactDefinition.class, formatName + " " + factName);
        if (factDef == null)
        {
            throw new SemanticsFailureException("Parse Error: Invalid Fact: " + factName + " is not a valid FACT name");
        }
        Class<?> usable = factDef.getUsableLocation();
        if (!usable.isAssignableFrom(objectFormat.getManagedClass()))
        {
            throw new SemanticsFailureException("Parse Error: Invalid Fact: " + factDef.getDisplayName() + " works on "
                    + usable + " but formula asserted it was in " + objectFormat.getIdentifierType());
        }
        return factDef.getFormatManager();
    }

    @Override
    public Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager manager)
    {
        String formatName = ((ASTQuotString) args[0]).getText();
        LoadContext context = manager.get(ManagerKey.CONTEXT);
        FormatManager<?> formatManager = context.getManufacturer(formatName);
        CDOMObject cdo = (CDOMObject) args[1].jjtAccept(visitor,
                manager.getWith(EvaluationManager.ASSERTED, Optional.of(formatManager)));
        String factName = ((ASTQuotString) args[2]).getText();
        FactKey<Object> fk = FactKey.valueOf(factName);
        return cdo.getResolved(fk);
    }

    @Override
    public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor, DependencyManager fdm, Node[] args)
    {
        String formatName = ((ASTQuotString) args[0]).getText();
        LoadContext context = fdm.get(ManagerKey.CONTEXT);
        FormatManager<?> formatManager = context.getManufacturer(formatName);
        args[1].jjtAccept(visitor, fdm.getWith(DependencyManager.ASSERTED, Optional.of(formatManager)));
        String factName = ((ASTQuotString) args[2]).getText();
        AbstractReferenceContext refContext = context.getReferenceContext();
        FactDefinition<?, ?> factDef =
                refContext.silentlyGetConstructedCDOMObject(FactDefinition.class, formatName + " " + factName);
        return Optional.of(factDef.getFormatManager());
    }
}
