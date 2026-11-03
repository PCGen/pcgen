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
package pcgen.base.formula.function;

/**
 * AbsFunction returns the absolute value of the given argument. The same
 * conditions/rules apply as those in Java.lang.Math.abs(double).
 */
public class AbsFunction extends AbstractUnaryFunction
{

	@Override
	public String getFunctionName()
	{
		return "ABS";
	}

	/**
	 * Returns the absolute value of the given argument. The same
	 * conditions/rules apply as those in Java.lang.Math.abs(double).
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected Number evaluate(Number n)
	{
		if (n instanceof Integer)
		{
			return Math.abs(n.intValue());
		}
		return Math.abs(n.doubleValue());
	}

}
