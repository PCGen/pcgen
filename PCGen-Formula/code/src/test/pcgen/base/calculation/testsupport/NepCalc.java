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
package pcgen.base.calculation.testsupport;

import pcgen.base.calculation.AbstractNEPCalculation;
import pcgen.base.calculation.BasicCalculation;
import pcgen.base.formula.inst.ScopeInformation;

public final class NepCalc extends AbstractNEPCalculation
{
	private final Number n;

	public NepCalc(BasicCalculation calc, Number n)
	{
		super(calc);
		this.n = n;
	}

	@Override
	public Object process(Object input, ScopeInformation scopeInfo)
	{
		return this.getBasicCalculation().process(input, n);
	}

	@Override
	public String toString()
	{
		return "+" + n;
	}

	@Override
	public boolean equals(Object o)
	{
		return (o instanceof NepCalc) && ((NepCalc) o).n.equals(n);
	}

	@Override
	public int hashCode()
	{
		return n.hashCode();
	}

	@Override
	public String getInstructions()
	{
		return n.toString();
	}
}