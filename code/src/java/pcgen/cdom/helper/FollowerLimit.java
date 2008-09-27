/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.base.formula.Formula;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMSingleRef;

public class FollowerLimit
{

	private final CDOMSingleRef<CompanionList> ref;
	private final Formula f;

	public FollowerLimit(CDOMSingleRef<CompanionList> cl, Formula limit)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException(
					"Reference for FollowerLimit cannot be null");
		}
		if (limit == null)
		{
			throw new IllegalArgumentException(
					"Formula for FollowerLimit cannot be null");
		}
		ref = cl;
		f = limit;
	}

	public CDOMSingleRef<CompanionList> getCompanionList()
	{
		return ref;
	}

	public Formula getValue()
	{
		return f;
	}

	@Override
	public int hashCode()
	{
		return ref.hashCode() * 31 + f.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof FollowerLimit)
		{
			FollowerLimit other = (FollowerLimit) o;
			return ref.equals(other.ref) && f.equals(other.f);
		}
		return false;
	}

}
