/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Arrays;
import java.util.Optional;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.FormulaParserTreeConstants;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;
import pcgen.output.channel.ChannelUtilities;

/**
 * InputFunction is a function designed to allow pulling information from a channel (as
 * defined by the argument to the input function).
 */
public class InputFunction implements FormulaFunction
{

    /**
     * Returns the function name for this function. This is how it is called by
     * a user in a formula.
     */
    @Override
    public String getFunctionName()
    {
        return "INPUT";
    }

    @Override
    public final FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args, FormulaSemantics semantics)
    {
        int argCount = args.length;
        if (argCount != 1)
        {
            throw new SemanticsFailureException("Function " + getFunctionName()
                    + " received incorrect # of arguments, expected: 1 got " + args.length + ' ' + Arrays.asList(args));
        }
        //String node (name)
        Node inputNode = args[0];
        if (inputNode.getId() != FormulaParserTreeConstants.JJTQUOTSTRING)
        {
            throw new SemanticsFailureException("Parse Error: Invalid Value: " + ((SimpleNode) inputNode).getText()
                    + " found in " + inputNode.getClass().getName() + " found in location requiring a literal"
                    + " String (cannot be evaluated)");
        }
        String inputName = ((SimpleNode) inputNode).getText();
        String varName = ChannelUtilities.createVarName(inputName);
        VariableLibrary varLib = semantics.get(FormulaSemantics.FMANAGER).getFactory();
        LegalScope scope = semantics.get(FormulaSemantics.SCOPE);
        FormatManager<?> formatManager = varLib.getVariableFormat(scope, varName);
        if (formatManager == null)
        {
            throw new SemanticsFailureException("Input Channel: " + varName + " was not found");
        }
        return formatManager;
    }

    @Override
    public Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager manager)
    {
        String s = (String) args[0].jjtAccept(visitor, manager);
        return visitor.visitVariable(ChannelUtilities.createVarName(s), manager);
    }

    @Override
    public Boolean isStatic(StaticVisitor visitor, Node[] args)
    {
        //Since we are dependent on user input, we can't be static
        return Boolean.FALSE;
    }

    @Override
    public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor, DependencyManager fdm, Node[] args)
    {
        ASTQuotString inputName = (ASTQuotString) args[0];
        String varName = inputName.getText();
        return visitor.visitVariable(ChannelUtilities.createVarName(varName), fdm);
    }
}
