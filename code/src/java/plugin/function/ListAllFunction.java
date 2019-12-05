/*
 * Copyright 2019 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.formatmanager.FormatUtilities;
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
import pcgen.base.util.ArrayUtilities;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.StringPClassUtil;
import pcgen.util.enumeration.View;

/**
 * This is a function that gets a list of objects from the game mode (context).
 * <p>
 * This function requires 1 and allows 2 arguments: (1) The Format name (2) String
 * representation of the view (optional)
 */
public class ListAllFunction implements FormulaFunction
{

    @Override
    public String getFunctionName()
    {
        return "ListAll";
    }

    @Override
    public Boolean isStatic(StaticVisitor visitor, Node[] args)
    {
        //This is a shortcut since allowArgs enforces ASTQuotString
        return true;
    }

    @Override
    public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
            FormulaSemantics semantics)
    {
        int argCount = args.length;
        if ((argCount < 1) || (argCount > 2))
        {
            throw new SemanticsFailureException("Function " + getFunctionName()
                    + " received incorrect # of arguments, expected: 1-2 got "
                    + args.length + ' ' + Arrays.asList(args));
        }
        if (!(args[0] instanceof ASTQuotString))
        {
            //Error
            throw new SemanticsFailureException(
                    "Parse Error: Invalid first argument: Must be a String");
        }

        //This will be a format name
        String formatName = ((ASTQuotString) args[0]).getText();
        Class<? extends Loadable> cl = StringPClassUtil.getClassFor(formatName);
        if (cl == null)
        {
            throw new SemanticsFailureException(
                    "Parse Error: Unable to understand Format: " + formatName);
        }
        LoadContext context = semantics.get(ManagerKey.CONTEXT);
        ReferenceManufacturer<? extends Loadable> refMfg =
                context.getReferenceContext().getManufacturer(cl);
        if (!CDOMObject.class.isAssignableFrom(refMfg.getManagedClass()))
        {
            throw new SemanticsFailureException(
                    "Parse Error: Unable to use non-CDOM Format: " + formatName);
        }

        if (argCount == 2)
        {
            if (!(args[1] instanceof ASTQuotString))
            {
                throw new SemanticsFailureException(
                        "Parse Error: Invalid second argument: Must be a String");
            }
            ASTQuotString typeNode = (ASTQuotString) args[1];
            String viewName = typeNode.getText();
            try
            {
                View.valueOf(viewName);
            } catch (IllegalArgumentException e)
            {
                throw new SemanticsFailureException(
                        "Parse Error: Invalid View: " + viewName, e);
            }
        }
        return context.getReferenceContext()
                .getFormatManager("ARRAY[" + formatName + "]");
    }

    @Override
    public Object evaluate(EvaluateVisitor visitor, Node[] args,
            EvaluationManager manager)
    {
        String formatName = (String) args[0].jjtAccept(visitor,
                manager.getWith(EvaluationManager.ASSERTED,
                        Optional.of(FormatUtilities.STRING_MANAGER)));
        LoadContext context = manager.get(ManagerKey.CONTEXT);
        Class<? extends Loadable> cl = StringPClassUtil.getClassFor(formatName);
        //checked in allowArgs
        @SuppressWarnings("unchecked")
        ReferenceManufacturer<? extends CDOMObject> refMfg =
                (ReferenceManufacturer<? extends CDOMObject>) context
                        .getReferenceContext().getManufacturer(cl);
        View view = View.ALL;
        if (args.length == 2)
        {
            ASTQuotString typeNode = (ASTQuotString) args[1];
            String viewName = typeNode.getText();
            view = View.valueOf(viewName);
        }
        return getAllArray(refMfg, view);
    }

    private <T extends CDOMObject> Object getAllArray(
            ReferenceManufacturer<T> refMfg, View view)
    {
        return refMfg.getAllObjects().stream()
                .filter(obj -> obj.getSafe(ObjectKey.VISIBILITY).isVisibleTo(view))
                .toArray(
                        ArrayUtilities.buildOfClass(refMfg.getManagedClass()));
    }

    @Override
    public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor,
            DependencyManager manager, Node[] args)
    {
        String format = ((ASTQuotString) args[0]).getText();
        AbstractReferenceContext refContext =
                manager.get(ManagerKey.CONTEXT).getReferenceContext();
        return Optional.of(refContext.getFormatManager(format));
    }

}
