/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.inst;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.Loadable;
import pcgen.core.bonus.BonusObj;

public class EqSizePenalty implements Loadable
{

	private URI sourceURI;
	private String penaltyName;
	private List<BonusObj> bonusList;

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	public void setName(String name)
	{
		penaltyName = name;
	}

	public String getDisplayName()
	{
		return penaltyName;
	}

	public void setKeyName(String key)
	{
		setName(key);
	}

	public String getKeyName()
	{
		return getDisplayName();
	}

	public String getLSTformat()
	{
		return getKeyName();
	}

	public boolean isInternal()
	{
		return false;
	}

	public boolean isType(String type)
	{
		return false;
	}

	public void addBonus(BonusObj bon)
	{
		if (bonusList == null)
		{
			bonusList = new ArrayList<BonusObj>();
		}
		bonusList.add(bon);
	}

	public Collection<BonusObj> getBonuses()
	{
		if (bonusList == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(bonusList);
	}

}
