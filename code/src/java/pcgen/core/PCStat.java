/*
 * PCStat.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on August 10, 2002, 11:58 PM
 */
package pcgen.core;

import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;

import java.util.StringTokenizer;

/**
 * <code>PCStat</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class PCStat extends PObject
{
	private String abbreviation = ""; // should be 3 characters all caps
	private String penaltyVar = "";
	private String statMod = "0"; // a formula defining this stat's modifier
	private int maxValue = 1000;
	private int minValue = 0;
	private int score = 0;
	private boolean rolled = true;

	public void setAbb(final String aString)
	{
		abbreviation = aString.toUpperCase();

		if (abbreviation.length() != 3)
		{
			Logging.errorPrint("Stat with ABB:" + abbreviation + " should be 3 characters long!");
		}
	}

	public String getAbb()
	{
		return abbreviation;
	}

	public void setBaseScore(final int x)
	{
		score = x;
	}

	public int getBaseScore()
	{
		return score;
	}

	public int getMaxValue()
	{
		return maxValue;
	}

	public int getMinValue()
	{
		return minValue;
	}

	public void setPenaltyVar(final String aString)
	{
		penaltyVar = aString;
	}

	public String getPenaltyVar()
	{
		return penaltyVar;
	}

	public void setStatMod(final String aString)
	{
		statMod = aString;
	}

	public void setStatRange(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		if (aTok.countTokens() == 2)
		{
			try
			{
				minValue = Integer.parseInt(aTok.nextToken());
				maxValue = Integer.parseInt(aTok.nextToken());
			}
			catch (NumberFormatException ignore)
			{
				//TODO: Should this really be ignored?
			}
		}
		else
		{
			Logging.errorPrint("Error in specified Stat range: " + aString);
		}
	}

	public void setRolled(final boolean b)
	{
		rolled = b;
	}

	public boolean isRolled()
	{
		return rolled;
	}

	@Override
	public PCStat clone()
	{
		try
		{
			return (PCStat) super.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
			return null;
		}
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer(30);
		sb.append("stat:").append(abbreviation).append(' ');
		sb.append("formula:").append(statMod).append(' ');
		sb.append("score:").append(score);
		if (!rolled)
		{
			sb.append(' ').append("rolled:").append(rolled);
		}

		return sb.toString();
	}

	String getStatMod()
	{
		return statMod;
	}
}
