/*
 * PointBuyCost.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 15, 2005, 11:40 AM
 *
 * $Id$
 */
package pcgen.core;

import pcgen.core.prereq.Prerequisite;

import java.util.ArrayList;
import java.util.List;


/**
 * <code>PointBuyCost</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class PointBuyCost
{
	private int statValue = 0;
	private int statCost = 0;
	private ArrayList<Prerequisite> preReqList = null;

	public PointBuyCost(final int argStatValue)
	{
		statValue = argStatValue;
	}

	public int getStatValue()
	{
		return statValue;
	}

	public void setStatCost(final int argStatCost)
	{
		statCost = argStatCost;
	}

	public int getStatCost()
	{
		return statCost;
	}

	public final void clearPreReq()
	{
		preReqList = null;
	}

	public final void addPreReq(final Prerequisite preReq)
	{
		addPreReq(preReq, -1);
	}

	public final void addPreReq(final Prerequisite preReq, final int anInt)
	{
		if ("clear".equals(preReq.getKind()))
		{
			preReqList = null;
		}
		else
		{
			if (preReqList == null)
			{
				preReqList = new ArrayList<Prerequisite>();
			}
			if (anInt > 0)
			{
				preReq.setLevelQualifier(anInt);
			}
			preReqList.add(preReq);
		}
	}

	public final int getPreReqCount()
	{
		if (preReqList == null)
		{
			return 0;
		}

		return preReqList.size();
	}

	public final List<Prerequisite> getPreReqList()
	{
		return preReqList;
	}

	public final Prerequisite getPreReq(final int i)
	{
		return preReqList.get(i);
	}

}
