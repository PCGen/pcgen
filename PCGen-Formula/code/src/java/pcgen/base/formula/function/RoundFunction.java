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
 * RoundFunction rounds the value of a Number to the nearest integer value. For
 * any value that is equally distant between two integers, the same rules apply
 * as those in java.lang.Math.round(double).
 */
public class RoundFunction extends AbstractUnaryFunction
{

	@Override
	public String getFunctionName()
	{
		return "ROUND";
	}

	/**
	 * Rounds the given argument to the nearest integer value. For any value
	 * that is equally distant between two integers, the same rules apply as
	 * those in java.lang.Math.round(double).
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected Number evaluate(Number n)
	{
		if (n instanceof Integer)
		{
			return n;
		}
		return Integer.valueOf((int) Math.round(n.doubleValue()));
	}

}
