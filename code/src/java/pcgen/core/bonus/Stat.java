/*
 * Stat.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision: 1.16 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/11/13 20:01:22 $
 *
 */
package pcgen.core.bonus;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.util.MissingObject;

/**
 * <code>Stat</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
final class Stat extends BonusObj
{
	private static final String[] bonusHandled =
		{
			"STAT"
		};
	private static final String[] bonusTags = { "BASESPELLSTAT", "BASESPELLKNOWNSTAT" };

	boolean parseToken(final String token)
	{
		for (int i = 0; i < bonusTags.length; ++i)
		{
			if (bonusTags[i].equals(token))
			{
				addBonusInfo(new Integer(i));

				return true;
			}
		}

		final int iStat;

		if (token.startsWith("CAST=") || token.startsWith("CAST."))
		{
			iStat = SettingsHandler.getGame().getStatFromAbbrev(token.substring(5));

			if (iStat >= 0)
			{
				addBonusInfo(new CastStat((PCStat) SettingsHandler.getGame().getUnmodifiableStatList().get(iStat)));

				return true;
			}
		}
		else
		{
			iStat = SettingsHandler.getGame().getStatFromAbbrev(token);

			if (iStat >= 0)
			{
				addBonusInfo(SettingsHandler.getGame().getUnmodifiableStatList().get(iStat));
			}
			else
			{
				final PCClass aClass = Globals.getClassNamed(token);

				if (aClass != null)
				{
					addBonusInfo(aClass);
				}
				else
				{
					addBonusInfo(new MissingObject(token));
				}
			}

			return true;
		}

		return false;
	}

	String unparseToken(final Object obj)
	{
		if (obj instanceof Integer)
		{
			return bonusTags[((Integer) obj).intValue()];
		}
		else if (obj instanceof CastStat)
		{
			return "CAST." + ((CastStat) obj).stat.getAbb();
		}
		else if (obj instanceof PCClass)
		{
			return ((PCClass) obj).getName();
		}
		else if (obj instanceof MissingObject)
		{
			return ((MissingObject) obj).getObjectName();
		}

		return ((PCStat) obj).getAbb();
	}

	private static class CastStat
	{
		final PCStat stat;

		CastStat(final PCStat argStat)
		{
			stat = argStat;
		}
	}

	String[] getBonusesHandled()
	{
		return bonusHandled;
	}
}
