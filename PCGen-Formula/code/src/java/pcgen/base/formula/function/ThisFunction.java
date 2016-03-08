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
package pcgen.base.formula.function;

import java.util.Arrays;

import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;

/**
 * ThisFunction returns the underlying object that the ScopeInstance is
 * representing. This allows formulas to effectively and simply reference their
 * location.
 */
public class ThisFunction implements Function
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFunctionName()
	{
		return "THIS";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return Boolean.TRUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		if (args.length != 0)
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Function " + getFunctionName()
					+ " received incorrect # of arguments, expected: 0 got "
					+ args.length + " " + Arrays.asList(args));
			return;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		Class<?> assertedFormat)
	{
		return visitor.getOwner();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getDependencies(DependencyVisitor visitor,
		Class<?> assertedFormat, Node[] args)
	{
	}

}
