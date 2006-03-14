/*
 * WieldCategory.java
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
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on November 21, 2003, 11:26 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.character;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.Logging;

/**
 * <code>WieldCategory.java</code>
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 */
public final class WieldCategory
{
	private final Map switchMap = new HashMap();
	private final Map wcStepMap = new HashMap();

	/*
	 * WieldCategory contains the following:
	 * Hands: Minimum hands required to wield this category of weapon
	 * Finessable: Can this weapon be used with weapon finesse feat?
	 * Damage Multiplier: Multiplier to damage based on hands used
	 * PREVAR and SWITCH map: If meet PREVAR, then switch category
	 */
	private String name = "";
	private boolean finessBool;
	private int hands = 999;
	private int sizeDiff;

	/**
	 * New constructor
	 * @param aName
	 */
	public WieldCategory(final String aName)
	{
		name = aName;
	}

	/**
	 * Set whether a weapon be used with weapon finesse Feat?
	 * @param aBool
	 */
	public void setFinessable(final boolean aBool)
	{
		finessBool = aBool;
	}

	/**
	 * Can the weapon be used with weapon finesse Feat.
	 * @return boolean
	 */
	public boolean isFinessable()
	{
		return finessBool;
	}

	/**
	 * Minumum hands required to wield this category of weapon
	 * @param x
	 */
	public void setHands(final int x)
	{
		hands = x;
	}

	/**
	 * Get the minumum hands required to wield this category of weapon.
	 * @return hands
	 */
	public int getHands()
	{
		return hands;
	}

	/**
	 * Get the name of wield category.
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get object size, equip size + size diff.
	 * @param eq
	 * @return object size
	 */
	public int getObjectSizeInt(final Equipment eq)
	{
		final int eqSize = eq.sizeInt();

		return eqSize + sizeDiff;
	}

	/**
	 * Number of size categories object size is different than Equip size
	 * @param x
	 **/
	public void setSizeDiff(final int x)
	{
		sizeDiff = x;
	}

	/**
	 * Map of Steps up or down the wield category chain
	 * @param aInt
	 * @param aVal
	 **/
	public void setWCStep(final int aInt, final String aVal)
	{
		final String aKey = (new Integer(aInt)).toString();
		wcStepMap.put(aKey, aVal);
	}

	/**
	 * Get the wield category name.
	 * @param aPC Player character to get the weild category for.
	 * @param eq Equipment to get the weild category for.
	 * @return wield category name.
	 */
	public String getWieldCategory(final PlayerCharacter aPC, final Equipment eq)
	{
		if (aPC == null || eq == null)
		{
			return name;
		}

		try
		{
			final PrerequisiteParserInterface parser = PreParserFactory.getInstance().getParser("VAR");
			for (Iterator pc = switchMap.keySet().iterator(); pc.hasNext();)
			{
				String aKey = (String) pc.next();
	
	
				boolean invertResult=false;
				if (aKey.startsWith("!")) {
					invertResult = true;
					aKey = aKey.substring(1);
				}
				final String aType = aKey.substring(3, aKey.indexOf(":"));
				final String preVar = aKey.substring(aKey.indexOf(":") + 1);
	
				try
				{
					final Prerequisite prereq = parser.parse(aType, preVar, invertResult, false);
					if (PrereqHandler.passes(prereq, eq, aPC))
					{
						return (String) switchMap.get(aKey);
					}
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
		}
		catch(PersistenceLayerException ple) {
			Logging.errorPrint(ple.getMessage(), ple);
		}

		return name;
	}

	/**
	 * Wield Category step is used to figure a bonus to WIELDCATEGORY
	 * Thus it should always return the best possible wield category
	 * and never a "bad" wield category
	 * @param aBump
	 * @param wieldString
	 * @param aSizeDiff
	 * @param aHands
	 * @return weild category step
	 **/
	public String getWieldCategoryStep(int aBump, final String wieldString, final int aSizeDiff, final boolean aHands)
	{
		// this structure is to chech if the size of the weapon is compensated by the bonus
		if ("Light".equals(wieldString) && aSizeDiff > -aBump + 2)
		{
			return name;
		}
		else if ("OneHanded".equals(wieldString) && aSizeDiff > -aBump + 1)
		{
			return name;
		}
		else if ("TwoHanded".equals(wieldString) && aSizeDiff > -aBump && !aHands)
		{
			return name;
		}
		else if (aHands)
		{
			if (aSizeDiff + aBump == 0)
			{
				// in this case a hands weapon (bastard sword) can be wielded only in twohanded mode
				aBump++;
			}
			else if (aSizeDiff + aBump > 0)
			{
				// in this case the object is too large
				return name;
			}
			else {
				// in this case the weapon can be wielded as a onehanded
			}
		}

		final String aKey = Integer.toString(aBump);
		final String newWC = (String) wcStepMap.get(aKey);

		if (newWC != null)
		{
			return newWC;
		}

		return name;
	}

	/**
	 * Map of PREVAR and wield category to switch to
	 * @param aKey
	 * @param aVal
	 */
	public void addSwitchMap(final String aKey, final String aVal)
	{
		switchMap.put(aKey, aVal);
	}
}
