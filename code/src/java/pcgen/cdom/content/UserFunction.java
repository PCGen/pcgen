/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.io.StringReader;
import java.util.Objects;

import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.library.GenericFunction;
import pcgen.base.formula.parse.FormulaParser;
import pcgen.base.formula.parse.ParseException;
import pcgen.base.formula.parse.SimpleNode;

/**
 * A UserFunction is a FormulaFunction that has been defined by the user via
 * data control. This is then made available to the Formula system as a custom
 * function, based on the given name (in UserContent).
 */
public class UserFunction extends UserContent
{

    /**
     * The underlying FormulaFunction for this UserFunction.
     */
    private FormulaFunction function;

    /**
     * The original Expression for this UserFuction.
     */
    private String origExpression;

    /**
     * Sets the Function for this UserFunction to the function defined by the
     * given UserExpression.
     *
     * @param expression The expression to be parsed into a Function.
     */
    public void setFunction(String expression)
    {
        Objects.requireNonNull(expression, "Cannot make formula from null String");
        origExpression = expression;
        try
        {
            SimpleNode root = new FormulaParser(new StringReader(expression)).query();
            function = new GenericFunction(getKeyName(), root);
        } catch (ParseException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public String getOriginalExpression()
    {
        return origExpression;
    }

    /**
     * Returns the FormulaFunction for this UserFunction.
     *
     * @return The FormulaFunction for this UserFunction
     */
    public FormulaFunction getFunction()
    {
        return function;
    }

    @Override
    public String getDisplayName()
    {
        return getKeyName();
    }
}
