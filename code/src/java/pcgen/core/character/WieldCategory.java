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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
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
	private final Map<String, String> switchMap = new HashMap<String, String>();
	private final Map<String, String> wcStepMap = new HashMap<String, String>();

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

	/** A map storing an integer key for the number of hands being used and
	 * a float value which sets the damage multiplier.  Values in the table
	 * are wrapped in Integer/Float objects.
	 */
	private Map<Integer, Float> damageMultipliers = new HashMap<Integer, Float>();

	/**
	 * New constructor
	 * @param aName The name of the category (e.g. Light)
	 */
	public WieldCategory(final String aName)
	{
		name = aName;
	}

	/**
	 * Set whether a weapon be used with weapon finesse Feat?
	 * @param aBool true means the weapon is available with the weapon finesse
	 * feat
	 */
	public void setFinessable(final boolean aBool)
	{
		finessBool = aBool;
	}

	/**
	 * Can the weapon be used with weapon finesse Feat.
	 * @return boolean true means the weapon is available with weapon finesse
	 */
	public boolean isFinessable()
	{
		return finessBool;
	}

	/**
	 * Minumum hands required to wield this category of weapon
	 * @param x The number of hands
	 */
	public void setHands(final int x)
	{
		hands = x;
	}

	/**
	 * Get the minumum hands required to wield this category of weapon.
	 * @return The number of hands
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
	 * @param eq The weapon to check
	 * @return object size
	 */
	public int getObjectSizeInt(final Equipment eq)
	{
		final int eqSize = eq.sizeInt();

		return eqSize + sizeDiff;
	}

	/**
	 * Number of size categories object size is different than Equip size
	 * @param x The number of categories
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
		wcStepMap.put(String.valueOf(aInt), aVal);
	}

	/**
	 * Get the WieldCategory adjusted for the size difference between the weapon
	 * and the PC.  This uses the 3.5 equipment sizes.
	 *
	 * @param aPC Player character to get the weild category for.
	 * @param eq Equipment to get the weild category for.
	 * @return The ajusted WieldCategory
	 */
	public WieldCategory adjustForSize(final PlayerCharacter aPC,
									   final Equipment eq)
	{
		if (aPC == null || eq == null || eq.get(ObjectKey.WIELD) == null)
		{
			return this;
		}

		// Check if we have a bonus that changes the weapons effective size
		// for wield purposes.
		SizeAdjustment oldEqSa = eq.getSizeAdjustment();
		if (aPC.sizeInt() != eq.sizeInt())
		{
			int aBump = 0;
			aBump += (int) aPC.getTotalBonusTo("WIELDCATEGORY", eq
					.getWieldName());
			aBump += (int) aPC.getTotalBonusTo("WIELDCATEGORY", "ALL");
			if (aBump != 0)
			{
				final int newSizeInt = eq.sizeInt() + aBump;
				final SizeAdjustment sadj = Globals.getContext().ref
						.getItemInOrder(SizeAdjustment.class, newSizeInt);
				eq.put(ObjectKey.SIZE, sadj);
			}
		}
		WieldCategory pcWCat = getSwitch(aPC, eq);
		eq.put(ObjectKey.SIZE, oldEqSa);
		return pcWCat;
	}

	private WieldCategory getSwitch(final PlayerCharacter aPC,
			final Equipment eq)
	{
		PrerequisiteParserInterface parser;
		try
		{
			parser = PreParserFactory.getInstance().getParser("VAR");
		}
		catch (PersistenceLayerException ple)
		{
			return this;
		}
		WieldCategory pcWCat = this;
		for (Iterator<String> pc = switchMap.keySet().iterator(); pc.hasNext();)
		{
			String aKey = pc.next();

			boolean invertResult = false;
			if (aKey.startsWith("!"))
			{
				invertResult = true;
				aKey = aKey.substring(1);
			}
			final String aType = aKey.substring(3, aKey.indexOf(":"));
			final String preVar = aKey.substring(aKey.indexOf(":") + 1);
			try
			{
				final Prerequisite prereq = parser.parse(aType, preVar,
					invertResult, false);
				if (PrereqHandler.passes(prereq, eq, aPC))
				{
					final String mappedCat = switchMap.get(aKey);
					WieldCategory wCat = SettingsHandler.getGame()
							.getWieldCategory(mappedCat);
					if (wCat != null)
					{
						pcWCat = wCat;
					}
				}
			}
			catch (PersistenceLayerException ple)
			{
				Logging.errorPrint(ple.getMessage(), ple);
			}
		}
		return pcWCat;
	}

	/**
	 * Wield Category step is used to figure a bonus to WIELDCATEGORY
	 * Thus it should always return the best possible wield category
	 * and never a "bad" wield category
	 * @param aBump
	 * @return weild category step
	 */
	public WieldCategory getWieldCategoryStep(int aBump)
	{
		final String aKey = Integer.toString(aBump);
		final String newWC = wcStepMap.get(aKey);

		if (newWC != null)
		{
			return SettingsHandler.getGame().getWieldCategory(newWC);
		}

		return this;
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

	/**
	 * Add a new Damage Mult entry.
	 * @param numHands Number of hands used to wield the weapon
	 * @param multiplier Amount to multiply STR damage by
	 */
	public void addDamageMult(int numHands, float multiplier)
	{
		damageMultipliers.put(numHands, multiplier);
	}

	/**
	 * Returns the STR damage multiplier for this wield category.
	 *
	 * @param numHands number of hands the weapon is wielded with
	 * @return float the multiplier
	 */
	public float getDamageMult(int numHands)
	{
		Float ret = damageMultipliers.get(numHands);
		if (ret == null)
		{
			return 0.0f;
		}
		return ret.floatValue();
	}
}
