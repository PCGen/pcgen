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
 * FloorFunction rounds the value of a Number to the next lowest integer value. The same
 * situations/rules apply as those in java.lang.Math.floor(double).
 */
public class FloorFunction extends AbstractUnaryFunction
{

	@Override
	public String getFunctionName()
	{
		return "FLOOR";
	}

	/**
	 * Rounds the given argument to the next lowest integer value. The same
	 * rules apply as those in java.lang.Math.floor(double).
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
		return Integer.valueOf((int) Math.floor(n.doubleValue()));
	}

}
