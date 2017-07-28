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

import pcgen.base.format.StringManager;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;
import pcgen.cdom.formula.ManagerKey;

/**
 * This is a function that gets an object of a given format from the String format of the
 * object.
 * 
 * This function requires 2 arguments: (1) The Format name (2) String representation of
 * the object
 */
public class GetFunction implements Function
{

	/**
	 * A constant referring to the String Class.
	 */
	private static final Class<String> STRING_CLASS = String.class;

	@Override
	public String getFunctionName()
	{
		return "Get";
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return (Boolean) args[1].jjtAccept(visitor, null);
	}

	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		int argCount = args.length;
		if (argCount != 2)
		{
			semantics.setInvalid("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 2 got " + args.length
				+ ' ' + Arrays.asList(args));
			return null;
		}

		FormatManager<?> rf;
		if (args[0] instanceof ASTQuotString)
		{
			//This will be a format then a table name;
			ASTQuotString qs = (ASTQuotString) args[0];
			rf = semantics.get(ManagerKey.CONTEXT).getReferenceContext()
				.getFormatManager(qs.getText());
		}
		else
		{
			//Error
			semantics.setInvalid("Parse Error: Invalid first argument: Must be a String");
			return null;
		}

		@SuppressWarnings("PMD.PrematureDeclaration")
		Object second = args[1].jjtAccept(visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, STRING_CLASS));
		if (!semantics.isValid())
		{
			return null;
		}
		if (!(second instanceof StringManager))
		{
			semantics.setInvalid("Parse Error: Invalid Object: " + second.getClass()
				+ " found in location requiring a String");
			return null;
		}

		return rf;
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		String firstArg = (String) args[0].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, null));
		String stringFormat = (String) args[1].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, STRING_CLASS));
		return manager.get(ManagerKey.CONTEXT).getReferenceContext()
			.getFormatManager(firstArg).convert(stringFormat);
	}

	@Override
	public void getDependencies(DependencyVisitor visitor, DependencyManager manager,
		Node[] args)
	{
		args[0].jjtAccept(visitor,
			manager.getWith(DependencyManager.ASSERTED, STRING_CLASS));
		args[1].jjtAccept(visitor,
			manager.getWith(DependencyManager.ASSERTED, STRING_CLASS));
	}

}
