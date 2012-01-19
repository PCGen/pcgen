/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.PCClass;

public class MultiClassFacet
{
	private FavoredClassFacet favoredClassFacet;
	private HasAnyFavoredClassFacet hasAnyFavoredClassFacet;
	private ClassFacet classFacet;
	private SubClassFacet subClassFacet;

	public double getMultiClassXPMultiplier(CharID id)
	{
		HashSet<PCClass> unfavoredClasses = new HashSet<PCClass>();
		SortedSet<PCClass> favored = new TreeSet<PCClass>(
				CDOMObjectUtilities.CDOM_SORTER);
		favored.addAll(favoredClassFacet.getSet(id));
		SortedSet<PCClass> aList = favored;
		boolean hasAny = hasAnyFavoredClassFacet.contains(id, Boolean.TRUE);
		PCClass maxClass = null;
		PCClass secondClass = null;
		int maxClassLevel = 0;
		int secondClassLevel = 0;
		int xpPenalty = 0;
		double xpMultiplier = 1.0;

		for (PCClass pcClass : classFacet.getClassSet(id))
		{
			if (!pcClass.hasXPPenalty())
			{
				continue;
			}
			String subClassKey = subClassFacet.getSource(id, pcClass);
			PCClass evalClass = pcClass;
			if (subClassKey != null && !subClassKey.equals("None"))
			{
				evalClass = pcClass.getSubClassKeyed(subClassKey);
			}
			if (!aList.contains(evalClass))
			{
				unfavoredClasses.add(pcClass);

				int pcClassLevel = classFacet.getLevel(id, pcClass);
				if (pcClassLevel > maxClassLevel)
				{
					if (hasAny)
					{
						secondClassLevel = maxClassLevel;
						secondClass = maxClass;
					}

					maxClassLevel = pcClassLevel;
					maxClass = pcClass;
				}
				else if ((pcClassLevel > secondClassLevel) && (hasAny))
				{
					secondClassLevel = pcClassLevel;
					secondClass = pcClass;
				}
			}
		}

		if ((hasAny) && (secondClassLevel > 0))
		{
			maxClassLevel = secondClassLevel;
			unfavoredClasses.remove(maxClass);
			maxClass = secondClass;
		}

		if (maxClassLevel > 0)
		{
			unfavoredClasses.remove(maxClass);

			for (PCClass aClass : unfavoredClasses)
			{
				if ((maxClassLevel - (classFacet.getLevel(id, aClass))) > 1)
				{
					++xpPenalty;
				}
			}

			xpMultiplier = 1.0 - (xpPenalty * 0.2);

			if (xpMultiplier < 0)
			{
				xpMultiplier = 0;
			}
		}

		return xpMultiplier;
	}

	public void setFavoredClassFacet(FavoredClassFacet favoredClassFacet)
	{
		this.favoredClassFacet = favoredClassFacet;
	}

	public void setHasAnyFavoredClassFacet(
		HasAnyFavoredClassFacet hasAnyFavoredClassFacet)
	{
		this.hasAnyFavoredClassFacet = hasAnyFavoredClassFacet;
	}

	public void setClassFacet(ClassFacet classFacet)
	{
		this.classFacet = classFacet;
	}

	public void setSubClassFacet(SubClassFacet subClassFacet)
	{
		this.subClassFacet = subClassFacet;
	}

}
