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

import pcgen.base.calculation.BasicCalculation;
import pcgen.base.formula.base.OperatorAction;

public final class BasicCalc implements BasicCalculation
{
	private final OperatorAction oa;

	public BasicCalc(OperatorAction oa)
	{
		this.oa = oa;
	}

	@Override
	public String getIdentification()
	{
		return "Basic";
	}

	@Override
	public Class getVariableFormat()
	{
		return Number.class;
	}

	@Override
	public int getInherentPriority()
	{
		return 6;
	}

	@Override
	public Object process(Object previousValue, Object argument)
	{
		return oa.evaluate(previousValue, argument);
	}
}