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
package pcgen.cdom.content;

import java.net.URI;

import pcgen.cdom.base.Loadable;

public class BonusSpellInfo implements Loadable
{

	private URI sourceURI;
	private int statRange;
	private int spellLevel;
	private int statScore;

	@Override
	public URI getSourceURI()
	{
		return sourceURI;
	}

	@Override
	public void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	@Override
	public void setName(String name)
	{
		try
		{
			int intValue = Integer.parseInt(name);
			if (intValue < 1)
			{
				throw new IllegalArgumentException("Name must be an integer >= 1");
			}
			spellLevel = intValue;
		}
		catch (NumberFormatException nfe)
		{
			throw new IllegalArgumentException("Name must be an integer, found: " + name, nfe);
		}
	}

	@Override
	public String getDisplayName()
	{
		return String.valueOf(spellLevel);
	}

	@Override
	public String getKeyName()
	{
		return getDisplayName();
	}

	@Override
	public boolean isInternal()
	{
		return false;
	}

	@Override
	public boolean isType(String type)
	{
		return false;
	}

	public void setStatRange(int range)
	{
		statRange = range;
	}

	public void setStatScore(int score)
	{
		statScore = score;
	}

	public boolean isValid()
	{
		return (spellLevel > 0) && (statRange > 0) && (statScore > 0);
	}

	public int getStatScore()
	{
		return statScore;
	}

	public int getStatRange()
	{
		return statRange;
	}

}
