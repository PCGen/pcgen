/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.formula.ManagerKey;
import pcgen.rules.context.AbstractReferenceContext;

/**
 * This is a function that gets an object of a given format from the String format of the
 * object.
 * <p>
 * This function requires 2 arguments: (1) The Format name (2) String representation of
 * the object
 */
public class GetFunction implements FormulaFunction
{

    @Override
    public String getFunctionName()
    {
        return "Get";
    }

    @Override
    public Boolean isStatic(StaticVisitor visitor, Node[] args)
    {
        //This is a shortcut since allowArgs enforces both are ASTQuotString
        return true;
    }

    @Override
    public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args, FormulaSemantics semantics)
    {
        int argCount = args.length;
        if (argCount != 2)
        {
            throw new SemanticsFailureException("Function " + getFunctionName()
                    + " received incorrect # of arguments, expected: 2 got " + args.length + ' ' + Arrays.asList(args));
        }
        if (!(args[0] instanceof ASTQuotString))
        {
            //Error
            throw new SemanticsFailureException("Parse Error: Invalid first argument: Must be a String");
        }
        if (!(args[1] instanceof ASTQuotString))
        {
            //Error
            throw new SemanticsFailureException("Parse Error: Invalid first argument: Must be a String");
        }

        //This will be a format
        return semantics.get(ManagerKey.CONTEXT).getReferenceContext()
                .getFormatManager(((ASTQuotString) args[0]).getText());
    }

    @Override
    public Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager manager)
    {
        @SuppressWarnings("PMD.PrematureDeclaration")
        String format =
                (String) args[0].jjtAccept(visitor, manager.getWith(EvaluationManager.ASSERTED, Optional.empty()));
        String stringRepresentation = (String) args[1].jjtAccept(visitor,
                manager.getWith(EvaluationManager.ASSERTED, Optional.of(FormatUtilities.STRING_MANAGER)));
        return manager.get(ManagerKey.CONTEXT).getReferenceContext().getFormatManager(format)
                .convert(stringRepresentation);
    }

    @Override
    public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor, DependencyManager manager, Node[] args)
    {
        @SuppressWarnings("PMD.PrematureDeclaration")
        String format = ((ASTQuotString) args[0]).getText();
        @SuppressWarnings("PMD.PrematureDeclaration")
        String stringRepresentation = ((ASTQuotString) args[1]).getText();
        AbstractReferenceContext refContext = manager.get(ManagerKey.CONTEXT).getReferenceContext();
        FormatManager<?> formatManager = refContext.getFormatManager(format);
        Indirect<?> reference = formatManager.convertIndirect(stringRepresentation);
        manager.get(ManagerKey.REFERENCES).put(reference);
        return Optional.of(formatManager);
    }

}
